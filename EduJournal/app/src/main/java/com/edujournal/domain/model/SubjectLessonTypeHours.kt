package com.edujournal.domain.model

data class SubjectLessonTypeHours(
    val id: Long = 0,
    val subjectId: Long,
    val lessonTypeId: Long,
    val hours: Double?
)
