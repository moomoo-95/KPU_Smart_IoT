package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class modifiy_info extends AppCompatActivity {

    private String Session_ID, safe_name, safe_date, usr_id, usr_name, usr_passwd, usr_email, usr_birth, usr_reg_dt;
    //마이페이지로 이동
    private ImageView menu_ico;

    //세션데이터, 수정불가 데이터 아이디 이름 생년월일 등록시간
    private TextView session_id, user_id, modi_id, modi_name, modi_birth, modi_REG_DT;
    //수정 가능 암호 암호확인 이메일
    private EditText modi_pwd, modi_pwdC, modi_email;
    //버튼 수정 버튼과 취소 버튼
    private Button mody_btn, mody_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifiy_info);

        session_id = (TextView) findViewById(R.id.session_id);
        user_id = (TextView) findViewById(R.id.user_id);

        // 쿼리문으로 받아올 데이터들 아이디 이름 생년월일 등록시간
        modi_id = (TextView) findViewById(R.id.modi_id);
        modi_name = (TextView) findViewById(R.id.modi_name);
        modi_birth = (TextView) findViewById(R.id.modi_birth);
        modi_REG_DT = (TextView) findViewById(R.id.modi_REG_DT);
        //수정가능한 애들 암호 암호확인 이메일
        modi_pwd = (EditText) findViewById(R.id.modi_pwd);
        modi_pwdC = (EditText) findViewById(R.id.modi_pwdC);
        modi_email = (EditText) findViewById(R.id.modi_email);
        //이동버튼 마이페이지 수정후 마이페이지 마이페이지
        menu_ico = (ImageView) findViewById(R.id.menu_ico);
        mody_btn = (Button) findViewById(R.id.mody_btn);
        mody_cancel = (Button) findViewById(R.id.mody_cancel);

        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값
        safe_name =  intent.getStringExtra("safe_name");
        safe_date =  intent.getStringExtra("safe_date");

        session_id.setText(Session_ID+"님");
        user_id.setText(Session_ID+"님 개인정보");


        menu_ico.setOnClickListener(btnListener);
        mody_btn.setOnClickListener(btnListener);
        mody_cancel.setOnClickListener(btnListener);

        try {
            // 메인페이지 호출
            String result = new CustomTask().execute(Session_ID, "modUsr").get();
            System.out.println(result);
            String[] result_arr = result.split("&");
            usr_id = result_arr[0];
            usr_name = result_arr[1];
            usr_email = result_arr[2];
            usr_birth = result_arr[3];
            usr_reg_dt = result_arr[4];
            System.out.println(modi_name.getText());
            System.out.println(modi_email.getText());
        } catch (Exception e) {

            e.printStackTrace();

        }
        modi_id.setText(usr_id);
        modi_name.setText(usr_name);
        modi_email.setText(usr_email);
        modi_birth.setText(usr_birth);
        modi_REG_DT.setText(usr_reg_dt);

    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://101.101.219.143:8080/kpu_mod_usr.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id="+strings[0]+"&type="+strings[1];
                osw.write(sendMsg);
                osw.flush();
                System.out.println(sendMsg);
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
            if(v.getId() == R.id.menu_ico){
//                bt.disconnect();
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("ID",Session_ID);
                intent.putExtra("safe_name",safe_name);
                intent.putExtra("safe_date",safe_date);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            // 수정했으을때 적용해야됨
            else if(v.getId()== R.id.mody_btn){
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("ID",Session_ID);
                intent.putExtra("safe_name",safe_name);
                intent.putExtra("safe_date",safe_date);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(v.getId()== R.id.mody_cancel){
                finish();
            }
        }
    };

}
