package com.edujournal.domain.usecase.lesson

import android.database.sqlite.SQLiteConstraintException
import com.edujournal.domain.model.Lesson
import com.edujournal.domain.repository.LessonRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class UpdateLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lesson: Lesson): EntityWriteResult {
        val normalizedTopic = lesson.topic.normalizeSpaces()
        require(normalizedTopic.isNotBlank()) { "TOPIC_REQUIRED" }
        val normalizedLesson = lesson.copy(topic = normalizedTopic)
        return try {
            val updatedRows = lessonRepository.updateLesson(normalizedLesson)
            if (updatedRows == 0) EntityWriteResult.NOT_FOUND else EntityWriteResult.SUCCESS
        } catch (_: SQLiteConstraintException) {
            EntityWriteResult.DUPLICATE
        }
    }
}




