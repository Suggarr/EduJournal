package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.TopicTemplateEntity
import com.edujournal.domain.model.TopicTemplate

fun TopicTemplateEntity.toDomain(): TopicTemplate {
    return TopicTemplate(
        id = id,
        semesterId = semesterId,
        subjectLessonTypeId = subjectLessonTypeId,
        title = title,
        orderInType = orderInType
    )
}

fun TopicTemplate.toEntity(): TopicTemplateEntity {
    return TopicTemplateEntity(
        id = id,
        semesterId = semesterId,
        subjectLessonTypeId = subjectLessonTypeId,
        title = title,
        orderInType = orderInType
    )
}


