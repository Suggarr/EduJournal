package com.edujournal.domain.usecase

import com.edujournal.domain.model.Homework
import com.edujournal.domain.repository.HomeworkRepository
import javax.inject.Inject

class UpdateHomeworkUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    suspend operator fun invoke(homework: Homework): Int {
        return repository.updateHomework(homework)
    }
}
