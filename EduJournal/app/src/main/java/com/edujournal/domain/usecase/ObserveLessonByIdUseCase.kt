package com.edujournal.domain.usecase

import com.edujournal.domain.model.Lesson
import com.edujournal.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLessonByIdUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(lessonId: Long): Flow<Lesson?> {
        return lessonRepository.observeLessonById(lessonId)
    }
}
