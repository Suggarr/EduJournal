package com.edujournal.domain.usecase.lesson

import com.edujournal.domain.model.Lesson
import com.edujournal.domain.repository.LessonRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class CreateLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lesson: Lesson): EntityWriteResult {
        val normalizedTopic = lesson.topic.normalizeSpaces()
        require(normalizedTopic.isNotBlank()) { "TOPIC_REQUIRED" }
        val normalizedLesson = lesson.copy(topic = normalizedTopic)
        val id = lessonRepository.insertLesson(normalizedLesson)
        return if (id == -1L) EntityWriteResult.DUPLICATE else EntityWriteResult.SUCCESS
    }
}




