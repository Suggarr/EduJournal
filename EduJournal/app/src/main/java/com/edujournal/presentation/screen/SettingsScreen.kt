package com.edujournal.presentation.screen

import android.app.KeyguardManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.edujournal.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userName: String,
    biometricEnabled: Boolean,
    onSaveUserName: (String) -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onManageSemesters: () -> Unit,
    onExportDatabase: (Uri) -> Unit,
    onImportDatabase: (Uri) -> Unit
) {
    val context = LocalContext.current
    var editedName by remember(userName) { mutableStateOf(userName) }

    val nameSavedText = stringResource(R.string.settings_name_saved)
    val biometricEnrollText = stringResource(R.string.settings_biometric_enroll)
    val biometricUnavailableText = stringResource(R.string.settings_biometric_unavailable)
    val timestampFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm") }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null) onExportDatabase(uri)
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) onImportDatabase(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.settings_user_name_label)) },
                    singleLine = true
                )
            }

            item {
                Button(
                    onClick = {
                        val trimmedName = editedName.trim()
                        if (trimmedName.isNotEmpty()) {
                            onSaveUserName(trimmedName)
                            Toast.makeText(context, nameSavedText, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = editedName.trim().isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_save_name))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(R.string.settings_biometric))
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = { enabled ->
                            if (!enabled) {
                                onBiometricToggle(false)
                                return@Switch
                            }

                            val isAvailable = canAuthenticateBiometricOrCredential(context)
                            if (isAvailable) {
                                onBiometricToggle(true)
                                return@Switch
                            }

                            val hasDeviceCredential = hasDeviceCredential(context)
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

                            Toast.makeText(
                                context,
                                if (biometricNoneEnrolled && !hasDeviceCredential) biometricEnrollText else biometricUnavailableText,
                                Toast.LENGTH_LONG
                            ).show()
                            onBiometricToggle(false)
                        }
                    )
                }
            }

            item {
                Button(
                    onClick = onManageSemesters,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_manage_semesters))
                }
            }

            item {
                Button(
                    onClick = {
                        val timestamp = LocalDateTime.now().format(timestampFormatter)
                        exportLauncher.launch("EduJournal_backup_$timestamp.db")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_export_db))
                }
            }

            item {
                Button(
                    onClick = {
                        importLauncher.launch(arrayOf("application/octet-stream", "application/x-sqlite3", "*/*"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_import_db))
                }
            }
        }
    }
}

private fun canAuthenticateBiometricOrCredential(context: android.content.Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    } else {
        @Suppress("DEPRECATION")
        val biometricResult = biometricManager.canAuthenticate()
        biometricResult == BiometricManager.BIOMETRIC_SUCCESS || hasDeviceCredential(context)
    }
}

private fun hasDeviceCredential(context: android.content.Context): Boolean {
    val keyguardManager = context.getSystemService(KeyguardManager::class.java)
    return keyguardManager?.isDeviceSecure == true
}
