package com.edujournal.domain.usecase.lesson

import com.edujournal.domain.repository.LessonRepository
import javax.inject.Inject

class DeleteLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lessonId: Long) {
        lessonRepository.deleteLesson(lessonId)
    }
}




