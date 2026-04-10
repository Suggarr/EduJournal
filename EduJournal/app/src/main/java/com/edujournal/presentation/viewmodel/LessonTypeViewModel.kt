package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.LessonType
import com.edujournal.domain.usecase.CreateLessonTypeUseCase
import com.edujournal.domain.usecase.DeleteLessonTypeUseCase
import com.edujournal.domain.usecase.EntityWriteResult
import com.edujournal.domain.usecase.ObserveLessonTypesUseCase
import com.edujournal.domain.usecase.UpdateLessonTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonTypeViewModel @Inject constructor(
    private val observeLessonTypesUseCase: ObserveLessonTypesUseCase,
    private val createLessonTypeUseCase: CreateLessonTypeUseCase,
    private val updateLessonTypeUseCase: UpdateLessonTypeUseCase,
    private val deleteLessonTypeUseCase: DeleteLessonTypeUseCase
) : ViewModel() {

    private val _uiMessageRes = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val uiMessageRes: SharedFlow<Int> = _uiMessageRes.asSharedFlow()

    val lessonTypes: StateFlow<List<LessonType>> = observeLessonTypesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addLessonType(name: String, onResult: (EntityWriteResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = createLessonTypeUseCase(name)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.lesson_type_duplicate_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.lesson_type_not_found_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun deleteLessonType(typeId: Long) {
        viewModelScope.launch {
            deleteLessonTypeUseCase(typeId)
        }
    }

    fun updateLessonType(type: LessonType, onResult: (EntityWriteResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = updateLessonTypeUseCase(type)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.lesson_type_duplicate_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.lesson_type_not_found_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }
}
