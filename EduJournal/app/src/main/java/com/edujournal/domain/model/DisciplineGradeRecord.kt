package com.edujournal.domain.model

data class DisciplineGradeRecord(
    val studentId: Long,
    val studentFirstName: String,
    val studentLastName: String,
    val lessonTypeName: String?,
    val gradeValue: Int?,
    val gradeType: String?
)
