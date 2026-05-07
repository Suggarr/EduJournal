package com.edujournal.domain.usecase.topictemplate

import android.database.sqlite.SQLiteConstraintException
import com.edujournal.domain.model.TopicTemplate
import com.edujournal.domain.repository.TopicTemplateRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class UpdateTopicTemplateUseCase @Inject constructor(
    private val repository: TopicTemplateRepository
) {
    suspend operator fun invoke(item: TopicTemplate): EntityWriteResult {
        if (item.orderInType <= 0) return EntityWriteResult.NOT_FOUND
        val normalized = item.copy(title = item.title.normalizeSpaces())
        return try {
            val updated = repository.update(normalized)
            if (updated > 0) EntityWriteResult.SUCCESS else EntityWriteResult.NOT_FOUND
        } catch (_: SQLiteConstraintException) {
            EntityWriteResult.DUPLICATE
        }
    }
}
