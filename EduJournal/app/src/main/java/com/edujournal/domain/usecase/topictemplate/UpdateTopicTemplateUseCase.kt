package com.edujournal.domain.usecase.topictemplate
import com.edujournal.domain.usecase.common.EntityWriteResult

import com.edujournal.domain.model.TopicTemplate
import com.edujournal.domain.repository.TopicTemplateRepository
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class UpdateTopicTemplateUseCase @Inject constructor(
    private val repository: TopicTemplateRepository
) {
    suspend operator fun invoke(item: TopicTemplate): EntityWriteResult {
        if (item.orderInType <= 0) return EntityWriteResult.NOT_FOUND
        val normalized = item.copy(title = item.title.normalizeSpaces())
        if (repository.existsOrder(normalized.semesterId, normalized.subjectLessonTypeId, normalized.orderInType, normalized.id)) {
            return EntityWriteResult.DUPLICATE
        }
        val updated = repository.update(normalized)
        return if (updated > 0) EntityWriteResult.SUCCESS else EntityWriteResult.NOT_FOUND
    }
}




