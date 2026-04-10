package com.edujournal.domain.repository

import com.edujournal.domain.model.LessonType
import kotlinx.coroutines.flow.Flow

interface LessonTypeRepository {
    fun observeLessonTypes(): Flow<List<LessonType>>

    suspend fun createLessonType(lessonType: LessonType): Long

    suspend fun updateLessonType(lessonType: LessonType): Int
    suspend fun existsById(id: Long): Boolean
    suspend fun existsByName(name: String): Boolean
    suspend fun existsByNameExceptId(name: String, id: Long): Boolean

    suspend fun deleteLessonType(typeId: Long)
}
