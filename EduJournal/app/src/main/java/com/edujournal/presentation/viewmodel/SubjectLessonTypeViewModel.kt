package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.usecase.CreateSubjectLessonTypeUseCase
import com.edujournal.domain.usecase.DeleteSubjectLessonTypeUseCase
import com.edujournal.domain.usecase.EntityWriteResult
import com.edujournal.domain.usecase.ObserveSubjectLessonTypesUseCase
import com.edujournal.domain.usecase.UpdateSubjectLessonTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectLessonTypeViewModel @Inject constructor(
    private val observeLessonTypesUseCase: ObserveSubjectLessonTypesUseCase,
    private val createLessonTypeUseCase: CreateSubjectLessonTypeUseCase,
    private val updateLessonTypeUseCase: UpdateSubjectLessonTypeUseCase,
    private val deleteLessonTypeUseCase: DeleteSubjectLessonTypeUseCase
) : ViewModel() {

    private val _uiMessageRes = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    private val _subjectId = MutableStateFlow<Long?>(null)
    val uiMessageRes: SharedFlow<Int> = _uiMessageRes.asSharedFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val lessonTypes: StateFlow<List<SubjectLessonType>> = _subjectId
        .filterNotNull()
        .flatMapLatest { subjectId -> observeLessonTypesUseCase(subjectId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun load(subjectId: Long) {
        _subjectId.value = subjectId
    }

    fun addLessonType(name: String, hours: Double?, onResult: (EntityWriteResult) -> Unit = {}) {
        viewModelScope.launch {
            val subjectId = _subjectId.value ?: return@launch
            val result = createLessonTypeUseCase(subjectId, name, hours)
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

    fun updateLessonType(type: SubjectLessonType, onResult: (EntityWriteResult) -> Unit = {}) {
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


