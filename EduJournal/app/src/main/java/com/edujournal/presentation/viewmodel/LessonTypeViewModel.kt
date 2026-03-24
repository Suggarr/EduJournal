package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.LessonType
import com.edujournal.domain.usecase.ObserveLessonTypesUseCase
import com.edujournal.domain.usecase.CreateLessonTypeUseCase
import com.edujournal.domain.repository.LessonTypeRepository
import com.edujournal.domain.usecase.DeleteLessonTypeUseCase
import com.edujournal.domain.usecase.UpdateLessonTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonTypeViewModel @Inject constructor(
    private val observeLessonTypesUseCase: ObserveLessonTypesUseCase,
    private val createLessonTypeUseCase: CreateLessonTypeUseCase,
    private val updateLessonTypeUseCase: UpdateLessonTypeUseCase, // Новый
    private val deleteLessonTypeUseCase: DeleteLessonTypeUseCase  // Новый
) : ViewModel() {

    val lessonTypes: StateFlow<List<LessonType>> = observeLessonTypesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addLessonType(name: String) {
        viewModelScope.launch {
            createLessonTypeUseCase(name)
        }
    }

    fun deleteLessonType(typeId: Long) {
        viewModelScope.launch {
            deleteLessonTypeUseCase(typeId)
        }
    }

    fun updateLessonType(type: LessonType) {
        viewModelScope.launch {
            updateLessonTypeUseCase(type)
        }
    }
}