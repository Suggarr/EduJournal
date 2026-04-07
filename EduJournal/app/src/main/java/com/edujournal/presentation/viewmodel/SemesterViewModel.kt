package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Semester
import com.edujournal.domain.model.SemesterSeason
import com.edujournal.domain.usecase.CreateSemesterUseCase
import com.edujournal.domain.usecase.DeleteSemesterUseCase
import com.edujournal.domain.usecase.ObserveSemestersUseCase
import com.edujournal.domain.usecase.UpdateSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    fun addSemester(season: SemesterSeason, year: Int) {
        viewModelScope.launch {
            createSemesterUseCase(season, year)
        }
    }

    fun updateSemester(semester: Semester) {
        viewModelScope.launch {
            updateSemesterUseCase(semester)
        }
    }

    fun deleteSemester(semesterId: Long) {
        viewModelScope.launch {
            deleteSemesterUseCase(semesterId)
        }
    }
}
