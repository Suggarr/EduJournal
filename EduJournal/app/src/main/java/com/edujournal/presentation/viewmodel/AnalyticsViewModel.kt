package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.GradeType
import com.edujournal.domain.usecase.GetGroupsUseCase
import com.edujournal.domain.usecase.ObserveDisciplineGradesUseCase
import com.edujournal.domain.usecase.ObserveSubjectLessonTypesUseCase
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
    val groupAverage: Double?,
    val studentsCount: Int,
    val lessonsCount: Int,
    val plannedHours: Double?,
    val attendanceMarkedCount: Int,
    val attendanceAbsentCount: Int,
    val attendanceSickCount: Int,
    val attendancePassCount: Int,
    val attendancePresentCount: Int,
    val attendancePercent: Double?,
    val overTime: List<GradeOverTimePoint>,
    val gradeDistribution: List<GradeDistributionItem>,
    val typeSummaries: List<TypeAnalyticsSummary>,
    val ranking: List<StudentDisciplineAverage>,
    val debtors: List<StudentDisciplineAverage>,
    val debtorThreshold: Double
)

data class GradeOverTimePoint(
    val label: String,
    val average: Double
)

data class GradeDistributionItem(
    val grade: Int,
    val count: Int
)

data class TypeAnalyticsSummary(
    val typeName: String,
    val plannedHours: Double?,
    val lessonsCount: Int
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val observeDisciplineGradesUseCase: ObserveDisciplineGradesUseCase,
    private val observeSubjectsUseCase: ObserveSubjectsUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val observeSubjectLessonTypesUseCase: ObserveSubjectLessonTypesUseCase
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
            getGroupsUseCase(),
            observeSubjectLessonTypesUseCase(subjectId)
        ) { gradeRows, subjects, groups, lessonTypes ->
            val subjectName = subjects.firstOrNull { it.id == subjectId }?.name.orEmpty()
            val groupName = groups.firstOrNull { it.id == groupId }?.name.orEmpty()

            val groupedByStudent = gradeRows.groupBy { it.studentId }

            val studentsCount = groupedByStudent.size
            val lessonsCount = gradeRows.mapNotNull { it.lessonId }.toSet().size

            val plannedHours = lessonTypes
                .mapNotNull { it.hours }
                .takeIf { it.isNotEmpty() }
                ?.sum()

            val gradeRowsWithMark = gradeRows.filter { it.gradeType != null }
            val attendanceMarkedCount = gradeRowsWithMark.size
            val attendanceAbsentCount = gradeRowsWithMark.count { it.gradeType == GradeType.ABSENT.name }
            val attendanceSickCount = gradeRowsWithMark.count { it.gradeType == GradeType.SICK.name }
            val attendancePassCount = gradeRowsWithMark.count { it.gradeType == GradeType.PASS.name }
            val attendancePresentCount = gradeRowsWithMark.count { it.gradeType == GradeType.GRADE.name }
            val attendancePercent = attendanceMarkedCount
                .takeIf { it > 0 }
                ?.let { attendancePresentCount * 100.0 / it }

            val allNumericValues = gradeRows.mapNotNull { row ->
                row.gradeValue?.takeIf { row.gradeType == GradeType.GRADE.name }
            }
            val groupAverage = allNumericValues
                .takeIf { it.isNotEmpty() }
                ?.average()

            val overTime = gradeRows
                .filter { row ->
                    row.lessonDate != null &&
                        row.gradeType == GradeType.GRADE.name &&
                        row.gradeValue != null
                }
                .groupBy { it.lessonDate!! }
                .toSortedMap()
                .map { (date, rows) ->
                    GradeOverTimePoint(
                        label = date.toString(),
                        average = rows.mapNotNull { it.gradeValue }.average()
                    )
                }

            val distributionRaw = allNumericValues
                .groupingBy { it }
                .eachCount()
            val gradeDistribution = (1..10).map { grade ->
                GradeDistributionItem(
                    grade = grade,
                    count = distributionRaw[grade] ?: 0
                )
            }

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

            val lessonIdsByType = gradeRows
                .filter { !it.lessonTypeName.isNullOrBlank() }
                .groupBy { it.lessonTypeName!! }
                .mapValues { (_, rows) -> rows.mapNotNull { it.lessonId }.toSet().size }

            val typeSummaries = lessonTypes.map { type ->
                TypeAnalyticsSummary(
                    typeName = type.name,
                    plannedHours = type.hours,
                    lessonsCount = lessonIdsByType[type.name] ?: 0
                )
            }.sortedBy { it.typeName }

            DisciplineAnalyticsUiState(
                subjectName = subjectName,
                groupName = groupName,
                groupAverage = groupAverage,
                studentsCount = studentsCount,
                lessonsCount = lessonsCount,
                plannedHours = plannedHours,
                attendanceMarkedCount = attendanceMarkedCount,
                attendanceAbsentCount = attendanceAbsentCount,
                attendanceSickCount = attendanceSickCount,
                attendancePassCount = attendancePassCount,
                attendancePresentCount = attendancePresentCount,
                attendancePercent = attendancePercent,
                overTime = overTime,
                gradeDistribution = gradeDistribution,
                typeSummaries = typeSummaries,
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
