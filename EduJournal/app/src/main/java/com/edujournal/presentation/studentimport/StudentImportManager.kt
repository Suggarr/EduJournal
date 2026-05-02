package com.edujournal.presentation.studentimport

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentImportManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun parse(uri: Uri): StudentImportParseResult {
        return StudentImportFileParser.parse(context, uri)
    }

    val supportedMimeTypes: Array<String>
        get() = StudentImportFileParser.supportedMimeTypes
}
