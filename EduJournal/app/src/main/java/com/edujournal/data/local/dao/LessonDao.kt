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
        SELECT * FROM lessons
        WHERE groupId = :groupId
        AND subjectId = :subjectId
        AND subjectLessonTypeId = :subjectLessonTypeId
        AND semesterId = :semesterId
        ORDER BY date
        """
    )
    fun observeLessons(
        groupId: Long,
        subjectId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    fun observeLessonById(lessonId: Long): Flow<LessonEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLesson(lesson: LessonEntity)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Query("DELETE FROM lessons WHERE id = :lessonId")
    suspend fun deleteLesson(lessonId: Long)
}

