package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Subject
import com.edujournal.domain.usecase.CreateSubjectUseCase
import com.edujournal.domain.usecase.DeleteSubjectUseCase
import com.edujournal.domain.usecase.ObserveSubjectsUseCase
import com.edujournal.domain.usecase.UpdateSubjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val observeSubjectsUseCase: ObserveSubjectsUseCase,
    private val createSubjectUseCase: CreateSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase
) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = observeSubjectsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSubject(name: String, abbreviation: String?) {
        viewModelScope.launch {
            createSubjectUseCase(name, abbreviation)
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            updateSubjectUseCase(subject)
        }
    }

    fun deleteSubject(subjectId: Long) {
        viewModelScope.launch {
            deleteSubjectUseCase(subjectId)
        }
    }
}
