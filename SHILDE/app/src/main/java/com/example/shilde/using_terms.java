package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class using_terms extends AppCompatActivity {

    Button using_agree;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using_terms);

        using_agree = (Button) findViewById(R.id.using_agree);
        text = (TextView) findViewById(R.id.terms_text);
        text.setMovementMethod(new ScrollingMovementMethod());
        using_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
