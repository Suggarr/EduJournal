package com.edujournal.data.repository

import com.edujournal.data.local.datasource.SubjectLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.Subject
import com.edujournal.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubjectRepositoryImpl(
    private val localDataSource: SubjectLocalDataSource
) : SubjectRepository {

    override fun observeSubjects(): Flow<List<Subject>>{
        return localDataSource
            .observeSubjects()
            .map{list -> list.map { it.toDomain() } }
    }

    override suspend fun createSubject(subject: Subject): Long {
        return localDataSource.insertSubject(subject.toEntity())
    }

    override suspend fun updateSubject(subject: Subject): Int {
        return localDataSource.updateSubject(subject.toEntity())
    }

    override suspend fun existsById(id: Long): Boolean {
        return localDataSource.existsById(id)
    }

    override suspend fun existsByName(name: String): Boolean {
        return localDataSource.existsByName(name)
    }

    override suspend fun existsByNameExceptId(name: String, id: Long): Boolean {
        return localDataSource.existsByNameExceptId(name, id)
    }

    override suspend fun deleteSubject(subjectId: Long) {
        localDataSource.deleteSubject(subjectId)
    }
}
