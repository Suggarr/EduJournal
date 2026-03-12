package com.edujournal.domain.model

import java.time.LocalDate

data class Lesson(
    val id: Long,
    val groupId: Long,
    val subjectId: Long,
    val lessonTypeId: Long,
    val date: LocalDate,
    val topic: String
)
