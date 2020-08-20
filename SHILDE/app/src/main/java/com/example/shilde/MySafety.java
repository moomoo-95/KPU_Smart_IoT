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

    private TextView safety_status; //개폐여부
    private TextView safety_move;   //자이로
    private TextView safety_vibrate;//충격

    private TextView safety_open;//개폐버튼

    private int x_before = 0, y_before = 0, z_before = 0;
    //boolean door_state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_safety);

        now_userID = (TextView) findViewById(R.id.Session_id);
        user_id = (TextView) findViewById(R.id.user_id);

        safe_name = (TextView) findViewById(R.id.safe_name);
        safe_date = (TextView) findViewById(R.id.safe_date);


        menu_Image = (ImageView) findViewById(R.id.menu_ico);
        safety_open = (TextView)findViewById(R.id.safety_open);
        safety_status = (TextView)findViewById(R.id.safety_status);
        safety_move = (TextView)findViewById(R.id.safety_move);
        safety_vibrate = (TextView)findViewById(R.id.safety_vibrate);

        safety_open.setOnClickListener(btnListener);
        menu_Image.setOnClickListener(btnListener);

        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값
        safe_n = intent.getStringExtra("safe_name");
        safe_d = intent.getStringExtra("safe_date");
        safe_name.setText(safe_n);
        safe_date.setText("등록일 : "+safe_d);


        //door_state = intent.getBooleanExtra("door_state", false);
        now_userID.setText(Session_ID+"님");
        user_id.setText(Session_ID+"님 금고현황");
        

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
                    if(Integer.parseInt(sensor_data[1]) > 10000){
                        safety_vibrate.setText("강한 충격");
                    }
                    else if(Integer.parseInt(sensor_data[1]) > 5000){
                        safety_vibrate.setText("약한 충격");
                    }
                    else {
                        safety_vibrate.setText("이상 없음");

                    }
                    if((Integer.parseInt(sensor_data[2]) - x_before) > 3000 || (Integer.parseInt(sensor_data[2]) - x_before) < -3000){
                        safety_move.setText("움직임 발생");
                    }
                    else if((Integer.parseInt(sensor_data[3]) - y_before) > 3000 || (Integer.parseInt(sensor_data[3]) - y_before) < -3000){
                        safety_move.setText("움직임 발생");
                    }
                    else if((Integer.parseInt(sensor_data[4]) - z_before) > 3000 || (Integer.parseInt(sensor_data[4]) - z_before) < -3000){
                        safety_move.setText("움직임 발생");
                    }
                    else{
                        safety_move.setText("이상 없음");
                    }
                    x_before = Integer.parseInt(sensor_data[2]);
                    y_before = Integer.parseInt(sensor_data[3]);
                    z_before = Integer.parseInt(sensor_data[4]);
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
        if (bt.getServiceState() != BluetoothState.STATE_CONNECTED) {
            bt.connect("00:19:10:09:42:FE");
        }
    }
    // 블류슈트 서비스 시작 후 실행되는 것, 전송시 Text가 아두이노에게 전송됨
    public void setup() {
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.safety_open){
//                bt.disconnect();
                if(safety_status.getText().equals("열림")){
                    bt.send("1", true);
                }else{
                    final TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {
                            bt.disconnect();
                            if(bt.getServiceState() != BluetoothState.STATE_CONNECTED){
                                cancel();
                                Intent intent = new Intent(getApplicationContext(), RecognitionActivity.class);
                                intent.putExtra("ID",Session_ID);
                                intent.putExtra("safe_name",safe_n);
                                intent.putExtra("safe_date",safe_d);
                                startActivity(intent);
                            }
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(tt, 1000, 500);
                }
            }
            else if(v.getId()== R.id.menu_ico){
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("ID",Session_ID);
                intent.putExtra("safe_name",safe_n);
                intent.putExtra("safe_date",safe_d);
                //intent.putExtra("door_state",false);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };
}
