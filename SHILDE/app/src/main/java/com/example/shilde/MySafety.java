package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shilde.Activities.RecognitionActivity;
import com.example.shilde.Activities.TestActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MySafety extends AppCompatActivity {

    //세션 유지 ID
    private String Session_ID, safe_n, safe_d;
    private TextView now_userID;
    private TextView user_id;
    private TextView safe_name;
    private TextView safe_date;

    ImageView menu_Image;

    private TextView now_safety_day, now_safety_vibrate, now_safety_move, now_safety_status;
    private TextView safety_check, safety_open;//개폐버튼

    private int close_count = 0;

    //리스트뷰 // gyro_status, pir_status, vib_status, open_status, check_status, new_date
    ListView status_list;
    int status_list_length = 20;
    String[] list_status_gyro; // 자이로
    String[] list_status_pir; // 적외선
    String[] list_status_vib;  // 충격
    String[] list_status_open; // 개폐여부
    String[] list_status_check;  // 사용자확인 여부
    String[] list_status_day;  // 발생시간

    MySafety.CustomList CustomListAdapter;
    final Handler handler = new Handler()  {
        public void handleMessage(Message msg) {
            try {
                // 현재상태 호출
                String result2 = new MySafety.mainCustomTask().execute(Session_ID, safe_n, "nowSafety").get();
//                    Log.e(this.getClass().getName(), "받은 메시지!!! : "+result);
                String[] result_arr2 = result2.split("&"); // gyro_status, pir_status, vib_status, open_status, check_status, new_date
                if(result_arr2[2].compareTo("0")==0){
                    now_safety_vibrate.setText("이상없음");
                    now_safety_vibrate.setTextColor(Color.parseColor("#000000"));
                }
                else{
                    now_safety_vibrate.setText("충격감지");
                    now_safety_vibrate.setTextColor(Color.parseColor("#0000FF"));
                }
                if(result_arr2[0].compareTo("0") == 0){
                    now_safety_move.setText("이상없음");
                    now_safety_move.setTextColor(Color.parseColor("#000000"));
                }
                else{
                    now_safety_move.setText("위치변경");
                    now_safety_move.setTextColor(Color.parseColor("#0000FF"));
                }
                if(result_arr2[3].compareTo("0") == 0){
                    now_safety_status.setText("CLOSE");
                    now_safety_status.setTextColor(Color.parseColor("#000000"));
                    safety_open.setText("금고열기");
                }
                else{
                    now_safety_status.setText("OPEN");
                    now_safety_status.setTextColor(Color.parseColor("#0000FF"));
                    safety_open.setText("금고닫기");
                }
                now_safety_day.setText(result_arr2[5]);
                // 로그 호출
                String result = new MySafety.mainCustomTask().execute(Session_ID, safe_n, "mySafety").get();
//                Log.e(this.getClass().getName(), "받은 메시지!!! : "+result);
                String[] result_arr = result.split("!"); // gyro_status, pir_status, vib_status, open_status, check_status, new_date
                String[][] temp_arr = new String[result_arr[0].split("&").length][result_arr.length];
                for(int i = 0; i < result_arr.length; i++){
                    String[] temp = result_arr[i].split("&");
                    temp_arr[0][i] = temp[0];
                    temp_arr[1][i] = temp[1];
                    temp_arr[2][i] = temp[2];
                    temp_arr[3][i] = temp[3];
                    temp_arr[4][i] = temp[4];
                    temp_arr[5][i] = temp[5];
                }
                list_status_gyro = temp_arr[0];
                list_status_pir = temp_arr[1];
                list_status_vib = temp_arr[2];
                list_status_open = temp_arr[3];
                list_status_check = temp_arr[4];
                list_status_day = temp_arr[5];

            } catch (Exception e) {
                e.printStackTrace();
            }
            CustomListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_safety);

        now_userID = (TextView) findViewById(R.id.Session_id);
        user_id = (TextView) findViewById(R.id.user_id);

        safe_name = (TextView) findViewById(R.id.safe_name);
        safe_date = (TextView) findViewById(R.id.safe_date);


        menu_Image = (ImageView) findViewById(R.id.menu_ico);

        now_safety_day = (TextView)findViewById(R.id.now_safety_day);
        now_safety_vibrate = (TextView)findViewById(R.id.now_safety_vibrate);
        now_safety_move = (TextView)findViewById(R.id.now_safety_move);
        now_safety_status = (TextView)findViewById(R.id.now_safety_status);

        safety_check = (TextView)findViewById(R.id.safety_check);
        safety_open = (TextView)findViewById(R.id.safety_open);

        safety_check.setOnClickListener(btnListener);
        safety_open.setOnClickListener(btnListener);
        menu_Image.setOnClickListener(btnListener);

        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값
        safe_n = intent.getStringExtra("safe_name");
        safe_d = intent.getStringExtra("safe_date");

        now_userID.setText(Session_ID+"님");
        user_id.setText(Session_ID+"님 금고현황");
        safe_name.setText(safe_n);
        safe_date.setText("등록일 : "+safe_d);


        String[] init = new String[status_list_length];
        for (int i = 0; i < init.length; i++){ init[i] = "0"; }
        list_status_gyro = init; // 자이로
        list_status_pir = init; // 적외선
        list_status_vib = init;  // 충격
        list_status_open = init; // 개폐여부
        list_status_check = init;  // 사용자확인 여부
        list_status_day = init;  // 발생시간

        //리스트뷰
        CustomListAdapter = new MySafety.CustomList(MySafety.this);
        status_list=findViewById(R.id.status_list);
        status_list.setAdapter(CustomListAdapter);

        final TimerTask t1 = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        };
//        final TimerTask t2 = new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    // 현재상태 호출
//                    String result = new MySafety.mainCustomTask().execute(Session_ID, safe_n, "nowSafety").get();
////                    Log.e(this.getClass().getName(), "받은 메시지!!! : "+result);
//                    String[] result_arr = result.split("&"); // gyro_status, pir_status, vib_status, open_status, check_status, new_date
//                    if(result_arr[2].compareTo("0")==0){ now_safety_vibrate.setText("이상없음"); }
//                    else{
//                        now_safety_vibrate.setText("충격감지");
//                        now_safety_vibrate.setTextColor(Color.parseColor("#0000FF"));
//                    }
//                    if(result_arr[0].compareTo("0") == 0){ now_safety_move.setText("이상없음"); }
//                    else{
//                        now_safety_move.setText("위치변경");
//                        now_safety_move.setTextColor(Color.parseColor("#0000FF"));
//                    }
//                    if(result_arr[3].compareTo("0") == 0){
//                        now_safety_status.setText("CLOSE");
//                        safety_open.setText("금고열기");
//                    }
//                    else{
//                        now_safety_status.setText("OPEN");
//                        now_safety_status.setTextColor(Color.parseColor("#0000FF"));
//                        safety_open.setText("금고닫기");
//                    }
//                    now_safety_day.setText(result_arr[5]);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
        Timer timer1 = new Timer();
//        Timer timer2 = new Timer();
        timer1.schedule(t1, 0, 500);
//        timer2.schedule(t2, 250, 500);
    }

    class mainCustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://101.101.219.143:8080/kpu_mysafety.jsp");
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
    class closeCustomTask extends AsyncTask<String, Void, String> {
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
    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.safety_check){
                try {
                    // 기록확인
                    String result = new MySafety.closeCustomTask().execute(Session_ID, safe_n, "checkSafety").get();
//                    if(result.compareTo("success") == 0){
//                        Toast.makeText(MySafety.this, "모든 기록은 확인하셨습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                    else if(result.compareTo("fail") == 0){
//                        Toast.makeText(MySafety.this, "서버오류입니다. 다시 한 번 시도해주세요.", Toast.LENGTH_SHORT).show();
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(v.getId() == R.id.safety_open){
                if(safety_open.getText().equals("금고닫기")){
                    try {
                        close_count++;
                        // 금고닫기
                        String result = new MySafety.closeCustomTask().execute(Session_ID, safe_n, "mySafety").get();
                        if(result.compareTo("success") == 0 && close_count > 2){
                            Toast.makeText(MySafety.this, "금고가 정상적으로 닫히지 못했습니다. 금고를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                            close_count = 0;
                        }
                        else if(result.compareTo("fail") == 0){
                            Toast.makeText(MySafety.this, "서버오류입니다. 다시 한 번 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Intent intent = new Intent(getApplicationContext(), RecognitionActivity.class);
                    intent.putExtra("ID",Session_ID);
                    intent.putExtra("safe_name",safe_n);
                    intent.putExtra("safe_date",safe_d);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
            else if(v.getId()== R.id.menu_ico){
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("ID",Session_ID);
                intent.putExtra("safe_name",safe_n);
                intent.putExtra("safe_date",safe_d);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    //리스트뷰
    class CustomList extends ArrayAdapter<String> {
        private final Activity context;
        public CustomList(Activity context){
            super(context, R.layout.listview_safe, list_status_gyro);
            this.context=context;
        }
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.listview_status, null);

            TextView dayView = rowView.findViewById(R.id.list_status_day);
            TextView vibView = rowView.findViewById(R.id.list_status_vib);
            TextView gyroView = rowView.findViewById(R.id.list_status_gyro);
            TextView openView = rowView.findViewById(R.id.list_status_open);


//            //            if(result_arr[1].compareTo("0") == 0){ safety_status.setText("적외선닫힘"); }
//            //            else{ safety_status.setText("적외선닫힘"); }

            dayView.setText(list_status_day[position]);
            if(list_status_vib[position].compareTo("0")==0){ vibView.setText("이상없음"); }
            else{
                vibView.setText("충격감지");
                vibView.setTextColor(Color.parseColor("#0000FF"));
            }
            if(list_status_gyro[position].compareTo("0") == 0){ gyroView.setText("이상없음"); }
            else{
                gyroView.setText("위치변경");
                gyroView.setTextColor(Color.parseColor("#0000FF"));
            }
            if(list_status_open[position].compareTo("0") == 0){
                openView.setText("CLOSE");
            }
            else{
                openView.setText("OPEN");
                openView.setTextColor(Color.parseColor("#0000FF"));
            }
            if(list_status_check[position].compareTo("0")==0){
                dayView.setTextColor(Color.parseColor("#FF0000"));
                vibView.setTextColor(Color.parseColor("#FF0000"));
                gyroView.setTextColor(Color.parseColor("#FF0000"));
                openView.setTextColor(Color.parseColor("#FF0000"));
            }
            return rowView;
        }
    }
}
