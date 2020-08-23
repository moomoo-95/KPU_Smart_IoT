package com.example.shilde;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shilde.Activities.AddPersonPreviewActivity;
import com.example.shilde.biometriclib.FingerInterface;
import com.example.shilde.facerecognitionlibrary.Helpers.FileHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {

    EditText userId, userPwd;
    Button join_button, login_button, id_button, pwd_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userId = (EditText) findViewById(R.id.input_id); // 아이디
        userPwd = (EditText) findViewById(R.id.input_pw); // 비밀번호

        join_button = (Button) findViewById(R.id.join); // 회원가입
        login_button = (Button) findViewById(R.id.login); // 로그인
        id_button = (Button) findViewById(R.id.find_id); // 아이디 찾기
        pwd_button = (Button) findViewById(R.id.find_pw); // 비밀번호 찾기

        join_button.setOnClickListener(btnListener);
        login_button.setOnClickListener(btnListener);
        id_button.setOnClickListener(btnListener);
        pwd_button.setOnClickListener(btnListener);

        Log.d("ACTIVITY_LC","SAFETY");
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        // 사용자에게 짧은 메세지 전달
//        Toast.makeText(getApplicationContext(),"환영합니다.",Toast.LENGTH_SHORT).show();

    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://101.101.219.143:8080/kpu_login.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id="+strings[0]+"&pwd="+strings[1]+"&type="+strings[2];
                osw.write(sendMsg);
                osw.flush();
//                System.out.println(sendMsg);
//                System.out.println(conn.getResponseCode());
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
//                        System.out.println(str);
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 회원가입 Paging
            if(v.getId() == R.id.join){
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            // 로그인 Paging
            if(v.getId() == R.id.login) {
                if (userId.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (userPwd.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    String joinId = userId.getText().toString();
                    String joinPw = userPwd.getText().toString();
                    try {
                        String result  = new LoginActivity.CustomTask().execute(joinId, joinPw, "login").get();
                        // 아이디, 비밀번호 오류
                        if(result.equals("fail")) {

                            Toast.makeText(LoginActivity.this,"아이디 및 비밀번호를 잘못입력하셨습니다. 다시입력해주세요.",Toast.LENGTH_SHORT).show();
                        } else {
                            if(isNameAlreadyUsed(new FileHelper().getTrainingList(), result)){
                                userId.setText("");
                                userPwd.setText("");
                                Toast.makeText(getApplicationContext(), result+" 님 환영합니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("ID",result);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            else {
                                userId.setText("");
                                userPwd.setText("");
                                Toast.makeText(getApplicationContext(), result+" 님의 얼굴 데이터를 등록합니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), AddPersonPreviewActivity.class);
                                intent.putExtra("Folder", "Training");
                                intent.putExtra("Name", result);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    }catch (Exception e) {}
                }
            }
            // 아이디 찾기 페이지
            if(v.getId() == R.id.find_id){
                Intent intent = new Intent(getApplicationContext(), FindIdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            // 비밀번호 찾기 페이지
            if(v.getId() == R.id.find_pw){
                Intent intent = new Intent(getApplicationContext(), FindPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    private boolean isNameAlreadyUsed(File[] list, String name){
        boolean used = false;
        if(list != null && list.length > 0){
            for(File person : list){
                // The last token is the name --> Folder name = Person name
                String[] tokens = person.getAbsolutePath().split("/");
                final String foldername = tokens[tokens.length-1];
                if(foldername.equals(name)){
                    used = true;
                    break;
                }
            }
        }
        return used;
    }

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    @Override
    protected void onStart() {  //@
        super.onStart();
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            //여기 전처리하는 곳
        }
    }

    //카메라 관련 권한 요청 메소드
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //여기 전처리하는 곳
        }else{
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( LoginActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){ //@
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}
