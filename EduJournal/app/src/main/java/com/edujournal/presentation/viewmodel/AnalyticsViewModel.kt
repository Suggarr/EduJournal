package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.GradeType
import com.edujournal.domain.usecase.GetGroupsUseCase
import com.edujournal.domain.usecase.ObserveDisciplineGradesUseCase
import com.edujournal.domain.usecase.ObserveSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StudentDisciplineAverage(
    val studentId: Long,
    val studentName: String,
    val average: Double?,
    val gradedCount: Int
)

data class DisciplineAnalyticsUiState(
    val subjectName: String,
    val groupName: String,
    val ranking: List<StudentDisciplineAverage>,
    val debtors: List<StudentDisciplineAverage>,
    val debtorThreshold: Double
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val observeDisciplineGradesUseCase: ObserveDisciplineGradesUseCase,
    private val observeSubjectsUseCase: ObserveSubjectsUseCase,
    private val getGroupsUseCase: GetGroupsUseCase
) : ViewModel() {

    fun observeState(
        groupId: Long,
        subjectId: Long,
        semesterId: Long,
        debtorThreshold: Double = 4.0
    ): StateFlow<DisciplineAnalyticsUiState?> {
        return combine(
            observeDisciplineGradesUseCase(groupId, subjectId, semesterId),
            observeSubjectsUseCase(),
            getGroupsUseCase()
        ) { gradeRows, subjects, groups ->
            val subjectName = subjects.firstOrNull { it.id == subjectId }?.name.orEmpty()
            val groupName = groups.firstOrNull { it.id == groupId }?.name.orEmpty()

            val groupedByStudent = gradeRows.groupBy { it.studentId }
            val ranking = groupedByStudent.map { (studentId, rows) ->
                val first = rows.first()
                val studentName = "${first.studentLastName} ${first.studentFirstName}"

                val numericRows = rows.filter { row ->
                    row.gradeValue != null && row.gradeType == GradeType.GRADE.name
                }
                val average = numericRows.mapNotNull { it.gradeValue }.average().takeIf { numericRows.isNotEmpty() }

                StudentDisciplineAverage(
                    studentId = studentId,
                    studentName = studentName,
                    average = average,
                    gradedCount = numericRows.size
                )
            }.sortedWith(
                compareByDescending<StudentDisciplineAverage> { it.average ?: Double.NEGATIVE_INFINITY }
                    .thenBy { it.studentName }
            )

            val debtors = ranking.filter { it.average != null && it.average < debtorThreshold }

            DisciplineAnalyticsUiState(
                subjectName = subjectName,
                groupName = groupName,
                ranking = ranking,
                debtors = debtors,
                debtorThreshold = debtorThreshold
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }
}
