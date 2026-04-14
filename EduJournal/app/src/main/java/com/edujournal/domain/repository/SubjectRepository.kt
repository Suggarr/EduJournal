package com.edujournal.domain.repository

import com.edujournal.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun observeSubjects(): Flow<List<Subject>>
    fun observeSubjectsBySemester(semesterId: Long): Flow<List<Subject>>
    fun observeSemesterIdsBySubject(subjectId: Long): Flow<List<Long>>

    suspend fun createSubject(subject: Subject): Long

    suspend fun updateSubject(subject: Subject): Int
    suspend fun replaceSubjectSemesters(subjectId: Long, semesterIds: List<Long>)
    suspend fun existsById(id: Long): Boolean
    suspend fun existsByName(name: String): Boolean
    suspend fun existsByNameExceptId(name: String, id: Long): Boolean

    suspend fun deleteSubject(subjectId: Long)
}
