package com.example.shilde.biometriclib;

import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shilde.biometriclib.BiometricPromptManager;

public interface IBiometricPromptImpl {

    void authenticate(@NonNull CancellationSignal cancel,
                      @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback);
}
