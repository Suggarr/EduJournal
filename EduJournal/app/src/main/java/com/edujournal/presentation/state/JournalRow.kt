package com.edujournal.presentation.state

data class JournalRow(
    val studentId: Long,
    val studentName: String,
    val cells: List<JournalCell>,
    val averageText: String = "-",
    val absencesCount: Int = 0
)
