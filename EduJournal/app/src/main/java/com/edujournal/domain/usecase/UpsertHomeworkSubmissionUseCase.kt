package com.edujournal.domain.usecase

import com.edujournal.domain.model.HomeworkSubmission
import com.edujournal.domain.repository.HomeworkSubmissionRepository
import javax.inject.Inject

class UpsertHomeworkSubmissionUseCase @Inject constructor(
    private val repository: HomeworkSubmissionRepository
) {
    suspend operator fun invoke(submission: HomeworkSubmission) {
        repository.upsertSubmission(submission)
    }
}
