package com.edujournal.domain.model

import java.time.LocalDate

data class DisciplineGradeRecord(
    val studentId: Long,
    val studentFirstName: String,
    val studentLastName: String,
    val lessonId: Long?,
    val lessonDate: LocalDate?,
    val lessonTypeName: String?,
    val gradeValue: Int?,
    val gradeType: String?
)
