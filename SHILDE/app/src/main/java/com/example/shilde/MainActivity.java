package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.shilde.Activities.TrainingActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        /*String test = "http://112.175.184.75/myadmin/index.php";
        task = new URLConnector(test);

        task.start();

        try{
            task.join();
            System.out.println("waiting... for result");
        }
        catch(InterruptedException e){

        }

        String result = task.getResult();

        System.out.println(result);*/

        // MyPage 페이지 이동
        ImageView Move_Image = (ImageView) findViewById(R.id.safety_image_1);
        ImageView menu_Image = (ImageView) findViewById(R.id.menu_ico);
        Move_Image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MySafety.class);
                startActivity(intent);
            }
        });
        menu_Image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(v.getContext(), TrainingActivity.class));
                //Intent intent = new Intent(getApplicationContext(), MyPage.class);
                //startActivity(intent);
            }
        });
    }
}