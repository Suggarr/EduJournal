package com.edujournal.data.repository

import com.edujournal.data.local.datasource.HomeworkSubmissionLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.HomeworkSubmission
import com.edujournal.domain.repository.HomeworkSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeworkSubmissionRepositoryImpl(
    private val localDataSource: HomeworkSubmissionLocalDataSource
) : HomeworkSubmissionRepository {

    override fun observeSubmissions(homeworkId: Long): Flow<List<HomeworkSubmission>> {
        return localDataSource.observeSubmissions(homeworkId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun upsertSubmission(submission: HomeworkSubmission) {
        localDataSource.upsert(submission.toEntity())
    }
}
