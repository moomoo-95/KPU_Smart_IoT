package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyPage extends AppCompatActivity {

    private String Session_ID, safe_name, safe_date;
    private ImageView menu_ico;
    private TextView session_id, user_id;
    private Button logout, my_safety, modify_safety, modify_info, settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        session_id = (TextView) findViewById(R.id.session_id);
        user_id = (TextView) findViewById(R.id.user_id);

        menu_ico = (ImageView) findViewById(R.id.menu_ico);

        logout = (Button) findViewById(R.id.logout);
        my_safety = (Button) findViewById(R.id.my_safety);
        modify_safety = (Button) findViewById(R.id.modify_safety);
        modify_info = (Button) findViewById(R.id.modify_info);
        settings = (Button) findViewById(R.id.settings);

        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값
        safe_name =  intent.getStringExtra("safe_name");
        safe_date =  intent.getStringExtra("safe_date");

        session_id.setText(Session_ID+"님");
        user_id.setText(Session_ID+"님");

        menu_ico.setOnClickListener(btnListener);
        logout.setOnClickListener(btnListener);
        my_safety.setOnClickListener(btnListener);
        modify_safety.setOnClickListener(btnListener);
        modify_info.setOnClickListener(btnListener);
        settings.setOnClickListener(btnListener);

    }
    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() ==R.id.menu_ico){
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("ID",Session_ID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(v.getId() == R.id.logout){
//                bt.disconnect();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                //intent.putExtra("door_state",false);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(v.getId()== R.id.my_safety){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("ID",Session_ID);
                //intent.putExtra("door_state",false);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(v.getId()== R.id.modify_safety){
            }
            else if(v.getId()== R.id.modify_info){
                Intent intent = new Intent(getApplicationContext(), modifiy_info.class);
                intent.putExtra("ID",Session_ID);
//                intent.putExtra("safe_name",safe_name);
//                intent.putExtra("safe_date",safe_date);
                //intent.putExtra("door_state",false);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else if(v.getId()== R.id.settings){

            }
        }
    };
}
