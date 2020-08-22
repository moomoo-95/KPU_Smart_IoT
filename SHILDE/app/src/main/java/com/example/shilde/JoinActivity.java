package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shilde.Activities.RecognitionActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Struct;

public class JoinActivity extends AppCompatActivity {
    EditText userName, userId, userEmail, userPwd, userPwdC, userBirth;
    CheckBox userResp;
    Button join_btn;
    TextView input_resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        userName = (EditText) findViewById(R.id.input_name);
        userId = (EditText) findViewById(R.id.input_id);
        userEmail = (EditText) findViewById(R.id.input_email);
        userPwd = (EditText) findViewById(R.id.input_pw);
        userPwdC = (EditText) findViewById(R.id.input_check_pw);
        userBirth = (EditText) findViewById(R.id.input_birthDay);
        userResp = (CheckBox) findViewById(R.id.check_resp);
        input_resp = (TextView) findViewById(R.id.input_resp);
        join_btn = (Button) findViewById(R.id.join_btn);


        input_resp.setOnClickListener(btnListener);
        join_btn.setOnClickListener(btnListener);

    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://101.101.219.143:8080/kpu_join.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "name="+strings[0]+"&id="+strings[1]+"&pwd="+strings[2]
                        +"&email="+strings[3]+"&birth="+strings[4]+"&type="+strings[5];
                osw.write(sendMsg);
                osw.flush();
                System.out.println(sendMsg);
                System.out.println(conn.getResponseCode());
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
            if(v.getId() == R.id.input_resp){
                startActivity(new Intent(getApplicationContext(), using_terms.class));
            }
            else if(v.getId() == R.id.join_btn) {
                if (userName.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (userId.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (userEmail.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (!userEmail.getText().toString().contains("@")) {
                    Toast.makeText(JoinActivity.this, "이메일이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (userPwd.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (userPwdC.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "비밀번호 확인를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (!userPwd.getText().toString().equals(userPwdC.getText().toString())) {
                    Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (userBirth.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (userBirth.getText().toString().length() != 8) {
                    Toast.makeText(JoinActivity.this, "생년월일이 올바르지 않습니다. Ex)19960704", Toast.LENGTH_SHORT).show();
                }
                else if (!userResp.isChecked()) {
                    Toast.makeText(JoinActivity.this, "이용약관에 동의하셔야 회원가입이 가능합니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    String joinName = userName.getText().toString();
                    String joinId = userId.getText().toString();
                    String joinPw = userPwd.getText().toString();
                    String joinEmail = userEmail.getText().toString();
                    String joinBirth = userBirth.getText().toString();

                    try {
                        String result  = new CustomTask().execute(joinName,joinId,joinPw,joinEmail,joinBirth,"join").get();
                        System.out.println("--------------");
                        System.out.println(result);
                        if(result.equals("id")) {
                            Toast.makeText(JoinActivity.this,"이미 존재하는 아이디입니다.",Toast.LENGTH_SHORT).show();
                            userId.setText("");
                            userPwd.setText("");
                            userPwdC.setText("");
                        } else if(result.equals("ok")) {
                            Toast.makeText(JoinActivity.this,"회원가입을 축하드립니다.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }catch (Exception e) {}
                }
            }
        }
    };
}
