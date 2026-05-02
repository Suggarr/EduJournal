package com.edujournal.domain.usecase.homework

import com.edujournal.domain.model.Homework
import com.edujournal.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHomeworksUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    operator fun invoke(lessonId: Long): Flow<Homework?> {
        return repository.observeHomework(lessonId)
    }
}




