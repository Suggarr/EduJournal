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

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(SubjectLessonType: SubjectLessonTypeEntity): Int

    @Query("SELECT EXISTS(SELECT 1 FROM subject_lesson_types WHERE id = :id)")
    suspend fun existsById(id: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM subject_lesson_types WHERE subjectId = :subjectId AND name = :name)")
    suspend fun existsByName(subjectId: Long, name: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM subject_lesson_types WHERE subjectId = :subjectId AND name = :name AND id != :id)")
    suspend fun existsByNameExceptId(subjectId: Long, name: String, id: Long): Boolean

    @Query("DELETE FROM subject_lesson_types WHERE id = :typeId")
    suspend fun deleteById(typeId: Long)
}


