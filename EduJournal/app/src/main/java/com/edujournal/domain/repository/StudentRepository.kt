package com.edujournal.domain.repository

import com.edujournal.domain.model.Student
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun observeStudents(groupId: Long): Flow<List<Student>>

    suspend fun createStudent(student: Student): Long

    suspend fun updateStudent(student: Student): Int

    suspend fun deleteStudent(studentId: Long)
}


