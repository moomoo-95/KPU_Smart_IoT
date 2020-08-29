package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.shilde.Activities.RecognitionActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class multi_modal extends AppCompatActivity {
    TextView btn_text;
    TextView text;

    private String Session_ID, safe_n, safe_d;

    CircleProgressBar circleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_modal);

        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값
        safe_n = intent.getStringExtra("safe_name");
        safe_d = intent.getStringExtra("safe_date");

        text = (TextView)findViewById(R.id.progressText);

        circleProgressBar=findViewById(R.id.cpb_circlebar);
        try {
            // 금고열기명령
            String result = new multi_modal.openCustomTask().execute(Session_ID, safe_n, "openmulty").get();

            if (result.compareTo("fail") == 0) {
                Toast.makeText(multi_modal.this, "서버오류입니다. 다시 한 번 시도해주세요.", Toast.LENGTH_SHORT).show();
                Intent fail_intent = new Intent(getApplicationContext(), MySafety.class);
                fail_intent.putExtra("ID", Session_ID);
                fail_intent.putExtra("safe_name", safe_n);
                fail_intent.putExtra("safe_date", safe_d);
                fail_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(fail_intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final TimerTask tt = new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                circleProgressBar.setProgress(i);
                if(i > 20) {
                    try {
                        String result = new multi_modal.openCustomTask().execute(Session_ID, safe_n, "opencheck").get();
                        if (result.compareTo("success") == 0) {
                            circleProgressBar.setProgress(100);
                            cancel();
                            Intent success_intent = new Intent(getApplicationContext(), MySafety.class);
                            success_intent.putExtra("ID", Session_ID);
                            success_intent.putExtra("safe_name", safe_n);
                            success_intent.putExtra("safe_date", safe_d);
                            success_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(success_intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(i < 90){
                    i += 4;
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(tt, 1000, 200);

    }

    class openCustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://101.101.219.143:8080/kpu_mysafety_close.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id="+strings[0]+"&name="+strings[1]+"&type="+strings[2];
                osw.write(sendMsg);
                osw.flush();
//                System.out.println(sendMsg);
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
}
