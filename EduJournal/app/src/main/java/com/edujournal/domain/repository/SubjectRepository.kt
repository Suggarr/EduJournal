package com.edujournal.domain.repository

import com.edujournal.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun observeSubjects(): Flow<List<Subject>>

    suspend fun createSubject(subject: Subject): Long

    suspend fun updateSubject(subject: Subject)

    suspend fun deleteSubject(subjectId: Long)
}
