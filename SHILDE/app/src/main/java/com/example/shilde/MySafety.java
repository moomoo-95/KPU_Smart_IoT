package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shilde.Activities.RecognitionActivity;
import com.example.shilde.Activities.TestActivity;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MySafety extends AppCompatActivity {

    //세션 유지 ID
    private String Session_ID;
    private TextView now_userID;
    private TextView user_id;
    private TextView safe_name;
    private TextView safe_date;

    private TextView safety_status; //개폐여부
    private TextView safety_move;   //자이로
    private TextView safety_vibrate;//충격

    private TextView safety_open;//개폐버튼
    //boolean door_state = false;

    // 블루투스
    private BluetoothSPP bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_safety);

        now_userID = (TextView) findViewById(R.id.Session_id);
        user_id = (TextView) findViewById(R.id.user_id);

        safe_name = (TextView) findViewById(R.id.safe_name);
        safe_date = (TextView) findViewById(R.id.safe_date);


        safety_open = (TextView)findViewById(R.id.safety_open);
        safety_status = (TextView)findViewById(R.id.safety_status);

        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값
        safe_name.setText(intent.getStringExtra("safe_name"));
        safe_date.setText(intent.getStringExtra("safe_date"));


        //door_state = intent.getBooleanExtra("door_state", false);
        now_userID.setText(Session_ID+"님");
        user_id.setText(Session_ID+"님 금고현황");


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
            public void onDataReceived(byte[] data, String message) {
                String[] sensor_data = new String[6];
                servo.setText(message);

                sensor_data = message.split("/");
                if(sensor_data.length == 6) {
                    if(sensor_data[0].equals("1") && safety_status.getText().equals("열림")){
                        safety_open.setText("열기");
                        safety_status.setText("닫힘");
                    }
                    else if(sensor_data[0].equals("0") && safety_status.getText().equals("닫힘")){
                        safety_open.setText("닫기");
                        safety_status.setText("열림");
                    }
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

        ImageView menu_Image = (ImageView) findViewById(R.id.menu_ico);
        menu_Image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });
        safety_open.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(safety_status.getText().equals("열림")){
                    bt.send("1", true);
                }else{
                    bt.disconnect();
                    Intent intent = new Intent(getApplicationContext(), RecognitionActivity.class);
                    intent.putExtra("ID",Session_ID);
                    startActivity(intent);
                }
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
}
