package com.edujournal.domain.usecase.lesson

import com.edujournal.domain.model.Lesson
import com.edujournal.domain.repository.LessonRepository
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class UpdateLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lesson: Lesson) {
        val normalizedLesson = lesson.copy(topic = lesson.topic.normalizeSpaces())
        lessonRepository.updateLesson(normalizedLesson)
    }
}




