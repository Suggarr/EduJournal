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

    override suspend fun createStudent(student: Student): Long {
        return localDataSource.insert(student.toEntity())
    }

    override suspend fun updateStudent(student: Student): Int {
        return localDataSource.update(student.toEntity())
    }

    override suspend fun existsById(id: Long): Boolean {
        return localDataSource.existsById(id)
    }

    override suspend fun existsByFullNameInGroup(
        groupId: Long,
        lastName: String,
        firstName: String,
        middleName: String
    ): Boolean {
        return localDataSource.existsByFullNameInGroup(groupId, lastName, firstName, middleName)
    }

    override suspend fun existsByFullNameInGroupExceptId(
        id: Long,
        groupId: Long,
        lastName: String,
        firstName: String,
        middleName: String
    ): Boolean {
        return localDataSource.existsByFullNameInGroupExceptId(id, groupId, lastName, firstName, middleName)
    }

    override suspend fun deleteStudent(studentId: Long) {
        localDataSource.delete(studentId)
    }
}
