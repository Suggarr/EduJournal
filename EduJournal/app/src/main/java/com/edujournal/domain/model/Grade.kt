package com.edujournal.domain.model

data class Grade(
    val id: Long,
    val studentId: Long,
    val lessonId: Long,
    val value: Int?,
    val type: GradeType,
    val comment: String?
)