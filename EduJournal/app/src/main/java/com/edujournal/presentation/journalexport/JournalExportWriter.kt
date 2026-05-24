package com.edujournal.presentation.journalexport

import android.content.ContentResolver
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.edujournal.presentation.state.JournalMetaState
import com.edujournal.presentation.state.JournalCellTone
import com.edujournal.presentation.state.JournalCellVisualStyle
import com.edujournal.presentation.state.JournalCellVisualStyles
import com.edujournal.presentation.state.JournalState
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap
import org.apache.poi.xssf.usermodel.XSSFColor
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

        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }
        val headerFont = workbook.createFont().apply { bold = true }
        headerStyle.setFont(headerFont)

        val defaultCellStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }
        val studentCellStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.LEFT
            verticalAlignment = VerticalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }
        val summaryRowStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.LEMON_CHIFFON.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }
        val summaryLabelStyle = workbook.createCellStyle().apply {
            cloneStyleFrom(summaryRowStyle)
            alignment = HorizontalAlignment.LEFT
        }
        val summaryHeaderStyle = workbook.createCellStyle().apply {
            cloneStyleFrom(headerStyle)
            fillForegroundColor = IndexedColors.LIGHT_TURQUOISE.index
        }
        val legendStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.LEFT
        }
        val gradeStyleCache = mutableMapOf<JournalCellTone, CellStyle>()

        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).apply {
            setCellValue("Студент")
            setCellStyle(headerStyle)
        }
        state.lessons.forEachIndexed { index, lesson ->
            headerRow.createCell(index + 1).apply {
                setCellValue(lesson.date.format(dateFormatter))
                setCellStyle(headerStyle)
            }
        }
        val avgColumnIndex = state.lessons.size + 1
        val absColumnIndex = state.lessons.size + 2
        headerRow.createCell(avgColumnIndex).apply {
            setCellValue("Ср. балл")
            setCellStyle(summaryHeaderStyle)
        }
        headerRow.createCell(absColumnIndex).apply {
            setCellValue("Н/З/О")
            setCellStyle(summaryHeaderStyle)
        }

        state.rows.forEachIndexed { rowIndex, row ->
            val xlsxRow = sheet.createRow(rowIndex + 1)
            xlsxRow.createCell(0).apply {
                setCellValue(row.studentName)
                setCellStyle(studentCellStyle)
            }
            row.cells.forEachIndexed { cellIndex, cell ->
                val value = if (cell.value == "-") "" else (cell.value ?: "")
                val visualStyle = JournalCellVisualStyles.forValue(value)
                val gradeStyle = gradeStyleCache.getOrPut(visualStyle.tone) {
                    createGradeCellStyle(workbook, visualStyle)
                }
                xlsxRow.createCell(cellIndex + 1).apply {
                    setCellValue(value)
                    setCellStyle(if (visualStyle.tone == JournalCellTone.DEFAULT) defaultCellStyle else gradeStyle)
                }
            }
            xlsxRow.createCell(avgColumnIndex).apply {
                setCellValue(row.averageText)
                setCellStyle(summaryRowStyle)
            }
            xlsxRow.createCell(absColumnIndex).apply {
                setCellValue(row.absencesCount.toString())
                setCellStyle(summaryRowStyle)
            }
        }

        val absencesRowIndex = state.rows.size + 1
        val absencesRow = sheet.createRow(absencesRowIndex)
        absencesRow.createCell(0).apply {
            setCellValue("Отсутствующие (Н/З/О)")
            setCellStyle(summaryLabelStyle)
        }
        state.lessonAbsencesCounts.forEachIndexed { index, count ->
            absencesRow.createCell(index + 1).apply {
                setCellValue(count.toDouble())
                setCellStyle(summaryRowStyle)
            }
        }
        absencesRow.createCell(avgColumnIndex).apply {
            setCellValue("-")
            setCellStyle(summaryRowStyle)
        }
        absencesRow.createCell(absColumnIndex).apply {
            setCellValue("-")
            setCellStyle(summaryRowStyle)
        }

        val legendRow = sheet.createRow(absencesRowIndex + 2)
        legendRow.createCell(0).apply {
            setCellValue("Обозначения: 1-10 — оценки, Н — отсутствовал, З — болел, О — освобожден")
            setCellStyle(legendStyle)
        }

        sheet.setColumnWidth(0, 9000)
        for (i in state.lessons.indices) {
            sheet.setColumnWidth(i + 1, 4200)
        }
        sheet.setColumnWidth(avgColumnIndex, 3800)
        sheet.setColumnWidth(absColumnIndex, 3800)

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
        val headerBgPaint = Paint().apply {
            color = Color.parseColor("#DDE6FF")
            style = Paint.Style.FILL
        }
        val summaryHeaderBgPaint = Paint().apply {
            color = Color.parseColor("#D6F5EA")
            style = Paint.Style.FILL
        }
        val summaryBgPaint = Paint().apply {
            color = Color.parseColor("#FFF3E0")
            style = Paint.Style.FILL
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

        val availableHeight = pageHeight - margin * 2 - titleHeight - rowHeight * 3
        val maxRowsPerPage = (availableHeight / rowHeight).coerceAtLeast(1)

        val tableWidth = pageWidth - margin * 2
        val summaryColumnWidth = 96
        val maxLessonColumns = 7.coerceAtMost(state.lessons.size)
        var pageNumber = 1

        for (colStart in state.lessons.indices step maxLessonColumns) {
            val colEnd = min(colStart + maxLessonColumns, state.lessons.size)
            val dateSlice = state.lessons.subList(colStart, colEnd)
            val dateColumnWidth = ((tableWidth - studentColumnWidth - summaryColumnWidth * 2) / dateSlice.size).coerceAtLeast(80)
            val currentTableWidth = studentColumnWidth + dateColumnWidth * dateSlice.size + summaryColumnWidth * 2

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

                canvas.drawRect(
                    tableLeft.toFloat(),
                    tableTop.toFloat(),
                    (tableLeft + studentColumnWidth + dateColumnWidth * dateSlice.size).toFloat(),
                    headerBottom.toFloat(),
                    headerBgPaint
                )
                val summaryStartX = tableLeft + studentColumnWidth + dateColumnWidth * dateSlice.size
                canvas.drawRect(
                    summaryStartX.toFloat(),
                    tableTop.toFloat(),
                    (summaryStartX + summaryColumnWidth * 2).toFloat(),
                    headerBottom.toFloat(),
                    summaryHeaderBgPaint
                )

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
                val avgLeft = summaryStartX
                val avgRight = avgLeft + summaryColumnWidth
                val absLeft = avgRight
                val absRight = absLeft + summaryColumnWidth
                drawTextCentered(
                    canvas = canvas,
                    text = "Ср. балл",
                    left = avgLeft,
                    right = avgRight,
                    top = tableTop,
                    bottom = headerBottom,
                    paint = headerPaint
                )
                drawTextCentered(
                    canvas = canvas,
                    text = "Н/З/О",
                    left = absLeft,
                    right = absRight,
                    top = tableTop,
                    bottom = headerBottom,
                    paint = headerPaint
                )

                rowsSlice.forEachIndexed { visualRowIndex, row ->
                    val top = headerBottom + visualRowIndex * rowHeight
                    val bottom = top + rowHeight
                    canvas.drawRect(avgLeft.toFloat(), top.toFloat(), absRight.toFloat(), bottom.toFloat(), summaryBgPaint)

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
                        val visualStyle = JournalCellVisualStyles.forValue(value)
                        if (visualStyle.tone != JournalCellTone.DEFAULT) {
                            val bgPaint = Paint().apply {
                                color = visualStyle.backgroundArgb
                                style = Paint.Style.FILL
                            }
                            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), bgPaint)
                        }
                        val valuePaint = Paint(cellPaint).apply {
                            color = visualStyle.textArgb
                        }
                        drawTextCentered(
                            canvas = canvas,
                            text = value,
                            left = left,
                            right = right,
                            top = top,
                            bottom = bottom,
                            paint = valuePaint
                        )
                    }
                    drawTextCentered(
                        canvas = canvas,
                        text = row.averageText,
                        left = avgLeft,
                        right = avgRight,
                        top = top,
                        bottom = bottom,
                        paint = cellPaint
                    )
                    drawTextCentered(
                        canvas = canvas,
                        text = row.absencesCount.toString(),
                        left = absLeft,
                        right = absRight,
                        top = top,
                        bottom = bottom,
                        paint = cellPaint
                    )
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
                val summarySep = (tableLeft + studentColumnWidth + dateColumnWidth * dateSlice.size).toFloat()
                val avgSep = (tableLeft + studentColumnWidth + dateColumnWidth * dateSlice.size + summaryColumnWidth).toFloat()
                canvas.drawLine(summarySep, tableTop.toFloat(), summarySep, tableBottom.toFloat(), linePaint)
                canvas.drawLine(avgSep, tableTop.toFloat(), avgSep, tableBottom.toFloat(), linePaint)

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

                val absTop = tableBottom
                val absBottom = absTop + rowHeight
                canvas.drawRect(tableLeft.toFloat(), absTop.toFloat(), (tableLeft + currentTableWidth).toFloat(), absBottom.toFloat(), summaryBgPaint)
                drawTextLeft(
                    canvas = canvas,
                    text = "Отсутствующие (Н/З/О)",
                    left = tableLeft + 8,
                    top = absTop,
                    bottom = absBottom,
                    paint = cellPaint
                )
                dateSlice.forEachIndexed { index, _ ->
                    val left = tableLeft + studentColumnWidth + index * dateColumnWidth
                    val right = left + dateColumnWidth
                    val countValue = state.lessonAbsencesCounts.getOrNull(colStart + index)?.toString().orEmpty()
                    drawTextCentered(
                        canvas = canvas,
                        text = countValue,
                        left = left,
                        right = right,
                        top = absTop,
                        bottom = absBottom,
                        paint = cellPaint
                    )
                }
                drawTextCentered(canvas, "-", avgLeft, avgRight, absTop, absBottom, cellPaint)
                drawTextCentered(canvas, "-", absLeft, absRight, absTop, absBottom, cellPaint)
                canvas.drawRect(
                    tableLeft.toFloat(),
                    absTop.toFloat(),
                    (tableLeft + currentTableWidth).toFloat(),
                    absBottom.toFloat(),
                    linePaint
                )
                canvas.drawLine(summarySep, absTop.toFloat(), summarySep, absBottom.toFloat(), linePaint)
                canvas.drawLine(avgSep, absTop.toFloat(), avgSep, absBottom.toFloat(), linePaint)
                for (lineX in 0..dateSlice.size) {
                    val x = (tableLeft + studentColumnWidth + lineX * dateColumnWidth).toFloat()
                    canvas.drawLine(x, absTop.toFloat(), x, absBottom.toFloat(), linePaint)
                }

                val legendY = absBottom + 22
                canvas.drawText("Обозначения: 1-10 — оценки, Н — отсутствовал, З — болел, О — освобожден.", margin.toFloat(), legendY.toFloat(), subtitlePaint)

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

    private fun createGradeCellStyle(workbook: XSSFWorkbook, visualStyle: JournalCellVisualStyle): CellStyle {
        val style = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            fillPattern = FillPatternType.SOLID_FOREGROUND
        }
        style.setFillForegroundColor(xssfColor(visualStyle.backgroundArgb))
        val font = workbook.createFont().apply {
            setColor(xssfColor(visualStyle.textArgb))
        }
        style.setFont(font)
        return style
    }

    private fun xssfColor(argb: Int): XSSFColor {
        return XSSFColor(
            byteArrayOf(
                ((argb ushr 16) and 0xFF).toByte(),
                ((argb ushr 8) and 0xFF).toByte(),
                (argb and 0xFF).toByte()
            ),
            DefaultIndexedColorMap()
        )
    }
}
