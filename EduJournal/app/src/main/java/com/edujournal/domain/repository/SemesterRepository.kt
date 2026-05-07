package com.edujournal.domain.repository

import com.edujournal.domain.model.Semester
import kotlinx.coroutines.flow.Flow

interface SemesterRepository {
    fun observeSemesters(): Flow<List<Semester>>
    suspend fun createSemester(semester: Semester): Long
    suspend fun updateSemester(semester: Semester): Int
    suspend fun deleteSemester(semesterId: Long)
}


