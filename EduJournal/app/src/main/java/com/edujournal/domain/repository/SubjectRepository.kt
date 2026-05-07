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

    suspend fun deleteSubject(subjectId: Long)
}


