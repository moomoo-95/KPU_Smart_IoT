package com.example.kpu_iot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.BackgroundColorSpan;

import org.xml.sax.helpers.LocatorImpl;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{

            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
