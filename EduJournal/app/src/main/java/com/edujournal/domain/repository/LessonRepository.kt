package com.edujournal.domain.repository

import com.edujournal.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun observeLessons(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): Flow<List<Lesson>>

    fun observeLessonById(lessonId: Long): Flow<Lesson?>

    suspend fun insertLesson(lesson: Lesson): Long

    suspend fun updateLesson(lesson: Lesson): Int

    suspend fun deleteLesson(lessonId: Long)
}



