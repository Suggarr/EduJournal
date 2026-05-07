package com.edujournal.domain.usecase.homework

import android.database.sqlite.SQLiteConstraintException
import com.edujournal.domain.model.Homework
import com.edujournal.domain.repository.HomeworkRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class UpdateHomeworkUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    suspend operator fun invoke(homework: Homework): EntityWriteResult {
        val normalizedText = homework.text.normalizeSpaces()
        require(normalizedText.isNotBlank()) { "HOMEWORK_TEXT_REQUIRED" }
        return try {
            val updatedRows = repository.updateHomework(homework.copy(text = normalizedText))
            if (updatedRows == 0) EntityWriteResult.NOT_FOUND else EntityWriteResult.SUCCESS
        } catch (_: SQLiteConstraintException) {
            EntityWriteResult.DUPLICATE
        }
    }
}




