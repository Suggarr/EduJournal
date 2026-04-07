package com.edujournal.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.data.local.UserPreferences
import com.edujournal.domain.model.Semester
import com.edujournal.domain.usecase.ObserveSemestersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    observeSemestersUseCase: ObserveSemestersUseCase
) : ViewModel() {

    val userName = mutableStateOf(userPreferences.getUserName())
    val biometricEnabled = mutableStateOf(userPreferences.isBiometricEnabled())
    val selectedSemesterId = mutableStateOf<Long?>(null)

    val semesters: StateFlow<List<Semester>> = observeSemestersUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            semesters.collect { list ->
                if (selectedSemesterId.value == null && list.isNotEmpty()) {
                    selectedSemesterId.value = list.first().id
                } else if (selectedSemesterId.value != null && list.none { it.id == selectedSemesterId.value }) {
                    selectedSemesterId.value = list.firstOrNull()?.id
                }
            }
        }
    }

    fun saveName(name: String) {
        userPreferences.saveUserName(name)
        userName.value = name
    }

    fun updateUserName(name: String) {
        saveName(name)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        userPreferences.setBiometricEnabled(enabled)
        biometricEnabled.value = enabled
    }

    fun selectSemester(semesterId: Long) {
        selectedSemesterId.value = semesterId
    }
}
