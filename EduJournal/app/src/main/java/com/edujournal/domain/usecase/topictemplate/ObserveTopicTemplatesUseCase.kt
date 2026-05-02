package com.edujournal.domain.usecase.topictemplate

import com.edujournal.domain.repository.TopicTemplateRepository
import javax.inject.Inject

class ObserveTopicTemplatesUseCase @Inject constructor(
    private val repository: TopicTemplateRepository
) {
    operator fun invoke(semesterId: Long, subjectLessonTypeId: Long) =
        repository.observeByContext(semesterId, subjectLessonTypeId)
}




