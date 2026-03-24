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

    override suspend fun createSubject(subject: Subject) {
        localDataSource.insertSubject(subject.toEntity())
    }

    override suspend fun updateSubject(subject: Subject) {
        localDataSource.updateSubject(subject.toEntity())
    }

    override suspend fun deleteSubject(subjectId: Long) {
        localDataSource.deleteSubject(subjectId)
    }
}