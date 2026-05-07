package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.Semester
import com.edujournal.domain.model.enum.SemesterSeason
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.semester.CreateSemesterUseCase
import com.edujournal.domain.usecase.semester.DeleteSemesterUseCase
import com.edujournal.domain.usecase.semester.ObserveSemestersUseCase
import com.edujournal.domain.usecase.semester.UpdateSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SemesterViewModel @Inject constructor(
    observeSemestersUseCase: ObserveSemestersUseCase,
    private val createSemesterUseCase: CreateSemesterUseCase,
    private val updateSemesterUseCase: UpdateSemesterUseCase,
    private val deleteSemesterUseCase: DeleteSemesterUseCase
) : ViewModel() {

    val semesters: StateFlow<List<Semester>> = observeSemestersUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiMessageRes = MutableSharedFlow<Int>()
    val uiMessageRes = _uiMessageRes.asSharedFlow()

    fun addSemester(
        season: SemesterSeason,
        year: Int,
        onResult: (EntityWriteResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = createSemesterUseCase(season, year)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.settings_semester_duplicate_season_year)
                EntityWriteResult.NOT_FOUND -> Unit
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun updateSemester(
        semester: Semester,
        onResult: (EntityWriteResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = updateSemesterUseCase(semester)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.settings_semester_duplicate_season_year)
                EntityWriteResult.NOT_FOUND -> Unit
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun deleteSemester(semesterId: Long) {
        viewModelScope.launch {
            deleteSemesterUseCase(semesterId)
        }
    }
}
