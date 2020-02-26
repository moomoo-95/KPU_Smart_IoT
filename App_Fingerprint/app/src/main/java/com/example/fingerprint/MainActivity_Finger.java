package com.example.fingerprint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity_Finger extends AppCompatActivity {

    private static final String KEY_NAME = "example_key";
    final String TAG = "MainActivity";
    private ImageView iv_fingerprint;
    private TextView tv_message;
    private LinearLayout linearLayout;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private long backKeyPressedTime = 0;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__finger);

        iv_fingerprint = (ImageView) findViewById(R.id.iv_fingerprint);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_message.setText("지문 인증을 해주세요");
        linearLayout = (LinearLayout) findViewById(R.id.ll_secure);
        linearLayout.setVisibility(LinearLayout.GONE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //SDK 버전 확인
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (!fingerprintManager.isHardwareDetected()) {//Manifest에 Fingerprint 퍼미션을 추가해 워야 사용가능
                Toast.makeText(this.getApplicationContext(), "지문을 사용할 수 없는 디바이스 입니다.", Toast.LENGTH_SHORT);


            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // 지문사용 권한이 허용되어 있지 않은 경우
                Toast.makeText(this.getApplicationContext(), "지문사용을 허용해 주세요.", Toast.LENGTH_SHORT);

                /*잠금화면 상태를 체크한다.*/
            } else if (!keyguardManager.isKeyguardSecure()) {
                Toast.makeText(this.getApplicationContext(), "잠금화면을 설정해 주세요.", Toast.LENGTH_SHORT);

            } else if (!fingerprintManager.hasEnrolledFingerprints()) { // 등록되어있는 지문이 없을 경우
                Toast.makeText(this.getApplicationContext(), "등록된 지문이 없습니다.", Toast.LENGTH_SHORT);

            } else {//모든 관문을 성공적으로 통과(지문인식을 지원하고 지문 사용이 허용되어 있고 잠금화면이 설정되었고 지문이 등록되어 있을때)
                Toast.makeText(this.getApplicationContext(), "지문을 스캔해주세요.", Toast.LENGTH_SHORT);

                generateKey();
                if (cipherInit()) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    //지문 핸들러실행
                    FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
                    fingerprintHandler.startAutho(fingerprintManager, cryptoObject);

                }
            }
        }





    }


    //Cipher Init()
    // FingerprintManager.CrytoObject 인스턴스를 만드는 데 사용할 암호를 초기화하는 작업
    // 앞서 만든 키가 이 초기화에 사용
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }




    //Key Generator
    // 안드로이드 Keystore 는 암호용 키 저장소로 키를 안전하게 지켜 주는 역활
    // 저장소에 저장된 키는 암호관련 operation를 통해서만 꺼집어 내서 사용할 수 있게 됨. 승인되지 않은 접근을 제한함
    // KeyGenerator와 함께 안전하게 키를 생성하고 저장하게 되는 것

    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void onBackPressed() {
        // 기존 뒤로가기 버튼의 기능을 막기위해 주석처리 또는 삭제
        // super.onBackPressed();

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }
}
