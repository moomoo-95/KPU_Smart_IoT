package com.example.shilde;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shilde.Activities.AddPersonPreviewActivity;
import com.example.shilde.facerecognitionlibrary.Helpers.FileHelper;

import java.io.File;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("ACTIVITY_LC","SAFETY");
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // 사용자에게 짧은 메세지 전달
        Toast.makeText(getApplicationContext(),"환영합니다.",Toast.LENGTH_SHORT).show();

        // 회원가입 페이지 이동
        Button join_button = (Button) findViewById(R.id.join);
        join_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

        // 아이디 찾기 페이지 이동
        Button find_id_button = (Button) findViewById(R.id.find_id);
        find_id_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), FindIdActivity.class);
                startActivity(intent);
            }
        });

        // 비밀번호 찾기 페이지 이동
        Button find_pw_button = (Button) findViewById(R.id.find_pw);
        find_pw_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), FindPasswordActivity.class);
                startActivity(intent);
            }
        });

        // TODO 로그인 기능
        // 로그인 Action
        Button login_button = (Button) findViewById(R.id.login);
        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name = "rla99tjr";
                if(isNameAlreadyUsed(new FileHelper().getTrainingList(), name)){
                    Toast.makeText(getApplicationContext(), name+" 님 환영합니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), AddPersonPreviewActivity.class);
                    intent.putExtra("Folder", "Training");
                    intent.putExtra("Name", name);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

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
    @Override  //@
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
