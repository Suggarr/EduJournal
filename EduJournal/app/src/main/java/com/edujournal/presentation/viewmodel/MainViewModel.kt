package com.edujournal.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.data.local.DatabaseTransferManager
import com.edujournal.data.local.UserPreferences
import com.edujournal.domain.model.Semester
import com.edujournal.domain.usecase.ObserveSemestersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsEvent {
    data class Message(val resId: Int) : SettingsEvent
    data class MessageText(val text: String) : SettingsEvent
    data object RestartRequired : SettingsEvent
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val databaseTransferManager: DatabaseTransferManager,
    observeSemestersUseCase: ObserveSemestersUseCase
) : ViewModel() {

    val userName = mutableStateOf(userPreferences.getUserName())
    val biometricEnabled = mutableStateOf(userPreferences.isBiometricEnabled())
    val selectedSemesterId = mutableStateOf<Long?>(null)
    private val _settingsEvents = MutableSharedFlow<SettingsEvent>(extraBufferCapacity = 1)
    val settingsEvents: SharedFlow<SettingsEvent> = _settingsEvents.asSharedFlow()

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

    fun exportDatabase(uri: Uri) {
        viewModelScope.launch {
            val success = databaseTransferManager.exportDatabase(uri)
            if (success) {
                _settingsEvents.emit(SettingsEvent.Message(R.string.settings_db_export_success))
            } else {
                val details = databaseTransferManager.lastError ?: "unknown"
                _settingsEvents.emit(
                    SettingsEvent.MessageText("Экспорт БД: ${contextualError(details)}")
                )
            }
        }
    }

    fun importDatabase(uri: Uri) {
        viewModelScope.launch {
            val success = databaseTransferManager.importDatabase(uri)
            if (success) {
                _settingsEvents.emit(SettingsEvent.Message(R.string.settings_db_import_success))
                _settingsEvents.emit(SettingsEvent.RestartRequired)
            } else {
                val details = databaseTransferManager.lastError ?: "unknown"
                _settingsEvents.emit(
                    SettingsEvent.MessageText("Импорт БД: ${contextualError(details)}")
                )
            }
        }
    }

    private fun contextualError(raw: String): String {
        return when {
            raw.contains("INVALID_DATABASE", ignoreCase = true) ->
                "файл не прошел проверку структуры"
            raw.contains("INVALID_DATABASE_HEADER", ignoreCase = true) ->
                "файл не является корректной SQLite базой"
            raw.contains("MISSING_TABLES:", ignoreCase = true) ->
                "в файле отсутствуют таблицы приложения"
            raw.contains("INPUT_STREAM_NOT_FOUND", ignoreCase = true) ->
                "не удалось открыть выбранный файл"
            raw.contains("OUTPUT_STREAM_NOT_FOUND", ignoreCase = true) ->
                "не удалось открыть файл назначения"
            else -> raw
        }
    }
}
