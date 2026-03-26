package com.edujournal.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.edujournal.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val userName = mutableStateOf(userPreferences.getUserName())
    val biometricEnabled = mutableStateOf(userPreferences.isBiometricEnabled())

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
}
