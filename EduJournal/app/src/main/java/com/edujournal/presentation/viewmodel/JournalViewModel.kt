package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Grade
import com.edujournal.domain.model.GradeType
import com.edujournal.domain.usecase.GetJournalUseCase
import com.edujournal.domain.usecase.GetLessonsUseCase
import com.edujournal.domain.usecase.SetGradeUseCase
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalRow
import com.edujournal.presentation.state.JournalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val getJournalUseCase: GetJournalUseCase,
    private val getLessonsUseCase: GetLessonsUseCase,
    private val setGradeUseCase: SetGradeUseCase
) : ViewModel() {

    fun observeJournal(groupId: Long): StateFlow<JournalState> {

        return combine(
            getJournalUseCase(groupId),
            getLessonsUseCase()
        ) { journalRows, lessons ->

            val grouped = journalRows.groupBy { it.studentId }

            val rows = grouped.map { (_, studentRows) ->

                val first = studentRows.first()

                JournalRow(
                    studentId = first.studentId,
                    studentName = "${first.studentFirstName} ${first.studentLastName}",

                    cells = studentRows.map {

                        JournalCell(
                            lessonId = it.lessonId,
                            value = it.gradeValue?.toString() ?: it.gradeType
                        )

                    }
                )
            }

            JournalState(
                lessons = lessons,
                rows = rows
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            JournalState()
        )
    }

    fun setGrade(studentId: Long, lessonId: Long, value: Int) {
        viewModelScope.launch {
            setGradeUseCase(
                Grade(
                    id = 0, // Room auto-increment if handled
                    studentId = studentId,
                    lessonId = lessonId,
                    value = value,
                    type = GradeType.GRADE,
                    comment = null
                )
            )
        }
    }
}