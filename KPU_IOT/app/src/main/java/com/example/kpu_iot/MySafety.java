package com.example.kpu_iot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MySafety extends AppCompatActivity {
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_safety);

        text = (TextView)findViewById(R.id.safety_open);

        text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(text.getText().equals("열기")){

                    text.setText("닫기");
                }else if(text.getText().equals("닫기")){

                    text.setText("열기");
                }
            }
        });
    }
}
