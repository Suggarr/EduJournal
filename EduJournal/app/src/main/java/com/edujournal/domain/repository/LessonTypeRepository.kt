package com.edujournal.domain.repository

import com.edujournal.domain.model.LessonType
import kotlinx.coroutines.flow.Flow

interface LessonTypeRepository {
    fun observeLessonTypes(): Flow<List<LessonType>>

    suspend fun createLessonType(lessonType: LessonType)
}