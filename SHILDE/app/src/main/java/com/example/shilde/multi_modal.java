package com.example.shilde;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.example.shilde.Activities.RecognitionActivity;

import java.util.Timer;
import java.util.TimerTask;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class multi_modal extends AppCompatActivity {
    TextView btn_text;
    TextView text;

    private String Session_ID;

    CircleProgressBar circleProgressBar;

    // 블루투스
    private BluetoothSPP bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_modal);


        Intent intent = getIntent();
        Session_ID = intent.getStringExtra("ID"); // Intent를 통해서 전달받은 로그인 아이디 값


        text = (TextView)findViewById(R.id.progressText);

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
                sensor_data = message.split("/");
                if(sensor_data.length == 6) {
                    if(sensor_data[0].equals("0")){
                        bt.disconnect();
                        Intent intent2 = new Intent(getApplicationContext(), MySafety.class);
                        intent2.putExtra("ID",Session_ID);
                        //intent2.putExtra("door_state",true);
                        startActivity(intent2);
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

        circleProgressBar=findViewById(R.id.cpb_circlebar);
        final TimerTask tt = new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                circleProgressBar.setProgress(i);
                if(i >= 100){
                    circleProgressBar.setProgress(100);
                    cancel();

                    bt.send("1", true);
                }
                i += 8;
            }
        };
        Timer timer = new Timer();
        timer.schedule(tt, 1000, 200);


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
