package com.edujournal.domain.usecase

import com.edujournal.domain.model.HomeworkSubmission
import com.edujournal.domain.repository.HomeworkSubmissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHomeworkSubmissionsUseCase @Inject constructor(
    private val repository: HomeworkSubmissionRepository
) {
    operator fun invoke(homeworkId: Long): Flow<List<HomeworkSubmission>> {
        return repository.observeSubmissions(homeworkId)
    }
}
