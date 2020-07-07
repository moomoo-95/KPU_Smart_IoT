package com.example.kpu_iot;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("ACTIVITY_LC","SAFETY");
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
