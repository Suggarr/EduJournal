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
            .map{list -> list.map{ it.toDomain()}}

    }

    override suspend fun createLessonType(lessonType: LessonType){
        localDataSource.insert(lessonType.toEntity())
    }
}