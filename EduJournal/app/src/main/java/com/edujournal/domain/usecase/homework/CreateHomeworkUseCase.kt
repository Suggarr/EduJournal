package com.edujournal.domain.usecase.homework

import com.edujournal.domain.model.Homework
import com.edujournal.domain.repository.HomeworkRepository
import javax.inject.Inject

class CreateHomeworkUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    suspend operator fun invoke(homework: Homework): Long {
        return repository.createHomework(homework)
    }
}




