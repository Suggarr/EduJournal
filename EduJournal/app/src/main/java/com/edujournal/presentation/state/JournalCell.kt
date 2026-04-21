package com.edujournal.presentation.state

import com.edujournal.domain.model.GradeType

data class JournalCell(
    val lessonId: Long,
    val value: String?,
    val gradeValue: Int?,
    val gradeType: GradeType?,
    val comment: String?
)
