package com.edujournal.presentation.journalexport

import android.content.ContentResolver
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.edujournal.presentation.state.JournalMetaState
import com.edujournal.presentation.state.JournalState
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

enum class JournalExportFormat(
    val extension: String
) {
    PDF("pdf"),
    EXCEL("xlsx")
}

object JournalExportWriter {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")

    fun buildDetailedFileName(
        meta: JournalMetaState?,
        semesterLabel: String,
        format: JournalExportFormat
    ): String {
        val subject = sanitizeNamePart(meta?.subjectLabel?.ifBlank { "Предмет" } ?: "Предмет")
        val type = sanitizeNamePart(meta?.lessonTypeLabel?.ifBlank { "Тип" } ?: "Тип")
        val group = sanitizeNamePart(meta?.groupLabel?.ifBlank { "Группа" } ?: "Группа")
        val semester = sanitizeNamePart(semesterLabel)
        val timestamp = LocalDateTime.now().format(dateTimeFormatter)

        return "Журнал_${subject}_${type}_${group}_${semester}_${timestamp}.${format.extension}"
    }

    fun export(
        resolver: ContentResolver,
        uri: Uri,
        format: JournalExportFormat,
        state: JournalState,
        meta: JournalMetaState?,
        semesterLabel: String
    ) {
        if (state.rows.isEmpty() || state.lessons.isEmpty()) {
            throw IllegalArgumentException("Нет данных для экспорта")
        }

        when (format) {
            JournalExportFormat.PDF -> writePdf(resolver, uri, state, meta, semesterLabel)
            JournalExportFormat.EXCEL -> writeExcel(resolver, uri, state)
        }
    }

    private fun writeExcel(
        resolver: ContentResolver,
        uri: Uri,
        state: JournalState
    ) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Журнал")

        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Студент")
        state.lessons.forEachIndexed { index, lesson ->
            headerRow.createCell(index + 1).setCellValue(lesson.date.format(dateFormatter))
        }

        state.rows.forEachIndexed { rowIndex, row ->
            val xlsxRow = sheet.createRow(rowIndex + 1)
            xlsxRow.createCell(0).setCellValue(row.studentName)
            row.cells.forEachIndexed { cellIndex, cell ->
                val value = if (cell.value == "-") "" else cell.value
                xlsxRow.createCell(cellIndex + 1).setCellValue(value)
            }
        }

        sheet.setColumnWidth(0, 9000)
        for (i in state.lessons.indices) {
            sheet.setColumnWidth(i + 1, 4200)
        }

        val topicsSheet = workbook.createSheet("Темы занятий")
        val topicsHeaderRow = topicsSheet.createRow(0)
        topicsHeaderRow.createCell(0).setCellValue("Дата")
        topicsHeaderRow.createCell(1).setCellValue("Тема занятия")
        state.lessons.forEachIndexed { index, lesson ->
            val row = topicsSheet.createRow(index + 1)
            row.createCell(0).setCellValue(lesson.date.format(dateFormatter))
            row.createCell(1).setCellValue(lesson.topic)
        }
        topicsSheet.setColumnWidth(0, 5200)
        topicsSheet.setColumnWidth(1, 18000)

        resolver.openOutputStream(uri)?.use { output ->
            workbook.use { wb ->
                wb.write(output)
                output.flush()
            }
        } ?: throw IllegalStateException("Не удалось открыть файл для записи")
    }

    private fun writePdf(
        resolver: ContentResolver,
        uri: Uri,
        state: JournalState,
        meta: JournalMetaState?,
        semesterLabel: String
    ) {
        val document = PdfDocument()

        val pageWidth = 1200
        val pageHeight = 842
        val margin = 28
        val titleHeight = 92
        val rowHeight = 32
        val studentColumnWidth = 300

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 22f
            isFakeBoldText = true
        }
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 16f
        }
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 14f
            isFakeBoldText = true
        }
        val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 13f
        }
        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
        }

        val availableHeight = pageHeight - margin * 2 - titleHeight - rowHeight
        val maxRowsPerPage = (availableHeight / rowHeight).coerceAtLeast(1)

        val tableWidth = pageWidth - margin * 2
        val maxLessonColumns = 8.coerceAtMost(state.lessons.size)
        var pageNumber = 1

        for (colStart in state.lessons.indices step maxLessonColumns) {
            val colEnd = min(colStart + maxLessonColumns, state.lessons.size)
            val dateSlice = state.lessons.subList(colStart, colEnd)
            val dateColumnWidth = ((tableWidth - studentColumnWidth) / dateSlice.size).coerceAtLeast(80)
            val currentTableWidth = studentColumnWidth + dateColumnWidth * dateSlice.size

            for (rowStart in state.rows.indices step maxRowsPerPage) {
                val rowEnd = min(rowStart + maxRowsPerPage, state.rows.size)
                val rowsSlice = state.rows.subList(rowStart, rowEnd)

                val page = document.startPage(
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
                )
                val canvas = page.canvas

                val title = buildString {
                    append("Журнал: ")
                    append(meta?.subjectLabel?.ifBlank { "-" } ?: "-")
                    append(" | ")
                    append(meta?.lessonTypeLabel?.ifBlank { "-" } ?: "-")
                    append(" | ")
                    append(meta?.groupLabel?.ifBlank { "-" } ?: "-")
                }
                canvas.drawText(title, margin.toFloat(), (margin + 22).toFloat(), titlePaint)
                canvas.drawText(
                    "Семестр: $semesterLabel   Экспорт: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}",
                    margin.toFloat(),
                    (margin + 48).toFloat(),
                    subtitlePaint
                )

                val tableTop = margin + titleHeight
                val tableLeft = margin
                val headerBottom = tableTop + rowHeight

                drawTextCentered(
                    canvas = canvas,
                    text = "Студент",
                    left = tableLeft,
                    right = tableLeft + studentColumnWidth,
                    top = tableTop,
                    bottom = headerBottom,
                    paint = headerPaint
                )

                dateSlice.forEachIndexed { index, lesson ->
                    val left = tableLeft + studentColumnWidth + index * dateColumnWidth
                    val right = left + dateColumnWidth
                    drawTextCentered(
                        canvas = canvas,
                        text = lesson.date.format(dateFormatter),
                        left = left,
                        right = right,
                        top = tableTop,
                        bottom = headerBottom,
                        paint = headerPaint
                    )
                }

                rowsSlice.forEachIndexed { visualRowIndex, row ->
                    val top = headerBottom + visualRowIndex * rowHeight
                    val bottom = top + rowHeight

                    drawTextLeft(
                        canvas = canvas,
                        text = shorten(row.studentName, 36),
                        left = tableLeft + 8,
                        top = top,
                        bottom = bottom,
                        paint = cellPaint
                    )

                    dateSlice.forEachIndexed { index, _ ->
                        val left = tableLeft + studentColumnWidth + index * dateColumnWidth
                        val right = left + dateColumnWidth
                        val value = row.cells.getOrNull(colStart + index)?.value.orEmpty().let {
                            if (it == "-") "" else it
                        }
                        drawTextCentered(
                            canvas = canvas,
                            text = value,
                            left = left,
                            right = right,
                            top = top,
                            bottom = bottom,
                            paint = cellPaint
                        )
                    }
                }

                val tableBottom = headerBottom + rowsSlice.size * rowHeight
                for (lineX in 0..dateSlice.size) {
                    val x = (tableLeft + studentColumnWidth + lineX * dateColumnWidth).toFloat()
                    canvas.drawLine(x, tableTop.toFloat(), x, tableBottom.toFloat(), linePaint)
                }
                canvas.drawLine(tableLeft.toFloat(), tableTop.toFloat(), tableLeft.toFloat(), tableBottom.toFloat(), linePaint)
                canvas.drawLine(
                    (tableLeft + currentTableWidth).toFloat(),
                    tableTop.toFloat(),
                    (tableLeft + currentTableWidth).toFloat(),
                    tableBottom.toFloat(),
                    linePaint
                )

                val horizontalLines = rowsSlice.size + 1
                for (lineY in 0..horizontalLines) {
                    val y = (tableTop + lineY * rowHeight).toFloat()
                    canvas.drawLine(
                        tableLeft.toFloat(),
                        y,
                        (tableLeft + currentTableWidth).toFloat(),
                        y,
                        linePaint
                    )
                }

                document.finishPage(page)
            }
        }

        appendTopicsPages(
            document = document,
            state = state,
            pageWidth = pageWidth,
            pageHeight = pageHeight,
            margin = margin,
            startPageNumber = pageNumber
        )

        resolver.openOutputStream(uri)?.use { output ->
            document.writeTo(output)
            output.flush()
        } ?: throw IllegalStateException("Не удалось открыть файл для записи")

        document.close()
    }

    private fun drawTextCentered(
        canvas: android.graphics.Canvas,
        text: String,
        left: Int,
        right: Int,
        top: Int,
        bottom: Int,
        paint: Paint
    ) {
        val x = left + (right - left) / 2f
        val y = top + (bottom - top) / 2f - (paint.descent() + paint.ascent()) / 2f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(shorten(text, 12), x, y, paint)
    }

    private fun drawTextLeft(
        canvas: android.graphics.Canvas,
        text: String,
        left: Int,
        top: Int,
        bottom: Int,
        paint: Paint
    ) {
        val y = top + (bottom - top) / 2f - (paint.descent() + paint.ascent()) / 2f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(text, left.toFloat(), y, paint)
    }

    private fun shorten(text: String, maxLength: Int): String {
        return if (text.length <= maxLength) text else text.take(maxLength - 1) + "…"
    }

    private fun appendTopicsPages(
        document: PdfDocument,
        state: JournalState,
        pageWidth: Int,
        pageHeight: Int,
        margin: Int,
        startPageNumber: Int
    ) {
        if (state.lessons.isEmpty()) return

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 21f
            isFakeBoldText = true
        }
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 14f
            isFakeBoldText = true
        }
        val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 13f
        }
        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
        }

        val top = margin + 52
        val rowHeight = 32
        val dateColumnWidth = 180
        val topicColumnWidth = pageWidth - margin * 2 - dateColumnWidth
        val maxRowsPerPage = ((pageHeight - top - margin - rowHeight) / rowHeight).coerceAtLeast(1)

        var pageNumber = startPageNumber
        for (rowStart in state.lessons.indices step maxRowsPerPage) {
            val rowEnd = min(rowStart + maxRowsPerPage, state.lessons.size)
            val lessonsSlice = state.lessons.subList(rowStart, rowEnd)

            val page = document.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
            )
            val canvas = page.canvas

            canvas.drawText("Темы занятий", margin.toFloat(), (margin + 24).toFloat(), titlePaint)

            val tableTop = top
            val tableLeft = margin
            val headerBottom = tableTop + rowHeight

            drawTextCentered(
                canvas = canvas,
                text = "Дата",
                left = tableLeft,
                right = tableLeft + dateColumnWidth,
                top = tableTop,
                bottom = headerBottom,
                paint = headerPaint
            )
            drawTextCentered(
                canvas = canvas,
                text = "Тема занятия",
                left = tableLeft + dateColumnWidth,
                right = tableLeft + dateColumnWidth + topicColumnWidth,
                top = tableTop,
                bottom = headerBottom,
                paint = headerPaint
            )

            lessonsSlice.forEachIndexed { index, lesson ->
                val rowTop = headerBottom + index * rowHeight
                val rowBottom = rowTop + rowHeight
                drawTextCentered(
                    canvas = canvas,
                    text = lesson.date.format(dateFormatter),
                    left = tableLeft,
                    right = tableLeft + dateColumnWidth,
                    top = rowTop,
                    bottom = rowBottom,
                    paint = cellPaint
                )
                drawTextLeft(
                    canvas = canvas,
                    text = shorten(lesson.topic, 95),
                    left = tableLeft + dateColumnWidth + 8,
                    top = rowTop,
                    bottom = rowBottom,
                    paint = cellPaint
                )
            }

            val tableBottom = headerBottom + lessonsSlice.size * rowHeight
            val right = tableLeft + dateColumnWidth + topicColumnWidth

            canvas.drawLine(tableLeft.toFloat(), tableTop.toFloat(), tableLeft.toFloat(), tableBottom.toFloat(), linePaint)
            canvas.drawLine((tableLeft + dateColumnWidth).toFloat(), tableTop.toFloat(), (tableLeft + dateColumnWidth).toFloat(), tableBottom.toFloat(), linePaint)
            canvas.drawLine(right.toFloat(), tableTop.toFloat(), right.toFloat(), tableBottom.toFloat(), linePaint)

            for (lineY in 0..(lessonsSlice.size + 1)) {
                val y = (tableTop + lineY * rowHeight).toFloat()
                canvas.drawLine(tableLeft.toFloat(), y, right.toFloat(), y, linePaint)
            }

            document.finishPage(page)
        }
    }

    private fun sanitizeNamePart(value: String): String {
        return value
            .trim()
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .replace(Regex("\\s+"), "_")
            .ifBlank { "БезНазвания" }
    }
}
