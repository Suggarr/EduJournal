package com.edujournal.domain.model

data class JournalRow (
    val studentId: Long,

    val studentFirstName: String,

    val studentLastName: String,

    val lessonId: Long,

    val gradeValue: Int?,

    val gradeType: String?
)