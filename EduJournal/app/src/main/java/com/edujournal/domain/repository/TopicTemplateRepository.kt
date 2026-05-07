package com.edujournal.domain.repository

import com.edujournal.domain.model.TopicTemplate
import kotlinx.coroutines.flow.Flow

interface TopicTemplateRepository {
    fun observeByContext(semesterId: Long, subjectLessonTypeId: Long): Flow<List<TopicTemplate>>

    suspend fun create(item: TopicTemplate): Long

    suspend fun update(item: TopicTemplate): Int

    suspend fun deleteById(id: Long)
}


