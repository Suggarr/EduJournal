package com.edujournal.data.repository

import com.edujournal.data.local.datasource.SubjectLessonTypeLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubjectLessonTypeRepositoryImpl(
    private val localDataSource: SubjectLessonTypeLocalDataSource
) : SubjectLessonTypeRepository {
    override fun observeLessonTypes(subjectId: Long): Flow<List<SubjectLessonType>> {
        return localDataSource
            .observeLessonTypes(subjectId)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun observeById(id: Long): Flow<SubjectLessonType?> {
        return localDataSource.observeById(id).map { it?.toDomain() }
    }

    override suspend fun getById(id: Long): SubjectLessonType? {
        return localDataSource.getById(id)?.toDomain()
    }

    override suspend fun createLessonType(SubjectLessonType: SubjectLessonType): Long {
        return localDataSource.insertLessonType(SubjectLessonType.toEntity())
    }

    override suspend fun updateLessonType(SubjectLessonType: SubjectLessonType): Int {
        return localDataSource.updateLessonType(SubjectLessonType.toEntity())
    }

    override suspend fun deleteLessonType(typeId: Long) {
        localDataSource.deleteLessonType(typeId)
    }
}




