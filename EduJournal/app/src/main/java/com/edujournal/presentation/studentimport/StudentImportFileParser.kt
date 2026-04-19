package com.edujournal.presentation.studentimport

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.edujournal.utils.normalizeSpaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayInputStream
import java.io.StringReader
import java.nio.charset.Charset

data class ImportStudentRow(
    val firstName: String,
    val lastName: String,
    val middleName: String
)

sealed class StudentImportParseResult {
    data class Success(val students: List<ImportStudentRow>) : StudentImportParseResult()
    data class Error(val reason: String) : StudentImportParseResult()
}

object StudentImportFileParser {
    suspend fun parse(context: Context, uri: Uri): StudentImportParseResult =
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver
            val displayName = resolver.queryDisplayName(uri).orEmpty().lowercase()

            return@withContext try {
                val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw IllegalArgumentException("Не удалось прочитать файл")

                val rows = when {
                    displayName.endsWith(".csv") -> parseCsv(bytes)
                    displayName.endsWith(".xlsx") -> parseXlsx(bytes)
                    displayName.endsWith(".xls") -> throw IllegalArgumentException(
                        "Формат .xls не поддерживается. Сохраните файл как .xlsx."
                    )
                    else -> {
                        runCatching { parseXlsx(bytes) }
                            .getOrElse { parseCsv(bytes) }
                    }
                }

                StudentImportParseResult.Success(rows)
            } catch (e: Exception) {
                StudentImportParseResult.Error(
                    reason = e.message ?: "Invalid file format"
                )
            }
        }

    private fun parseCsv(bytes: ByteArray): List<ImportStudentRow> {
        val content = decodeCsvContent(bytes)
        if (content.isBlank()) return emptyList()

        val firstLine = content.lineSequence().firstOrNull().orEmpty()
        val delimiter = detectDelimiter(firstLine)

        val csvFormat = CSVFormat.DEFAULT.builder()
            .setDelimiter(delimiter)
            .setTrim(true)
            .setIgnoreSurroundingSpaces(true)
            .build()

        val rows = csvFormat.parse(StringReader(content))
            .map { record ->
                val count = maxOf(record.size(), 3)
                (0 until count).map { index ->
                    if (index < record.size()) record.get(index).normalizeSpaces() else ""
                }
            }
            .filter { cells -> cells.any { it.isNotBlank() } }

        return mapRowsToStudents(rows)
    }

    private fun parseXlsx(bytes: ByteArray): List<ImportStudentRow> {
        ByteArrayInputStream(bytes).use { input ->
            XSSFWorkbook(input).use { workbook ->
                if (workbook.numberOfSheets <= 0) return emptyList()

                val sheet = workbook.getSheetAt(0) ?: return emptyList()
                val formatter = DataFormatter()
                val evaluator = workbook.creationHelper.createFormulaEvaluator()

                val rows = buildList {
                    for (row in sheet) {
                        val columnCount = maxOf(row.lastCellNum.toInt().coerceAtLeast(0), 3)
                        val cells = (0 until columnCount).map { index ->
                            val cell = row.getCell(
                                index,
                                org.apache.poi.ss.usermodel.Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
                            )
                            if (cell == null) "" else formatter.formatCellValue(cell, evaluator).normalizeSpaces()
                        }
                        if (cells.any { it.isNotBlank() }) add(cells)
                    }
                }

                return mapRowsToStudents(rows)
            }
        }
    }

    private fun mapRowsToStudents(rows: List<List<String>>): List<ImportStudentRow> {
        if (rows.isEmpty()) return emptyList()

        val header = rows.first().map { normalizeStrictHeader(it) }
        val lastNameIndex = header.indexOf(REQUIRED_LAST_NAME_HEADER)
        val firstNameIndex = header.indexOf(REQUIRED_FIRST_NAME_HEADER)
        val middleNameIndex = header.indexOf(REQUIRED_MIDDLE_NAME_HEADER)

        if (lastNameIndex < 0 || firstNameIndex < 0 || middleNameIndex < 0) {
            throw IllegalArgumentException("Обязательные колонки: Фамилия, Имя, Отчество")
        }

        return rows.drop(1).mapNotNull { cells ->
            val lastName = cells.getOrNull(lastNameIndex).orEmpty().normalizeSpaces()
            val firstName = cells.getOrNull(firstNameIndex).orEmpty().normalizeSpaces()
            val middleName = cells.getOrNull(middleNameIndex).orEmpty().normalizeSpaces()
            buildRow(firstName = firstName, lastName = lastName, middleName = middleName)
        }
    }

    private fun buildRow(firstName: String, lastName: String, middleName: String): ImportStudentRow? {
        val first = firstName.normalizeSpaces()
        val last = lastName.normalizeSpaces()
        if (first.isBlank() || last.isBlank()) return null

        return ImportStudentRow(
            firstName = first,
            lastName = last,
            middleName = middleName.normalizeSpaces()
        )
    }

    private fun decodeCsvContent(bytes: ByteArray): String {
        val utf8 = bytes.toString(Charsets.UTF_8).removePrefix("\uFEFF") // BOM символ - в нашей кадировке считается как пробел
        val looksBrokenUtf8 = utf8.count { it == '�' } > 0
        if (!looksBrokenUtf8) return utf8

        return bytes.toString(Charset.forName("windows-1251")).removePrefix("\uFEFF")
    }

    private fun detectDelimiter(line: String): Char {
        val candidates = listOf(';', ',', '\t', '|')
        return candidates.maxByOrNull { delimiter ->
            line.count { it == delimiter }
        } ?: ';'
    }

    private fun normalizeStrictHeader(value: String): String {
        return value
            .trim()
            .lowercase()
            .replace("ё", "е")
    }

    private fun ContentResolver.queryDisplayName(uri: Uri): String? {
        query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) return cursor.getString(index)
            }
        }
        return null
    }

    val supportedMimeTypes: Array<String> = arrayOf(
        "text/csv",
        "application/csv",
        "text/comma-separated-values",
        "text/plain",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )

    private const val REQUIRED_LAST_NAME_HEADER = "фамилия"
    private const val REQUIRED_FIRST_NAME_HEADER = "имя"
    private const val REQUIRED_MIDDLE_NAME_HEADER = "отчество"
}
