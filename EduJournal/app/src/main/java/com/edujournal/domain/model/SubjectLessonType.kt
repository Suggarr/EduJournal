package com.edujournal.domain.model

data class SubjectLessonType(
    val id: Long,
    val subjectId: Long,
    val name: String,
    val hours: Double?
)



