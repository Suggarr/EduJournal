package com.edujournal.domain.repository

import com.edujournal.domain.model.SubjectLessonTypeHours
import kotlinx.coroutines.flow.Flow

interface SubjectLessonTypeHoursRepository {
    fun observeAll(): Flow<List<SubjectLessonTypeHours>>

    suspend fun getBySubjectId(subjectId: Long): List<SubjectLessonTypeHours>

    suspend fun replaceForSubject(
        subjectId: Long,
        items: List<SubjectLessonTypeHours>
    )
}
