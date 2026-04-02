package com.edujournal

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.edujournal.data.local.UserPreferences
import com.edujournal.presentation.navigation.AppNavigation
import com.edujournal.ui.theme.EduJournalTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    private var isBiometricAuthenticated = false
    private var isPromptInProgress = false

    companion object {
        private const val STATE_BIOMETRIC_AUTHENTICATED = "state_biometric_authenticated"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isBiometricAuthenticated =
            savedInstanceState?.getBoolean(STATE_BIOMETRIC_AUTHENTICATED, false) ?: false
        isPromptInProgress = false

        val composeView = ComposeView(this).apply {
            setContent {
                EduJournalTheme {
                    AppNavigation()
                }
            }
        }
        setContentView(composeView)
    }

    override fun onStart() {
        super.onStart()
        requestBiometricIfNeeded()
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            isBiometricAuthenticated = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_BIOMETRIC_AUTHENTICATED, isBiometricAuthenticated)
    }

    private fun requestBiometricIfNeeded() {
        if (!userPreferences.isBiometricEnabled()) return
        if (userPreferences.getUserName().isNullOrBlank()) return
        if (isBiometricAuthenticated || isPromptInProgress) return

        if (!canAuthenticateBiometricOrCredential()) {
            userPreferences.setBiometricEnabled(false)
            return
        }

        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isPromptInProgress = false
                    isBiometricAuthenticated = true
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    isPromptInProgress = false
                    isBiometricAuthenticated = false
                    moveTaskToBack(true)
                }
            }
        )

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle(getString(R.string.biometric_prompt_subtitle))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val authenticators =
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            promptInfoBuilder.setAllowedAuthenticators(authenticators)
        } else {
            @Suppress("DEPRECATION")
            promptInfoBuilder.setDeviceCredentialAllowed(true)
        }

        val promptInfo = promptInfoBuilder.build()

        isPromptInProgress = true
        biometricPrompt.authenticate(promptInfo)
    }

    private fun canAuthenticateBiometricOrCredential(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val authenticators =
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
        } else {
            @Suppress("DEPRECATION")
            val biometricResult = biometricManager.canAuthenticate()
            val keyguardManager = getSystemService(KeyguardManager::class.java)
            biometricResult == BiometricManager.BIOMETRIC_SUCCESS || keyguardManager?.isDeviceSecure == true
        }
    }
}
