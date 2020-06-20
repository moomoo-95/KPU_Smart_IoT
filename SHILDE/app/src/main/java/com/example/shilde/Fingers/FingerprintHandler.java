package com.example.shilde.Fingers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.shilde.Activities.AddPersonPreviewActivity;
import com.example.shilde.MySafety;
import com.example.shilde.R;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    CancellationSignal cancellationSignal;
    private Context context;



    public FingerprintHandler(Context context) {
        this.context = context;
    }

    //메소드들 정의
    public void startAutho(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("인증 에러 발생" + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("인증 실패", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("인증성공", true);
    }

    public void stopFingerAuth() {
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
    }

    private void update(String s, boolean b) {
        final TextView tv_message = (TextView) ((Activity) context).findViewById(R.id.tv_message);
        final ImageView iv_fingerprint = (ImageView) ((Activity) context).findViewById(R.id.iv_fingerprint);
        final LinearLayout linearLayout = (LinearLayout) ((Activity) context).findViewById(R.id.ll_secure);
        final LinearLayout resultLayout = (LinearLayout) ((Activity) context).findViewById(R.id.resultView);

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
            //linearLayout.setVisibility(LinearLayout.VISIBLE);
            resultLayout.setVisibility(LinearLayout.VISIBLE);
            //sound effect
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone((Activity) context, notification);
//            Intent intent = new Intent((Activity) context, MySafety.class);
//            intent.putExtra("Name", "open");
//            ((Activity) context).setIntent(intent);
        }
    }

}
