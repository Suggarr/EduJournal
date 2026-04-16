package com.edujournal.presentation.state

data class JournalMetaState(
    val subjectId: Long? = null,
    val subjectLabel: String,
    val lessonTypeLabel: String,
    val groupLabel: String,
    val semesterSeason: String? = null,
    val semesterYear: Int? = null
)
