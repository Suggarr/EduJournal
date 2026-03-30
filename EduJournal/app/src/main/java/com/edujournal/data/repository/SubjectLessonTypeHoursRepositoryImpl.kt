package com.edujournal.data.repository

import com.edujournal.data.local.datasource.SubjectLessonTypeHoursLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.SubjectLessonTypeHours
import com.edujournal.domain.repository.SubjectLessonTypeHoursRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubjectLessonTypeHoursRepositoryImpl(
    private val localDataSource: SubjectLessonTypeHoursLocalDataSource
) : SubjectLessonTypeHoursRepository {

    override fun observeAll(): Flow<List<SubjectLessonTypeHours>> {
        return localDataSource
            .observeAll()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getBySubjectId(subjectId: Long): List<SubjectLessonTypeHours> {
        return localDataSource.getBySubjectId(subjectId).map { it.toDomain() }
    }

    override suspend fun replaceForSubject(
        subjectId: Long,
        items: List<SubjectLessonTypeHours>
    ) {
        localDataSource.replaceForSubject(subjectId, items.map { it.toEntity() })
    }
}
