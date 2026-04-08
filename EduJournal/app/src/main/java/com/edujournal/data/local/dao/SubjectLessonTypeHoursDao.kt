package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.edujournal.data.local.database.entities.SubjectLessonTypeHoursEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectLessonTypeHoursDao {

    @Query("SELECT * FROM subject_lesson_type_hours")
    fun observeAll(): Flow<List<SubjectLessonTypeHoursEntity>>

    @Query("SELECT * FROM subject_lesson_type_hours WHERE subjectId = :subjectId")
    suspend fun getBySubjectId(subjectId: Long): List<SubjectLessonTypeHoursEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<SubjectLessonTypeHoursEntity>)

    @Query("DELETE FROM subject_lesson_type_hours WHERE subjectId = :subjectId")
    suspend fun deleteBySubjectId(subjectId: Long)

    @Transaction
    suspend fun replaceForSubject(
        subjectId: Long,
        items: List<SubjectLessonTypeHoursEntity>
    ) {
        deleteBySubjectId(subjectId)
        if (items.isNotEmpty()) {
            insertAll(items)
        }
    }
}
