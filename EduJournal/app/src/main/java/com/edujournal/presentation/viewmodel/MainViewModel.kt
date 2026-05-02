package com.edujournal.presentation.viewmodel

import android.app.KeyguardManager
import android.os.Build
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.biometric.BiometricManager
import com.edujournal.R
import com.edujournal.data.local.DatabaseTransferManager
import com.edujournal.data.local.UserPreferences
import com.edujournal.domain.model.Semester
import com.edujournal.domain.usecase.semester.ObserveSemestersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import android.content.Context
import javax.inject.Inject

sealed interface SettingsEvent {
    data class Message(val resId: Int) : SettingsEvent
    data class MessageText(val text: String) : SettingsEvent
    data class ShareDatabase(val uri: Uri) : SettingsEvent
    data object RestartRequired : SettingsEvent
}

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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

    fun requestBiometricToggle(enabled: Boolean) {
        if (!enabled) {
            setBiometricEnabled(false)
            return
        }

        if (canAuthenticateBiometricOrCredential()) {
            setBiometricEnabled(true)
            return
        }

        val hasDeviceCredential = hasDeviceCredential()
        val biometricManager = BiometricManager.from(context)
        val biometricNoneEnrolled =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val authenticators =
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
            } else {
                @Suppress("DEPRECATION")
                biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
            }

        viewModelScope.launch {
            _settingsEvents.emit(
                SettingsEvent.Message(
                    if (biometricNoneEnrolled && !hasDeviceCredential) {
                        R.string.settings_biometric_enroll
                    } else {
                        R.string.settings_biometric_unavailable
                    }
                )
            )
        }
        setBiometricEnabled(false)
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
                    SettingsEvent.MessageText(
                        context.getString(
                            R.string.settings_db_export_error_detailed,
                            contextualError(details)
                        )
                    )
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
                    SettingsEvent.MessageText(
                        context.getString(
                            R.string.settings_db_import_error_detailed,
                            contextualError(details)
                        )
                    )
                )
            }
        }
    }

    fun shareDatabase() {
        viewModelScope.launch {
            val snapshotFile: File? = databaseTransferManager.createShareSnapshot()
            if (snapshotFile == null) {
                _settingsEvents.emit(SettingsEvent.Message(R.string.settings_db_export_error))
                return@launch
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                snapshotFile
            )
            _settingsEvents.emit(SettingsEvent.ShareDatabase(uri))
        }
    }

    private fun contextualError(raw: String): String {
        return when {
            raw.contains("INVALID_DATABASE", ignoreCase = true) ->
                context.getString(R.string.settings_db_error_invalid_structure)
            raw.contains("INVALID_DATABASE_HEADER", ignoreCase = true) ->
                context.getString(R.string.settings_db_error_invalid_header)
            raw.contains("MISSING_TABLES:", ignoreCase = true) ->
                context.getString(R.string.settings_db_error_missing_tables)
            raw.contains("INPUT_STREAM_NOT_FOUND", ignoreCase = true) ->
                context.getString(R.string.settings_db_error_input_stream)
            raw.contains("OUTPUT_STREAM_NOT_FOUND", ignoreCase = true) ->
                context.getString(R.string.settings_db_error_output_stream)
            else -> raw
        }
    }

    private fun canAuthenticateBiometricOrCredential(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val authenticators =
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
        } else {
            @Suppress("DEPRECATION")
            val biometricResult = biometricManager.canAuthenticate()
            biometricResult == BiometricManager.BIOMETRIC_SUCCESS || hasDeviceCredential()
        }
    }

    private fun hasDeviceCredential(): Boolean {
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)
        return keyguardManager?.isDeviceSecure == true
    }
}
