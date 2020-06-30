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

public class FindIdActivity extends AppCompatActivity {

    EditText userName, userBirth;
    Button find_id_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        userName = (EditText) findViewById(R.id.find_input_name); // 이름
        userBirth = (EditText) findViewById(R.id.find_input_birthDay); // 생년월일

        find_id_btn = (Button) findViewById(R.id.find_id_btn);    // 아이디 찾기

        find_id_btn.setOnClickListener(btnListener);
    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://101.101.219.143:8080/kpu_findId.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "name="+strings[0]+"&birth="+strings[1]+"&type="+strings[2];
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
            // 아이디 찾기
            if(v.getId() == R.id.find_id_btn) {
                if (userName.getText().toString().equals("")) {
                    Toast.makeText(FindIdActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (userBirth.getText().toString().equals("")) {
                    Toast.makeText(FindIdActivity.this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    String findName = userName.getText().toString();
                    String findBirth = userBirth.getText().toString();
                    try {
                        String result  = new FindIdActivity.CustomTask().execute(findName, findBirth, "findId").get();
                        // 이름, 생년월일 오류
                        if(result.equals("fail")) {

                            Toast.makeText(FindIdActivity.this,"일치하는 아이디가 없습니다.",Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(FindIdActivity.this,"아이디는 "+result+" 입니다.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(v.getContext(), LoginActivity.class));
                        }
                    }catch (Exception e) {}
                }
            }
        }
    };
}
