package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shilde.Activities.RecognitionActivity;
import com.example.shilde.Activities.TestActivity;
import com.example.shilde.Fingers.MainActivity_Finger;

public class MySafety extends AppCompatActivity {
    TextView btn_text;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_safety);

        btn_text = (TextView)findViewById(R.id.safety_open);
        text = (TextView)findViewById(R.id.safety_status);
        btn_text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(btn_text.getText().equals("열기")){
                    startActivity(new Intent(v.getContext(), RecognitionActivity.class));

                    btn_text.setText("닫기");
                    text.setText("열림");
                }else if(btn_text.getText().equals("닫기")){

                    btn_text.setText("열기");
                    text.setText("닫힘");
                }
            }
        });
    }
}
