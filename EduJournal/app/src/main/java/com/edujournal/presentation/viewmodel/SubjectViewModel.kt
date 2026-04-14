package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.Subject
import com.edujournal.domain.usecase.CreateSubjectUseCase
import com.edujournal.domain.usecase.DeleteSubjectUseCase
import com.edujournal.domain.usecase.EntityWriteResult
import com.edujournal.domain.usecase.ObserveSubjectsUseCase
import com.edujournal.domain.usecase.UpdateSubjectUseCase
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
class SubjectViewModel @Inject constructor(
    private val observeSubjectsUseCase: ObserveSubjectsUseCase,
    private val createSubjectUseCase: CreateSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase
) : ViewModel() {

    private val _uiMessageRes = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val uiMessageRes: SharedFlow<Int> = _uiMessageRes.asSharedFlow()

    val subjects: StateFlow<List<Subject>> = observeSubjectsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSubject(
        name: String,
        abbreviation: String?,
        onResult: (EntityWriteResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = createSubjectUseCase(name, abbreviation)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.subject_duplicate_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.subject_not_found_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun updateSubject(
        subject: Subject,
        onResult: (EntityWriteResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = updateSubjectUseCase(subject)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.subject_duplicate_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.subject_not_found_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun deleteSubject(subjectId: Long) {
        viewModelScope.launch {
            deleteSubjectUseCase(subjectId)
        }
    }
}
