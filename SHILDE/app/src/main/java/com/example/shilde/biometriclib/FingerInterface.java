package com.example.shilde.biometriclib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shilde.MyPage;
import com.example.shilde.R;
import com.example.shilde.multi_modal;

public class FingerInterface extends AppCompatActivity {

//    private TextView mTextView;
    private ImageView mimg;
    private BiometricPromptManager mManager;


    private String Session_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_interface);
//        mTextView = findViewById(R.id.text_view);
        mimg = findViewById(R.id.finger_btn);


        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값

        mManager = BiometricPromptManager.from(this);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SDK version is "+ Build.VERSION.SDK_INT);
        stringBuilder.append("\n");
        stringBuilder.append("isHardwareDetected : "+mManager.isHardwareDetected());
        stringBuilder.append("\n");
        stringBuilder.append("hasEnrolledFingerprints : "+mManager.hasEnrolledFingerprints());
        stringBuilder.append("\n");
        stringBuilder.append("isKeyguardSecure : "+mManager.isKeyguardSecure());
        stringBuilder.append("\n");

//        mTextView.setText(stringBuilder.toString());

        mimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mManager.isBiometricPromptEnable()) {
                    mManager.authenticate(new BiometricPromptManager.OnBiometricIdentifyCallback() {
                        @Override
                        public void onUsePassword() {
                            Toast.makeText(FingerInterface.this, "onUsePassword", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSucceeded() {
                            Toast.makeText(FingerInterface.this, "onSucceeded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), multi_modal.class);
                            intent.putExtra("ID",Session_ID);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailed() {

                            Toast.makeText(FingerInterface.this, "onFailed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int code, String reason) {

                            Toast.makeText(FingerInterface.this, "onError", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {

                            Toast.makeText(FingerInterface.this, "onCancel", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
