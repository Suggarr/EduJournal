package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Grade
import com.edujournal.domain.model.GradeType
import com.edujournal.domain.usecase.GetGroupsUseCase
import com.edujournal.domain.usecase.GetJournalUseCase
import com.edujournal.domain.usecase.GetLessonsUseCase
import com.edujournal.domain.usecase.ObserveHomeworkLessonIdsUseCase
import com.edujournal.domain.usecase.ObserveSubjectLessonTypeByIdUseCase
import com.edujournal.domain.usecase.ObserveSemestersUseCase
import com.edujournal.domain.usecase.ObserveSubjectsUseCase
import com.edujournal.domain.usecase.SetGradeUseCase
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalMetaState
import com.edujournal.presentation.state.JournalRow
import com.edujournal.presentation.state.JournalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val getJournalUseCase: GetJournalUseCase,
    private val getLessonsUseCase: GetLessonsUseCase,
    private val setGradeUseCase: SetGradeUseCase,
    private val observeHomeworkLessonIdsUseCase: ObserveHomeworkLessonIdsUseCase,
    private val observeLessonTypeByIdUseCase: ObserveSubjectLessonTypeByIdUseCase,
    private val observeSubjectsUseCase: ObserveSubjectsUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val observeSemestersUseCase: ObserveSemestersUseCase
) : ViewModel() {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun observeJournal(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): StateFlow<JournalState?> {

        return combine(
            getJournalUseCase(groupId, subjectLessonTypeId, semesterId),
            getLessonsUseCase(groupId, subjectLessonTypeId, semesterId)
        ) { journalRows, lessons -> journalRows to lessons }
            .flatMapLatest { (journalRows, lessons) ->
                val lessonIds = lessons.map { it.id }
                val homeworkIdsFlow = if (lessonIds.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    observeHomeworkLessonIdsUseCase(lessonIds)
                }

                homeworkIdsFlow.map { homeworkLessonIds ->
                    val grouped = journalRows.groupBy { it.studentId }

                    val rows = grouped.map { (_, studentRows) ->
                        val first = studentRows.first()
                        val cells = lessons.map { lesson ->
                            val rowForLesson = studentRows.firstOrNull { it.lessonId == lesson.id }
                            JournalCell(
                                lessonId = lesson.id,
                                value = formatCellValue(
                                    gradeValue = rowForLesson?.gradeValue,
                                    gradeType = rowForLesson?.gradeType
                                ),
                                gradeValue = rowForLesson?.gradeValue,
                                gradeType = rowForLesson?.gradeType?.let { GradeType.valueOf(it) },
                                comment = rowForLesson?.gradeComment
                            )
                        }
                        val numericValues = cells.mapNotNull { it.gradeValue }
                        val absencesCount = cells.count {
                            it.gradeType == GradeType.ABSENT ||
                                it.gradeType == GradeType.SICK ||
                                it.gradeType == GradeType.PASS
                        }

                        JournalRow(
                            studentId = first.studentId,
                            studentName = "${first.studentLastName} ${first.studentFirstName}",
                            cells = cells,
                            averageText = formatAverage(numericValues.takeIf { it.isNotEmpty() }?.average()),
                            absencesCount = absencesCount
                        )
                    }

                    val lessonAbsencesCounts = lessons.map { lesson ->
                        journalRows.count {
                            it.lessonId == lesson.id && (
                                it.gradeType == GradeType.ABSENT.name ||
                                    it.gradeType == GradeType.SICK.name ||
                                    it.gradeType == GradeType.PASS.name
                                )
                        }
                    }

                    JournalState(
                        lessons = lessons,
                        rows = rows,
                        homeworkLessonIds = homeworkLessonIds.toSet(),
                        lessonAbsencesCounts = lessonAbsencesCounts
                    )
                }
            }
            .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }

    fun observeJournalMeta(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): StateFlow<JournalMetaState?> {
        return combine(
            observeSubjectsUseCase(),
            observeLessonTypeByIdUseCase(subjectLessonTypeId),
            getGroupsUseCase(),
            observeSemestersUseCase()
        ) { subjects, SubjectLessonType, groups, semesters ->
            val subject = subjects.firstOrNull { it.id == SubjectLessonType?.subjectId }
            val group = groups.firstOrNull { it.id == groupId }
            val semester = semesters.firstOrNull { it.id == semesterId }

            JournalMetaState(
                subjectId = subject?.id,
                subjectLabel = subject?.abbreviation?.takeIf { it.isNotBlank() } ?: subject?.name.orEmpty(),
                lessonTypeLabel = SubjectLessonType?.name.orEmpty(),
                groupLabel = group?.name.orEmpty(),
                semesterSeason = semester?.season?.name,
                semesterYear = semester?.year
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }

    fun setNumericGrade(studentId: Long, lessonId: Long, value: Int, comment: String?) {
        viewModelScope.launch {
            setGradeUseCase(
                Grade(
                    id = 0,
                    studentId = studentId,
                    lessonId = lessonId,
                    value = value,
                    type = GradeType.GRADE,
                    comment = comment
                )
            )
        }
    }

    fun setGradeType(studentId: Long, lessonId: Long, type: GradeType, comment: String?) {
        viewModelScope.launch {
            setGradeUseCase(
                Grade(
                    id = 0,
                    studentId = studentId,
                    lessonId = lessonId,
                    value = null,
                    type = type,
                    comment = comment
                )
            )
        }
    }

    fun clearGrade(studentId: Long, lessonId: Long, comment: String?) {
        viewModelScope.launch {
            setGradeUseCase(
                Grade(
                    id = 0,
                    studentId = studentId,
                    lessonId = lessonId,
                    value = null,
                    type = GradeType.GRADE,
                    comment = comment
                )
            )
        }
    }

    fun setComment(
        studentId: Long,
        lessonId: Long,
        comment: String?,
        currentGradeValue: Int?,
        currentGradeType: GradeType?
    ) {
        viewModelScope.launch {
            val normalizedComment = comment?.trim()?.takeIf { it.isNotEmpty() }
            val normalizedType = currentGradeType ?: GradeType.GRADE
            val normalizedValue = if (normalizedType == GradeType.GRADE) currentGradeValue else null

            setGradeUseCase(
                Grade(
                    id = 0,
                    studentId = studentId,
                    lessonId = lessonId,
                    value = normalizedValue,
                    type = normalizedType,
                    comment = normalizedComment
                )
            )
        }
    }

    private fun formatCellValue(
        gradeValue: Int?,
        gradeType: String?
    ): String {
        return when {
            gradeValue != null -> gradeValue.toString()
            gradeType == GradeType.ABSENT.name -> "Н"
            gradeType == GradeType.SICK.name -> "З"
            gradeType == GradeType.PASS.name -> "О"
            else -> "-"
        }
    }

    private fun formatAverage(value: Double?): String {
        if (value == null || value.isNaN()) return "-"
        val rounded = kotlin.math.round(value * 10.0) / 10.0
        val asInt = rounded.toInt()
        return if (rounded == asInt.toDouble()) {
            asInt.toString()
        } else {
            rounded.toString().replace('.', ',')
        }
    }
}



