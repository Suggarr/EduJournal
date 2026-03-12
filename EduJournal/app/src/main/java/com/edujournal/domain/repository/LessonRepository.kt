package com.edujournal.domain.repository

import com.edujournal.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun getLessons(): Flow<List<Lesson>>
    suspend fun insertLesson(lesson: Lesson)
}