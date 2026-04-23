package com.edujournal.presentation.state

import com.edujournal.domain.model.Lesson

data class JournalState(
    val lessons: List<Lesson> = emptyList(),
    val rows: List<JournalRow> = emptyList(),
    val homeworkLessonIds: Set<Long> = emptySet(),
    val lessonAbsencesCounts: List<Int> = emptyList()
)
