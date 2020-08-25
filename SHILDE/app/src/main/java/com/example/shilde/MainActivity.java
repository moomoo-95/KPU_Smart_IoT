package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shilde.Activities.AddPersonPreviewActivity;
import com.example.shilde.facerecognitionlibrary.Helpers.FileHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //세션 유지 ID
    private String Session_ID;
    private TextView now_userID;

    //인식테스트를 위한 임시
    private ImageView menu_ico;

    //리스트뷰
    ListView safe_list;
    String[] names;
    String[] days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        now_userID = (TextView) findViewById(R.id.Session_id);

        menu_ico = (ImageView) findViewById(R.id.menu_ico);

        menu_ico.setOnClickListener(btnListener);

        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값
        now_userID.setText(Session_ID + "님");
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        try {
            // 메인페이지 호출
            String result = new MainActivity.CustomTask().execute(Session_ID, "getMain").get();
            Log.e(this.getClass().getName(), "받은 메시지 : "+result);
            String[] result_arr = result.split("!");
            result_arr[0] = result_arr[0].substring(1, result_arr[0].length());
            result_arr[1] = result_arr[1].substring(1, result_arr[1].length());
            Log.e(this.getClass().getName(), "받은 메시지 : "+result_arr[0]);
            Log.e(this.getClass().getName(), "받은 메시지 : "+result_arr[1]);
            days = result_arr[0].split("&");
            names = result_arr[1].split("&");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //리스트뷰
        CustomList CustomListAdapter = new CustomList(MainActivity.this);
        safe_list=findViewById(R.id.safe_list);
        safe_list.setAdapter(CustomListAdapter);
        safe_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), MySafety.class);
                    intent.putExtra("ID",Session_ID);
                    intent.putExtra("safe_name",names[position]);
                    intent.putExtra("safe_date",days[position]);
                    //intent.putExtra("door_state",false);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
            }
        });
    }
    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://101.101.219.143:8080/kpu_main.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id="+strings[0]+"&type="+strings[1];
                osw.write(sendMsg);
                osw.flush();
//                System.out.println(sendMsg);
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
//                        System.out.println(str);
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
            if(v.getId()== R.id.menu_ico){
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("ID",Session_ID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };
    //리스트뷰
    class CustomList extends ArrayAdapter<String>{
        private final Activity context;
        public CustomList(Activity context){
            super(context, R.layout.listview_safe, names);
            this.context=context;
        }
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.listview_safe, null);
            ImageView imageView = rowView.findViewById(R.id.safe_image);
            TextView nameView = rowView.findViewById(R.id.safe_name);
            TextView dateView = rowView.findViewById(R.id.safe_date);

            nameView.setText(names[position]);
            dateView.setText("등록일 : "+days[position]);
            return rowView;
        }
    }
}