#include <SoftwareSerial.h> //시리얼 통신 라이브러리 호출
#include "Servo.h" //서보 라이브러리
#include <string.h>
 
Servo myservo; //서보객체
#define BT_RXD 13
#define BT_TXD 12
SoftwareSerial bluetooth(BT_RXD, BT_TXD);;  //시리얼 통신을 위한 객체선언
String myString=""; // 받는 문자열
int state = 1;
int angle = 0; // 각도
 
void setup() {
  myservo.attach(9);   //서보 시그널 핀설정
  myservo.write(0);     //서보 초기각도 0도 설정
  Serial.begin(9600);
  bluetooth.begin(9600); //블루투스 시리얼 개방
}

void loop() {
  while(bluetooth.available()){
    bluetooth.read();
    if(state == 1){
      myservo.write(90);
      state = 0;
    }
    else{
      myservo.write(0);
      state = 1;
    }
   }
}
