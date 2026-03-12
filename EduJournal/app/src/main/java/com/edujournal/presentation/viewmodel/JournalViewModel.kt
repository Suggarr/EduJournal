package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.usecase.GetJournalUseCase
import com.edujournal.domain.usecase.GetLessonsUseCase
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalRow
import com.edujournal.presentation.state.JournalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val getJournalUseCase: GetJournalUseCase,
    private val getLessonsUseCase: GetLessonsUseCase
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
}
//@HiltViewModel
//class JournalViewModel @Inject constructor(
//    private val observeStudentsUseCase: ObserveStudentsUseCase,
//    private val getLessonsUseCase: GetLessonsUseCase,
//    private val getGradesForLessonUseCase: GetGradesForLessonUseCase
//) : ViewModel() {
//
//    fun observeJournal(groupId: Long): StateFlow<JournalState> {
//
//        return combine(
//            observeStudentsUseCase(groupId),
//            getLessonsUseCase()
//        ) { students, lessons ->
//
//            val rows = students.map { student ->
//
//                val cells = lessons.map { lesson ->
//
//                    JournalCell(
//                        lessonId = lesson.id,
//                        value = null
//                    )
//
//                }
//
//                JournalRow(
//                    studentId = student.id,
//                    studentName = student.firstName + " " + student.lastName,
//                    cells = cells
//                )
//            }
//
//            JournalState(
//                lessons = lessons,
//                rows = rows
//            )
//
//        }.stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5000),
//            JournalState()
//        )
//    }
//}

//package com.edujournal.presentation.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.edujournal.domain.usecase.GetLessonsUseCase
//import com.edujournal.domain.usecase.GetGradesForLessonUseCase
//import com.edujournal.domain.usecase.ObserveStudentsUseCase
//import com.edujournal.presentation.state.JournalCell
//import com.edujournal.presentation.state.JournalRow
//import com.edujournal.presentation.state.JournalState
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.map
//
//@HiltViewModel
//class JournalViewModel @Inject constructor(
//
//    private val observeStudentsUseCase: ObserveStudentsUseCase,
//
//    private val getLessonsUseCase: GetLessonsUseCase,
//
//    private val getGradesForLessonUseCase: GetGradesForLessonUseCase
//
//) : ViewModel() {
//
//    private val _state = MutableStateFlow(JournalState())
//    val state: StateFlow<JournalState> = _state
//
//    fun loadJournal(groupId: Long) {
//
//        viewModelScope.launch {
//
//            observeStudentsUseCase(groupId).collect { students ->
//
//                getLessonsUseCase().collect { lessons ->
//
//                    val rows = students.map { student ->
//
//                        val cells = lessons.map { lesson ->
//
//                            JournalCell(
//                                lessonId = lesson.id,
//                                value = null
//                            )
//
//                        }
//
//                        JournalRow(
//                            studentId = student.id,
//                            studentName = student.firstName + " " + student.lastName,
//                            cells = cells
//                        )
//
//                    }
//
//                    _state.value = JournalState(
//                        lessons = lessons,
//                        rows = rows
//                    )
//
//                }
//
//            }
//
//        }
//
//    }
//
//}
