package com.example.shilde.biometriclib;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import android.text.TextUtils;
import android.util.Log;

@RequiresApi(Build.VERSION_CODES.M)
public class BiometricPromptApi23 implements IBiometricPromptImpl {

    private static final String TAG = "BiometricPromptApi23";
    private Activity mActivity;
    private BiometricPromptDialog mDialog;
    private FingerprintManager mFingerprintManager;
    private CancellationSignal mCancellationSignal;
    private BiometricPromptManager.OnBiometricIdentifyCallback mManagerIdentifyCallback;
    private FingerprintManager.AuthenticationCallback mFmAuthCallback
            = new FingerprintManageCallbackImpl();

    public BiometricPromptApi23(Activity activity) {
        mActivity = activity;

        mFingerprintManager = getFingerprintManager(activity);
    }

    @Override
    public void authenticate(@Nullable CancellationSignal cancel,
                             @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback) {
        mManagerIdentifyCallback = callback;

        mDialog = BiometricPromptDialog.newInstance();
        mDialog.setOnBiometricPromptDialogActionCallback(new BiometricPromptDialog.OnBiometricPromptDialogActionCallback() {
            @Override
            public void onDialogDismiss() {
                if (mCancellationSignal != null && !mCancellationSignal.isCanceled()) {
                    mCancellationSignal.cancel();
                }
            }

            @Override
            public void onUsePassword() {
                if (mManagerIdentifyCallback != null) {
                    mManagerIdentifyCallback.onUsePassword();
                }
            }

            @Override
            public void onCancel() {
                if (mManagerIdentifyCallback != null) {
                    mManagerIdentifyCallback.onCancel();
                }
            }
        });
        mDialog.show(mActivity.getFragmentManager(), "BiometricPromptApi23");

        mCancellationSignal = cancel;
        if (mCancellationSignal == null) {
            mCancellationSignal = new CancellationSignal();
        }
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                mDialog.dismiss();
            }
        });

        try {
            CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
            getFingerprintManager(mActivity).authenticate(
                    cryptoObjectHelper.buildCryptoObject(), mCancellationSignal,
                    0, mFmAuthCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FingerprintManageCallbackImpl extends FingerprintManager.AuthenticationCallback {

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Log.d(TAG, "onAuthenticationError() called with: errorCode = [" + errorCode + "], errString = [" + errString + "]");
            mDialog.setState(BiometricPromptDialog.STATE_ERROR);
            mManagerIdentifyCallback.onError(errorCode, errString.toString());
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Log.d(TAG, "onAuthenticationFailed() called");
            mDialog.setState(BiometricPromptDialog.STATE_FAILED);
            mManagerIdentifyCallback.onFailed();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            Log.d(TAG, "onAuthenticationHelp() called with: helpCode = [" + helpCode + "], helpString = [" + helpString + "]");
            mDialog.setState(BiometricPromptDialog.STATE_FAILED);
            mManagerIdentifyCallback.onFailed();

        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Log.i(TAG, "onAuthenticationSucceeded: ");
            mDialog.setState(BiometricPromptDialog.STATE_SUCCEED);

            mManagerIdentifyCallback.onSucceeded();

        }
    }

    private FingerprintManager getFingerprintManager(Context context) {
        if (mFingerprintManager == null) {
            mFingerprintManager = context.getSystemService(FingerprintManager.class);
        }
        return mFingerprintManager;
    }

    public boolean isHardwareDetected() {
        if (mFingerprintManager != null) {
            return mFingerprintManager.isHardwareDetected();
        }
        return false;
    }

    public boolean hasEnrolledFingerprints() {
        if (mFingerprintManager != null) {
            return mFingerprintManager.hasEnrolledFingerprints();
        }
        return false;
    }
}
