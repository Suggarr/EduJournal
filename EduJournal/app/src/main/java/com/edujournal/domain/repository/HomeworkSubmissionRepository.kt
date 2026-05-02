package com.edujournal.domain.repository

import com.edujournal.domain.model.HomeworkSubmission
import kotlinx.coroutines.flow.Flow

interface HomeworkSubmissionRepository {
    fun observeSubmissions(homeworkId: Long): Flow<List<HomeworkSubmission>>

    suspend fun upsertSubmission(submission: HomeworkSubmission)
}


