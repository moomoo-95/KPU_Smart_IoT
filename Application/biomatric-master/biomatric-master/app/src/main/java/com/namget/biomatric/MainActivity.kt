package com.namget.biomatric


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        runFingerprint()
    }


    fun runFingerprint() {
        var biometricPromptInfo: androidx.biometric.BiometricPrompt.PromptInfo
        biometricPromptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("finger print biomatric")
            .setDescription("namget tistory blog")
            .setSubtitle("namget")
            .setNegativeButtonText("Cancel")
            .build()
        val authenticationCallback = getAuthenticationCallback()
        val biometricPrompt: androidx.biometric.BiometricPrompt =
            androidx.biometric.BiometricPrompt(this, Executor { }, authenticationCallback)
        biometricPrompt.authenticate(biometricPromptInfo)
    }


    private fun getAuthenticationCallback() =
        object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }
        }
}
