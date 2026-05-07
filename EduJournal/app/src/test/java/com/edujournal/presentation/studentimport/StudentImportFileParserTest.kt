package com.edujournal.presentation.studentimport

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.edujournal.R
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class StudentImportFileParserTest {

    @Test
    fun `parse returns error for xls files`() = runBlocking {
        val context = mockk<Context>()
        val resolver = mockk<ContentResolver>()
        val cursor = mockk<Cursor>()
        val uri = mockk<Uri>()

        every { context.contentResolver } returns resolver
        every { context.getString(R.string.student_import_error_xls_not_supported) } returns "XLS_NOT_SUPPORTED"
        every { context.getString(R.string.student_import_error_required_columns) } returns "REQUIRED_COLUMNS"
        every { context.getString(R.string.student_import_error_read_file) } returns "READ_FILE_ERROR"
        every { context.getString(R.string.student_import_error_invalid_format) } returns "INVALID_FORMAT"

        every { resolver.query(uri, any(), null, null, null) } returns cursor
        every { cursor.moveToFirst() } returns true
        every { cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) } returns 0
        every { cursor.getString(0) } returns "students.xls"
        every { resolver.openInputStream(uri) } returns ByteArrayInputStream("a,b,c".toByteArray())
        every { cursor.close() } returns Unit

        val result = StudentImportFileParser.parse(context, uri)

        assertTrue(result is StudentImportParseResult.Error)
        val error = result as StudentImportParseResult.Error
        assertTrue(error.reason.isNotBlank())

        verify(exactly = 1) { resolver.query(uri, any(), null, null, null) }
        verify(exactly = 1) { resolver.openInputStream(uri) }
    }
}