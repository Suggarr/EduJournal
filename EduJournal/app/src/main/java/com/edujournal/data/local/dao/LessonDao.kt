package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edujournal.data.local.database.entities.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Query(
        """
        SELECT lessons.* FROM lessons
        WHERE lessons.groupId = :groupId
        AND lessons.subjectLessonTypeId = :subjectLessonTypeId
        AND lessons.semesterId = :semesterId
        ORDER BY lessons.date
        """
    )
    fun observeLessons(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    fun observeLessonById(lessonId: Long): Flow<LessonEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Update
    suspend fun updateLesson(lesson: LessonEntity): Int

    @Query("DELETE FROM lessons WHERE id = :lessonId")
    suspend fun deleteLesson(lessonId: Long)
}


