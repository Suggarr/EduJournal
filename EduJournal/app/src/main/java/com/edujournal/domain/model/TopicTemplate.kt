package com.edujournal.domain.model

data class TopicTemplate(
    val id: Long,
    val semesterId: Long,
    val subjectLessonTypeId: Long,
    val title: String,
    val orderInType: Int
)


