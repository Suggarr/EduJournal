package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Homework
import com.edujournal.domain.model.HomeworkSubmission
import com.edujournal.domain.model.enum.HomeworkSubmissionStatus
import com.edujournal.domain.usecase.homework.CreateHomeworkUseCase
import com.edujournal.domain.usecase.homework.DeleteHomeworkUseCase
import com.edujournal.domain.usecase.lesson.ObserveLessonByIdUseCase
import com.edujournal.domain.usecase.homeworksubmission.ObserveHomeworkSubmissionsUseCase
import com.edujournal.domain.usecase.homework.ObserveHomeworksUseCase
import com.edujournal.domain.usecase.student.ObserveStudentsUseCase
import com.edujournal.domain.usecase.homework.UpdateHomeworkUseCase
import com.edujournal.domain.usecase.homeworksubmission.UpsertHomeworkSubmissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

enum class HomeworkDisplayStatus {
    SUBMITTED,
    NOT_SUBMITTED
}

data class HomeworkStudentUi(
    val studentId: Long,
    val studentName: String,
    val storedStatus: HomeworkSubmissionStatus,
    val displayStatus: HomeworkDisplayStatus
)

data class HomeworkUiState(
    val lessonDate: LocalDate,
    val homework: Homework?,
    val students: List<HomeworkStudentUi>
)

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    private val observeLessonByIdUseCase: ObserveLessonByIdUseCase,
    private val observeHomeworksUseCase: ObserveHomeworksUseCase,
    private val observeStudentsUseCase: ObserveStudentsUseCase,
    private val observeHomeworkSubmissionsUseCase: ObserveHomeworkSubmissionsUseCase,
    private val createHomeworkUseCase: CreateHomeworkUseCase,
    private val updateHomeworkUseCase: UpdateHomeworkUseCase,
    private val deleteHomeworkUseCase: DeleteHomeworkUseCase,
    private val upsertHomeworkSubmissionUseCase: UpsertHomeworkSubmissionUseCase
) : ViewModel() {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun observeState(lessonId: Long): StateFlow<HomeworkUiState?> {
        return observeLessonByIdUseCase(lessonId).flatMapLatest { lesson ->
            if (lesson == null) {
                return@flatMapLatest flowOf(null)
            }

            combine(
                observeHomeworksUseCase(lessonId),
                observeStudentsUseCase(lesson.groupId)
            ) { homework, students -> homework to students }
                .flatMapLatest { (homework, students) ->
                    val submissionsFlow: Flow<List<HomeworkSubmission>> =
                        if (homework != null)
                            observeHomeworkSubmissionsUseCase(homework.id)
                        else flowOf(emptyList())

                    submissionsFlow.map { submissions ->
                        val submissionsByStudent = submissions.associateBy { it.studentId }
                        HomeworkUiState(
                            lessonDate = lesson.date,
                            homework = homework,
                            students = students.map { student ->
                                val submission = submissionsByStudent[student.id]
                                val storedStatus = submission?.status
                                    ?: HomeworkSubmissionStatus.NOT_SUBMITTED
                                val displayStatus = when {
                                    storedStatus == HomeworkSubmissionStatus.SUBMITTED
                                        -> HomeworkDisplayStatus.SUBMITTED
                                    else -> HomeworkDisplayStatus.NOT_SUBMITTED
                                }

                                HomeworkStudentUi(
                                    studentId = student.id,
                                    studentName =
                                        "${student.lastName} ${student.firstName} ${student.middleName}",
                                    storedStatus = storedStatus,
                                    displayStatus = displayStatus
                                )
                            }
                        )
                    }
                }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }

    fun addHomework(
        lessonId: Long,
        text: String
    ) {
        viewModelScope.launch {
            createHomeworkUseCase(
                Homework(
                    id = 0,
                    lessonId = lessonId,
                    text = text.trim()
                )
            )
        }
    }

    fun updateHomework(homework: Homework) {
        viewModelScope.launch {
            updateHomeworkUseCase(homework.copy(text = homework.text.trim()))
        }
    }

    fun deleteHomework(homeworkId: Long) {
        viewModelScope.launch {
            deleteHomeworkUseCase(homeworkId)
        }
    }

    fun setStudentSubmitted(
        homeworkId: Long,
        studentId: Long
    ) {
        upsertSubmission(
            homeworkId = homeworkId,
            studentId = studentId,
            status = HomeworkSubmissionStatus.SUBMITTED
        )
    }

    fun setStudentNotSubmitted(
        homeworkId: Long,
        studentId: Long
    ) {
        upsertSubmission(
            homeworkId = homeworkId,
            studentId = studentId,
            status = HomeworkSubmissionStatus.NOT_SUBMITTED
        )
    }

    private fun upsertSubmission(
        homeworkId: Long,
        studentId: Long,
        status: HomeworkSubmissionStatus
    ) {
        viewModelScope.launch {
            upsertHomeworkSubmissionUseCase(
                HomeworkSubmission(
                    id = 0,
                    homeworkId = homeworkId,
                    studentId = studentId,
                    status = status
                )
            )
        }
    }
}
