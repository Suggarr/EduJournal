package com.edujournal.domain.repository

import com.edujournal.domain.model.SubjectLessonType
import kotlinx.coroutines.flow.Flow

interface SubjectLessonTypeRepository {
    fun observeLessonTypes(subjectId: Long): Flow<List<SubjectLessonType>>
    fun observeById(id: Long): Flow<SubjectLessonType?>

    suspend fun getById(id: Long): SubjectLessonType?

    suspend fun createLessonType(SubjectLessonType: SubjectLessonType): Long

    suspend fun updateLessonType(SubjectLessonType: SubjectLessonType): Int

    suspend fun deleteLessonType(typeId: Long)
}




