package com.edujournal.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import com.edujournal.data.local.database.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseTransferManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDatabase: AppDatabase
) {
    private val dbName = "edujournal_db"
    @Volatile
    var lastError: String? = null
        private set

    suspend fun exportDatabase(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            lastError = null
            val snapshotFile = createExportSnapshot()
            require(snapshotFile.exists()) { "DB_FILE_NOT_FOUND" }

            context.contentResolver.openOutputStream(uri)?.use { output ->
                snapshotFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            } ?: error("OUTPUT_STREAM_NOT_FOUND")
            snapshotFile.delete()
            true
        }.getOrElse { e ->
            lastError = e.message ?: e::class.java.simpleName
            Log.e("DatabaseTransfer", "Export failed: ${lastError}", e)
            false
        }
    }

    suspend fun importDatabase(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            lastError = null
            val tempFile = File(context.cacheDir, "import_tmp.db")
            if (tempFile.exists()) tempFile.delete()

            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: error("INPUT_STREAM_NOT_FOUND")

            require(hasSQLiteHeader(tempFile)) { "INVALID_DATABASE_HEADER" }
            require(isValidDatabase(tempFile)) { "INVALID_DATABASE" }

            appDatabase.close()
            deleteDbFiles()

            val dbFile = context.getDatabasePath(dbName)
            dbFile.parentFile?.mkdirs()
            tempFile.inputStream().use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile.delete()
            true
        }.getOrElse { e ->
            lastError = e.message ?: e::class.java.simpleName
            Log.e("DatabaseTransfer", "Import failed: ${lastError}", e)
            false
        }
    }

    private fun checkpointWal() {
        appDatabase.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").close()
    }

    private fun createExportSnapshot(): File {
        checkpointWal()
        val sourceDbFile = context.getDatabasePath(dbName)
        require(sourceDbFile.exists()) { "DB_FILE_NOT_FOUND" }

        val snapshotFile = File(context.cacheDir, "edujournal_export_snapshot.db")
        if (snapshotFile.exists()) snapshotFile.delete()

        val escapedSnapshotPath = snapshotFile.absolutePath.replace("'", "''")
        val sourceDb = SQLiteDatabase.openDatabase(
            sourceDbFile.absolutePath,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
        return runCatching {
            sourceDb.use { db ->
                db.execSQL("PRAGMA wal_checkpoint(FULL)")
                db.execSQL("VACUUM INTO '$escapedSnapshotPath'")
            }
            snapshotFile
        }.getOrElse {
            // Fallback for older SQLite engines where VACUUM INTO may be unavailable.
            sourceDbFile.inputStream().use { input ->
                snapshotFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            snapshotFile
        }
    }

    private fun deleteDbFiles() {
        val dbFile = context.getDatabasePath(dbName)
        val walFile = File(dbFile.absolutePath + "-wal")
        val shmFile = File(dbFile.absolutePath + "-shm")
        if (walFile.exists()) walFile.delete()
        if (shmFile.exists()) shmFile.delete()
        context.deleteDatabase(dbName)
    }

    private fun isValidDatabase(file: File): Boolean {
        val requiredCoreTables = setOf(
            "groups",
            "students",
            "subjects",
            "semesters",
            "subject_lesson_types",
            "lessons",
            "grades"
        )
        val existingTables = mutableSetOf<String>()
        val db = SQLiteDatabase.openDatabase(file.absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
        db.use {
            it.execSQL("PRAGMA foreign_keys=OFF")
            val cursor = it.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'",
                null
            )
            cursor.use { c ->
                while (c.moveToNext()) {
                    existingTables += c.getString(0)
                }
            }

            if ("subject_lesson_types" !in existingTables && "lesson_types" in existingTables) {
                it.execSQL("ALTER TABLE lesson_types RENAME TO subject_lesson_types")
                existingTables.remove("lesson_types")
                existingTables.add("subject_lesson_types")
            }

            if (!requiredCoreTables.all { table -> table in existingTables }) {
                val missing = requiredCoreTables.filterNot { it in existingTables }
                Log.e("DatabaseTransfer", "Missing core tables: $missing; existing=$existingTables")
                lastError = "MISSING_TABLES: $missing"
                return false
            }

            if ("subject_semesters" !in existingTables) {
                it.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS subject_semesters (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        subjectId INTEGER NOT NULL,
                        semesterId INTEGER NOT NULL,
                        FOREIGN KEY(subjectId) REFERENCES subjects(id) ON DELETE CASCADE,
                        FOREIGN KEY(semesterId) REFERENCES semesters(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                it.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_subject_semesters_subjectId_semesterId ON subject_semesters(subjectId, semesterId)"
                )
                it.execSQL("CREATE INDEX IF NOT EXISTS index_subject_semesters_subjectId ON subject_semesters(subjectId)")
                it.execSQL("CREATE INDEX IF NOT EXISTS index_subject_semesters_semesterId ON subject_semesters(semesterId)")
                it.execSQL(
                    """
                    INSERT OR IGNORE INTO subject_semesters(subjectId, semesterId)
                    SELECT s.id, (SELECT MIN(id) FROM semesters)
                    FROM subjects s
                    WHERE (SELECT MIN(id) FROM semesters) IS NOT NULL
                    """.trimIndent()
                )
            }

            if ("homeworks" !in existingTables) {
                it.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS homeworks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        lessonId INTEGER NOT NULL,
                        text TEXT NOT NULL,
                        FOREIGN KEY(lessonId) REFERENCES lessons(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                it.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_homeworks_lessonId ON homeworks(lessonId)")
            }

            if ("homework_submissions" !in existingTables) {
                it.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS homework_submissions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        homeworkId INTEGER NOT NULL,
                        studentId INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        FOREIGN KEY(homeworkId) REFERENCES homeworks(id) ON DELETE CASCADE,
                        FOREIGN KEY(studentId) REFERENCES students(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                it.execSQL("CREATE INDEX IF NOT EXISTS index_homework_submissions_homeworkId ON homework_submissions(homeworkId)")
                it.execSQL("CREATE INDEX IF NOT EXISTS index_homework_submissions_studentId ON homework_submissions(studentId)")
                it.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_homework_submissions_homeworkId_studentId ON homework_submissions(homeworkId, studentId)"
                )
            }
        }
        return true
    }

    private fun hasSQLiteHeader(file: File): Boolean {
        if (!file.exists() || file.length() < 16L) return false
        return runCatching {
            val header = ByteArray(16)
            file.inputStream().use { input ->
                val read = input.read(header)
                if (read < 16) return false
            }
            String(header, Charsets.US_ASCII) == "SQLite format 3\u0000"
        }.getOrDefault(false)
    }
}
