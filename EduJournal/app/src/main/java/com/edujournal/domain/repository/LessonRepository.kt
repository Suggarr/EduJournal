package com.edujournal.domain.repository

import com.edujournal.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun observeLessons(
        groupId: Long,
        subjectId: Long,
        lessonTypeId: Long
    ): Flow<List<Lesson>>

    suspend fun insertLesson(lesson: Lesson)

    suspend fun updateLesson(lesson: Lesson)

    suspend fun deleteLesson(lessonId: Long)
}
