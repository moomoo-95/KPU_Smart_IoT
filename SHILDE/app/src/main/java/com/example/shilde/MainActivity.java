package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shilde.Activities.AddPersonPreviewActivity;
import com.example.shilde.facerecognitionlibrary.Helpers.FileHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity {

    //세션 유지 ID
    private String Session_ID;
    private TextView now_userID;

    //인식테스트를 위한 임시
    private LinearLayout imsi;

    TextView safe_name, safe_date;
    // 블루투스
    private BluetoothSPP bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        now_userID = (TextView) findViewById(R.id.Session_id);

        safe_name = (TextView) findViewById(R.id.safe_name);
        safe_date = (TextView) findViewById(R.id.safe_date);

        imsi = (LinearLayout) findViewById(R.id.safety);


        imsi.setOnClickListener(btnListener);

        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값
        now_userID.setText(Session_ID+"님");
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        try{
            // 메인페이지 호출
            String result  = new MainActivity.CustomTask().execute(Session_ID, "getMain").get();
            System.out.println(result);
            String[] result_arr = result.split("&");
            safe_name.setText(result_arr[0]);
            safe_date.setText("등록일 : "+result_arr[1]);
            System.out.println(safe_name.getText());
            System.out.println(safe_date.getText());
        } catch(Exception e){

            e.printStackTrace();

        }
        //블루투스 시작
        bt = new BluetoothSPP(this); //Initializing
        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        // 아두이노에서 넘어오는 데이터를 수신하는 부분 1바이트씩 준다. 이걸 모아서 메세지로 리턴
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            TextView servo = (TextView) findViewById(R.id.servo);
            TextView vib = (TextView) findViewById(R.id.vib);
            TextView gyro_x = (TextView) findViewById(R.id.gyro_x);
            TextView gyro_y = (TextView) findViewById(R.id.gyro_y);
            TextView gyro_z = (TextView) findViewById(R.id.gyro_z);
            TextView PIR = (TextView) findViewById(R.id.PIR);
            public void onDataReceived(byte[] data, String message) {
                String[] sensor_data = new String[6];
                sensor_data = message.split("/");
                if(sensor_data.length == 6) {
                    servo.setText("servo : " + sensor_data[0]);
                    vib.setText("vib : " + sensor_data[1]);
                    gyro_x.setText("gyro_x : " + sensor_data[2]);
                    gyro_y.setText("gyro_y : " + sensor_data[3]);
                    gyro_z.setText("gyro_z : " + sensor_data[4]);
                    PIR.setText("PIR : " + sensor_data[5]);
                }
            }
        });
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                // 아두이노 연결
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
        bt.connect("00:19:10:09:42:FE");
    }
    // 블류슈트 서비스 시작 후 실행되는 것, 전송시 Text가 아두이노에게 전송됨
    public void setup() {
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
            if(v.getId() == R.id.safety){
                bt.disconnect();
                Intent intent = new Intent(getApplicationContext(), MySafety.class);
                intent.putExtra("ID",Session_ID);
                intent.putExtra("safe_name",safe_name.getText());
                intent.putExtra("safe_date",safe_date.getText());
                //intent.putExtra("door_state",false);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };
}