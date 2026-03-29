package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Lesson
import com.edujournal.domain.usecase.CreateLessonUseCase
import com.edujournal.domain.usecase.DeleteLessonUseCase
import com.edujournal.domain.usecase.GetLessonsUseCase
import com.edujournal.domain.usecase.UpdateLessonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LessonTopicsViewModel @Inject constructor(
    private val getLessonsUseCase: GetLessonsUseCase,
    private val createLessonUseCase: CreateLessonUseCase,
    private val updateLessonUseCase: UpdateLessonUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase
) : ViewModel() {

    fun observeLessons(
        groupId: Long,
        subjectId: Long,
        lessonTypeId: Long
    ): StateFlow<List<Lesson>> {
        return getLessonsUseCase(groupId, subjectId, lessonTypeId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addLesson(
        groupId: Long,
        subjectId: Long,
        lessonTypeId: Long,
        date: LocalDate,
        topic: String
    ) {
        viewModelScope.launch {
            createLessonUseCase(
                Lesson(
                    id = 0,
                    groupId = groupId,
                    subjectId = subjectId,
                    lessonTypeId = lessonTypeId,
                    date = date,
                    topic = topic
                )
            )
        }
    }

    fun updateLesson(lesson: Lesson) {
        viewModelScope.launch {
            updateLessonUseCase(lesson)
        }
    }

    fun deleteLesson(lessonId: Long) {
        viewModelScope.launch {
            deleteLessonUseCase(lessonId)
        }
    }
}
