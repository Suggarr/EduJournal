
package com.edujournal.domain.model

import com.edujournal.domain.model.enum.GradeType

data class Grade(
    val id: Long,
    val studentId: Long,
    val lessonId: Long,
    val value: Int?,
    val type: GradeType,
    val comment: String?
)


