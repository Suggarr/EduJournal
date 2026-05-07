package com.edujournal.domain.usecase.homework

import com.edujournal.domain.model.Homework
import com.edujournal.domain.repository.HomeworkRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class CreateHomeworkUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    suspend operator fun invoke(homework: Homework): EntityWriteResult {
        val normalizedText = homework.text.normalizeSpaces()
        require(normalizedText.isNotBlank()) { "HOMEWORK_TEXT_REQUIRED" }
        val id = repository.createHomework(homework.copy(text = normalizedText))
        return if (id == -1L) EntityWriteResult.DUPLICATE else EntityWriteResult.SUCCESS
    }
}




