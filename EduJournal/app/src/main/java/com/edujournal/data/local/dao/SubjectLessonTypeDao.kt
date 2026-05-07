package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edujournal.data.local.database.entities.SubjectLessonTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectLessonTypeDao {

    @Query("SELECT * FROM subject_lesson_types WHERE subjectId = :subjectId ORDER BY id")
    fun observeLessonTypes(subjectId: Long): Flow<List<SubjectLessonTypeEntity>>

    @Query("SELECT * FROM subject_lesson_types WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SubjectLessonTypeEntity?

    @Query("SELECT * FROM subject_lesson_types WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<SubjectLessonTypeEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(SubjectLessonType: SubjectLessonTypeEntity): Long

    @Update
    suspend fun update(SubjectLessonType: SubjectLessonTypeEntity): Int

    @Query("DELETE FROM subject_lesson_types WHERE id = :typeId")
    suspend fun deleteById(typeId: Long)
}



