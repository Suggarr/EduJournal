package com.edujournal.domain.usecase

import com.edujournal.domain.repository.HomeworkRepository
import javax.inject.Inject

class DeleteHomeworkUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteHomework(id)
    }
}
