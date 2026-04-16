package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Lesson
import com.edujournal.domain.usecase.CreateLessonUseCase
import com.edujournal.domain.usecase.DeleteLessonUseCase
import com.edujournal.domain.usecase.GetLessonsUseCase
import com.edujournal.domain.usecase.ObserveSubjectLessonTypeByIdUseCase
import com.edujournal.domain.usecase.UpdateLessonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LessonTopicsViewModel @Inject constructor(
    private val getLessonsUseCase: GetLessonsUseCase,
    private val observeLessonTypeByIdUseCase: ObserveSubjectLessonTypeByIdUseCase,
    private val createLessonUseCase: CreateLessonUseCase,
    private val updateLessonUseCase: UpdateLessonUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase
) : ViewModel() {

    fun observeLessons(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): StateFlow<List<Lesson>?> {
        return getLessonsUseCase(groupId, subjectLessonTypeId, semesterId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun observeRequiredHours(
        subjectLessonTypeId: Long
    ): StateFlow<Double?> {
        return observeLessonTypeByIdUseCase(subjectLessonTypeId)
            .map { it?.hours }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun addLesson(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long,
        date: LocalDate,
        topic: String
    ) {
        viewModelScope.launch {
            runCatching {
                createLessonUseCase(
                    Lesson(
                        id = 0,
                        groupId = groupId,
                        subjectLessonTypeId = subjectLessonTypeId,
                        semesterId = semesterId,
                        date = date,
                        topic = topic
                    )
                )
            }
        }
    }

    fun updateLesson(lesson: Lesson) {
        viewModelScope.launch {
            runCatching {
                updateLessonUseCase(lesson)
            }
        }
    }

    fun deleteLesson(lessonId: Long) {
        viewModelScope.launch {
            deleteLessonUseCase(lessonId)
        }
    }
}


