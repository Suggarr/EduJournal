package com.edujournal.presentation.studentimport

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.StringReader
import java.nio.charset.Charset
import java.util.zip.ZipInputStream

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
                    ?: throw IllegalArgumentException("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c \u0444\u0430\u0439\u043b")

                val rows = when {
                    displayName.endsWith(".csv") -> parseCsv(bytes)
                    displayName.endsWith(".xlsx") -> parseXlsx(bytes)
                    displayName.endsWith(".xls") -> throw IllegalArgumentException(
                        "\u0424\u043e\u0440\u043c\u0430\u0442 .xls \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f. \u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u0435 \u0444\u0430\u0439\u043b \u043a\u0430\u043a .xlsx."
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
                    if (index < record.size()) record.get(index).trim() else ""
                }
            }
            .filter { cells -> cells.any { it.isNotBlank() } }

        return mapRowsToStudents(rows)
    }

    private fun parseXlsx(bytes: ByteArray): List<ImportStudentRow> {
        val sharedStrings = mutableListOf<String>()
        var firstSheetBytes: ByteArray? = null

        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val name = entry.name
                when {
                    name == "xl/sharedStrings.xml" -> {
                        sharedStrings.clear()
                        sharedStrings.addAll(parseSharedStrings(zip.readBytes()))
                    }

                    firstSheetBytes == null && name.startsWith("xl/worksheets/") && name.endsWith(".xml") -> {
                        firstSheetBytes = zip.readBytes()
                    }
                }

                zip.closeEntry()
                entry = zip.nextEntry
            }
        }

        val sheetBytes = firstSheetBytes ?: return emptyList()
        val rows = parseSheetRows(sheetBytes, sharedStrings)
            .filter { row -> row.any { it.isNotBlank() } }

        return mapRowsToStudents(rows)
    }

    private fun parseSharedStrings(xmlBytes: ByteArray): List<String> {
        val parser = Xml.newPullParser()
        parser.setInput(ByteArrayInputStream(xmlBytes), Charsets.UTF_8.name())

        val result = mutableListOf<String>()
        var currentText: StringBuilder? = null

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            when (parser.eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "si" -> currentText = StringBuilder()
                        "t" -> {
                            if (currentText != null) {
                                currentText.append(readElementText(parser))
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "si") {
                        result.add(currentText?.toString().orEmpty())
                        currentText = null
                    }
                }
            }
            parser.next()
        }

        return result
    }

    private fun parseSheetRows(
        xmlBytes: ByteArray,
        sharedStrings: List<String>
    ): List<List<String>> {
        val parser = Xml.newPullParser()
        parser.setInput(ByteArrayInputStream(xmlBytes), Charsets.UTF_8.name())

        val rows = mutableListOf<List<String>>()
        var rowMap: MutableMap<Int, String>? = null

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            when (parser.eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "row" -> rowMap = mutableMapOf()
                        "c" -> {
                            val currentRowMap = rowMap
                            if (currentRowMap != null) {
                                val ref = parser.getAttributeValue(null, "r").orEmpty()
                                val type = parser.getAttributeValue(null, "t").orEmpty()
                                val columnIndex = columnIndexFromCellRef(ref)
                                val value = parseCellValue(parser, type, sharedStrings).trim()
                                if (value.isNotEmpty()) {
                                    currentRowMap[columnIndex] = value
                                }
                            } else {
                                skipCurrentTag(parser, "c")
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "row") {
                        val cells = rowMap.orEmpty()
                        val maxIndex = cells.keys.maxOrNull() ?: -1
                        val minColumns = maxOf(maxIndex + 1, 3)
                        rows.add((0 until minColumns).map { index -> cells[index].orEmpty() })
                        rowMap = null
                    }
                }
            }
            parser.next()
        }

        return rows
    }

    private fun parseCellValue(
        parser: XmlPullParser,
        type: String,
        sharedStrings: List<String>
    ): String {
        var valueText: String? = null
        val startDepth = parser.depth

        while (!(parser.eventType == XmlPullParser.END_TAG && parser.depth == startDepth && parser.name == "c")) {
            parser.next()
            if (parser.eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "v" -> valueText = readElementText(parser)
                    "t" -> {
                        if (type == "inlineStr") {
                            valueText = readElementText(parser)
                        }
                    }
                }
            }
        }

        return when (type) {
            "s" -> {
                val index = valueText?.toIntOrNull()
                if (index == null) "" else sharedStrings.getOrNull(index).orEmpty()
            }

            "b" -> if (valueText == "1") "TRUE" else "FALSE"
            else -> valueText.orEmpty()
        }
    }

    private fun readElementText(parser: XmlPullParser): String {
        if (parser.next() == XmlPullParser.TEXT) {
            val text = parser.text.orEmpty()
            parser.nextTag()
            return text
        }
        return ""
    }

    private fun skipCurrentTag(parser: XmlPullParser, tagName: String) {
        val startDepth = parser.depth
        while (!(parser.eventType == XmlPullParser.END_TAG && parser.depth == startDepth && parser.name == tagName)) {
            parser.next()
        }
    }

    private fun columnIndexFromCellRef(cellRef: String): Int {
        if (cellRef.isBlank()) return 0
        var index = 0
        for (char in cellRef) {
            if (!char.isLetter()) break
            index = index * 26 + (char.uppercaseChar() - 'A' + 1)
        }
        return (index - 1).coerceAtLeast(0)
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
            val lastName = cells.getOrNull(lastNameIndex).orEmpty().trim()
            val firstName = cells.getOrNull(firstNameIndex).orEmpty().trim()
            val middleName = cells.getOrNull(middleNameIndex).orEmpty().trim()
            buildRow(firstName = firstName, lastName = lastName, middleName = middleName)
        }
    }

    private fun buildRow(firstName: String, lastName: String, middleName: String): ImportStudentRow? {
        val first = firstName.trim()
        val last = lastName.trim()
        if (first.isBlank() || last.isBlank()) return null

        return ImportStudentRow(
            firstName = first,
            lastName = last,
            middleName = middleName.trim()
        )
    }

    private fun decodeCsvContent(bytes: ByteArray): String {
        val utf8 = bytes.toString(Charsets.UTF_8).removePrefix("\uFEFF")
        val looksBrokenUtf8 = utf8.count { it == '\uFFFD' } > 0
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
            .replace("\u0451", "\u0435")
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

    private const val REQUIRED_LAST_NAME_HEADER = "\u0444\u0430\u043c\u0438\u043b\u0438\u044f"
    private const val REQUIRED_FIRST_NAME_HEADER = "\u0438\u043c\u044f"
    private const val REQUIRED_MIDDLE_NAME_HEADER = "\u043e\u0442\u0447\u0435\u0441\u0442\u0432\u043e"
}
