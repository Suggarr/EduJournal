package com.edujournal.domain.usecase.topictemplate

import com.edujournal.domain.repository.TopicTemplateRepository
import javax.inject.Inject

class DeleteTopicTemplateUseCase @Inject constructor(
    private val repository: TopicTemplateRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteById(id)
}




