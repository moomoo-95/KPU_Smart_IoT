package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FindPasswordActivity extends AppCompatActivity {

    EditText userId, userEmail;
    Button find_passwd_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        userId = (EditText) findViewById(R.id.find_input_id); // 아이디
        userEmail = (EditText) findViewById(R.id.find_input_email); // 이메일

        find_passwd_btn = (Button) findViewById(R.id.find_pw_btn);    // 비밀번호 찾기

        find_passwd_btn.setOnClickListener(btnListener);
    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://101.101.219.143:8080/kpu_findPasswd.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id="+strings[0]+"&email="+strings[1]+"&type="+strings[2];
                osw.write(sendMsg);
                osw.flush();
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        System.out.println(str);
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
            // 비밀번호 찾기
            if(v.getId() == R.id.find_pw_btn) {
                if (userId.getText().toString().equals("")) {
                    Toast.makeText(FindPasswordActivity.this, "아이디을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (userEmail.getText().toString().equals("")) {
                    Toast.makeText(FindPasswordActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    String findId = userId.getText().toString();
                    String findEmail = userEmail.getText().toString();
                    try {
                        String result  = new FindPasswordActivity.CustomTask().execute(findId, findEmail, "findPasswd").get();
                        System.out.println(result);
                        // 아이디, 이메일 오류
                        if(result.equals("fail")) {

                            Toast.makeText(FindPasswordActivity.this,"일치하는 정보가 없습니다.",Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(FindPasswordActivity.this,"비밀번호는 "+result+" 입니다.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(v.getContext(), LoginActivity.class));
                        }
                    }catch (Exception e) {}
                }
            }
        }
    };
}
