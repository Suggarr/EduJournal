package com.edujournal.domain.repository

import com.edujournal.domain.model.Homework
import kotlinx.coroutines.flow.Flow

interface HomeworkRepository {
    fun observeHomework(lessonId: Long): Flow<Homework?>
    fun observeHomeworkLessonIds(lessonIds: List<Long>): Flow<List<Long>>

    suspend fun createHomework(homework: Homework): Long

    suspend fun updateHomework(homework: Homework): Int

    suspend fun deleteHomework(id: Long)
}


