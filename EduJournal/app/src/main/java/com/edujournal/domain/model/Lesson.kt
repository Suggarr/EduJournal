package com.edujournal.domain.model

import java.time.LocalDate

data class Lesson(
    val id: Long,
    val groupId: Long,
    val subjectLessonTypeId: Long,
    val semesterId: Long = 1L,
    val date: LocalDate,
    val topic: String
)

