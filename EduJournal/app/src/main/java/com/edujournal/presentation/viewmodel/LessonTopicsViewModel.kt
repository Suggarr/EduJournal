package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.Lesson
import com.edujournal.domain.model.TopicTemplate
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.lesson.CreateLessonUseCase
import com.edujournal.domain.usecase.lesson.DeleteLessonUseCase
import com.edujournal.domain.usecase.lesson.GetLessonsUseCase
import com.edujournal.domain.usecase.topictemplate.ObserveTopicTemplatesUseCase
import com.edujournal.domain.usecase.subjectlessontype.ObserveSubjectLessonTypeByIdUseCase
import com.edujournal.domain.usecase.lesson.UpdateLessonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LessonTopicsViewModel @Inject constructor(
    private val getLessonsUseCase: GetLessonsUseCase,
    private val observeLessonTypeByIdUseCase: ObserveSubjectLessonTypeByIdUseCase,
    private val observeTopicTemplatesUseCase: ObserveTopicTemplatesUseCase,
    private val createLessonUseCase: CreateLessonUseCase,
    private val updateLessonUseCase: UpdateLessonUseCase,
    private val deleteLessonUseCase: DeleteLessonUseCase
) : ViewModel() {
    private val _uiMessageRes = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val uiMessageRes: SharedFlow<Int> = _uiMessageRes.asSharedFlow()


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

    fun observeTopicTemplates(
        semesterId: Long,
        subjectLessonTypeId: Long
    ): StateFlow<List<TopicTemplate>> {
        return observeTopicTemplatesUseCase(semesterId, subjectLessonTypeId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addLesson(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long,
        date: LocalDate,
        topic: String,
        onResult: (EntityWriteResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = createLessonUseCase(
                Lesson(
                    id = 0,
                    groupId = groupId,
                    subjectLessonTypeId = subjectLessonTypeId,
                    semesterId = semesterId,
                    date = date,
                    topic = topic
                )
            )
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.lesson_topics_duplicate_date_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.lesson_topics_invalid_date_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun updateLesson(
        lesson: Lesson,
        onResult: (EntityWriteResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = updateLessonUseCase(lesson)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.lesson_topics_duplicate_date_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.lesson_topics_invalid_date_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun deleteLesson(lessonId: Long) {
        viewModelScope.launch {
            deleteLessonUseCase(lessonId)
        }
    }
}


