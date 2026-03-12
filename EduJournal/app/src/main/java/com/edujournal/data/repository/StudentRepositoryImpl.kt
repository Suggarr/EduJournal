package com.edujournal.data.repository

import com.edujournal.data.local.datasource.StudentLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudentRepositoryImpl(
    private val localDataSource: StudentLocalDataSource
) : StudentRepository {

    override fun observeStudents(groupId: Long): Flow<List<Student>> {
        return localDataSource
            .observeStudents(groupId)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun createStudent(student: Student) {
        localDataSource.insert(student.toEntity())
    }
}