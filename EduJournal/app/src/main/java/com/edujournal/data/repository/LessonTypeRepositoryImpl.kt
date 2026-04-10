package com.edujournal.data.repository

import com.edujournal.data.local.datasource.LessonTypeLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.LessonType
import com.edujournal.domain.repository.LessonTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LessonTypeRepositoryImpl(
    private val localDataSource: LessonTypeLocalDataSource
) : LessonTypeRepository {
    override fun observeLessonTypes(): Flow<List<LessonType>> {
        return localDataSource
            .observeLessonTypes()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun createLessonType(lessonType: LessonType): Long {
        return localDataSource.insertLessonType(lessonType.toEntity())
    }

    override suspend fun updateLessonType(lessonType: LessonType): Int {
        return localDataSource.updateLessonType(lessonType.toEntity())
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

    override suspend fun deleteLessonType(typeId: Long) {
        localDataSource.deleteLessonType(typeId)
    }
}
