package com.edujournal.presentation.journalexport

import android.content.ContentResolver
import android.net.Uri
import com.edujournal.presentation.state.JournalMetaState
import com.edujournal.presentation.state.JournalState
import io.mockk.mockk
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class JournalExportWriterTest {

    @Test
    fun `buildDetailedFileName sanitizes unsafe chars and appends extension`() {
        val meta = JournalMetaState(
            subjectLabel = "Math/Algebra",
            lessonTypeLabel = "Lab:Type",
            groupLabel = "1070|1322"
        )

        val fileName = JournalExportWriter.buildDetailedFileName(
            meta = meta,
            semesterLabel = "Spring 2026",
            format = JournalExportFormat.EXCEL
        )

        assertTrue(fileName.startsWith("Журнал_"))
        assertTrue(fileName.endsWith(".xlsx"))
        assertTrue(fileName.contains("Math_Algebra"))
        assertTrue(fileName.contains("Lab_Type"))
        assertTrue(fileName.contains("1070_1322"))
        assertTrue(fileName.contains("Spring_2026"))
        assertTrue(fileName.none { it in "\\/:*?\"<>|" })
    }

    @Test
    fun `export throws when journal data is empty`() {
        val resolver = mockk<ContentResolver>(relaxed = true)
        val uri = mockk<Uri>(relaxed = true)
        val state = JournalState(lessons = emptyList(), rows = emptyList())

        assertThrows(IllegalArgumentException::class.java) {
            JournalExportWriter.export(
                resolver = resolver,
                uri = uri,
                format = JournalExportFormat.PDF,
                state = state,
                meta = null,
                semesterLabel = "Spring 2026"
            )
        }
    }
}