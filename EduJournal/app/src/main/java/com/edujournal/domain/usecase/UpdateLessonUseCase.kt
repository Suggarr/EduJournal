package com.edujournal.domain.usecase

import com.edujournal.domain.model.Lesson
import com.edujournal.domain.repository.LessonRepository
import javax.inject.Inject

class UpdateLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lesson: Lesson) {
        lessonRepository.updateLesson(lesson)
    }
}
