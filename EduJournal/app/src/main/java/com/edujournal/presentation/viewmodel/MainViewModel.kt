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

    fun saveName(name: String) {
        userPreferences.saveUserName(name)
        userName.value = name
    }
}