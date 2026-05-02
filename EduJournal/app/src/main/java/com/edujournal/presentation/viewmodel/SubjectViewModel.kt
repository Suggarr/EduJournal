package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.Subject
import com.edujournal.domain.usecase.subject.CreateSubjectUseCase
import com.edujournal.domain.usecase.subject.DeleteSubjectUseCase
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.subject.ObserveSubjectsUseCase
import com.edujournal.domain.usecase.subject.ObserveSubjectSemesterIdsUseCase
import com.edujournal.domain.usecase.subject.UpdateSubjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val observeSubjectsUseCase: ObserveSubjectsUseCase,
    private val observeSubjectSemesterIdsUseCase: ObserveSubjectSemesterIdsUseCase,
    private val createSubjectUseCase: CreateSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase
) : ViewModel() {

    private val _uiMessageRes = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val uiMessageRes: SharedFlow<Int> = _uiMessageRes.asSharedFlow()

    private val selectedSemesterId = MutableStateFlow<Long?>(null)
    private val _subjects = MutableStateFlow<List<Subject>?>(null)
    val subjects: StateFlow<List<Subject>?> = _subjects.asStateFlow()

    init {
        viewModelScope.launch {
            selectedSemesterId
                .collectLatest { semesterId ->
                    if (semesterId == null) {
                        // Нет выбранного семестра: не показываем бесконечный loading.
                        _subjects.value = emptyList()
                    } else {
                        _subjects.value = null
                        observeSubjectsUseCase(semesterId).collectLatest { list ->
                            _subjects.value = list
                        }
                    }
                }
        }
    }

    fun setSelectedSemester(semesterId: Long?) {
        selectedSemesterId.value = semesterId
    }

    fun loadSubjectSemesterIds(
        subjectId: Long,
        onLoaded: (Set<Long>) -> Unit
    ) {
        viewModelScope.launch {
            val ids = observeSubjectSemesterIdsUseCase(subjectId).first()
            onLoaded(ids.toSet())
        }
    }

    fun addSubject(
        name: String,
        abbreviation: String?,
        semesterIds: List<Long>,
        onResult: (EntityWriteResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = try {
                createSubjectUseCase(name, abbreviation, semesterIds)
            } catch (_: IllegalArgumentException) {
                _uiMessageRes.emit(R.string.subject_semester_required_error)
                onResult(EntityWriteResult.NOT_FOUND)
                return@launch
            }
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
        semesterIds: List<Long>,
        onResult: (EntityWriteResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = try {
                updateSubjectUseCase(subject, semesterIds)
            } catch (_: IllegalArgumentException) {
                _uiMessageRes.emit(R.string.subject_semester_required_error)
                onResult(EntityWriteResult.NOT_FOUND)
                return@launch
            }
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
