package com.example.fingerprint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    CancellationSignal cancellationSignal;
    private Context context;



    public FingerprintHandler(Context context) {
        this.context = context;
    } // 핸들러 등록

    //메소드들 정의
    public void startAutho(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) { // 인증 에러 발생시 문구 출력
        this.update("인증 에러 발생" + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("인증 실패", false);
    } // 인증 실패

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) { //어플에서의 접근 권한 허용시
        this.update("앱 접근이 허용되었습니다.", true);
    }

    public void stopFingerAuth() {
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
    }

    private void update(String s, boolean b) { // 이벤트 업데이트시 안내메세지 출력을 위한 함수
        final TextView tv_message = (TextView) ((Activity) context).findViewById(R.id.tv_message);
        final ImageView iv_fingerprint = (ImageView) ((Activity) context).findViewById(R.id.iv_fingerprint);
        final LinearLayout linearLayout = (LinearLayout) ((Activity) context).findViewById(R.id.ll_secure);

        //안내 메세지 출력
        tv_message.setText(s);

        if (b == false) {
            tv_message.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            iv_fingerprint.setImageResource(R.drawable.ic_fingerprint_error);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_fingerprint.setImageResource(R.drawable.ic_fingerprint);
                }
            }, 1000);


        } else {//지문인증 성공
            tv_message.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
           iv_fingerprint.setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.SRC_IN);
            linearLayout.setVisibility(LinearLayout.VISIBLE);

            //sound effect
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone((Activity) context, notification);

            r.play();
        }
    }

}
