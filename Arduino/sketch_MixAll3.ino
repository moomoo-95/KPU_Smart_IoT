#include <SoftwareSerial.h> //시리얼 통신 라이브러리 호출
#include <Servo.h> //서보모터 라이브러리
#include <Wire.h> //자이로센서 라이브러리
#include <LiquidCrystal_I2C.h> // LCD 라이브러리

#define DEBUG true
SoftwareSerial esp8266(6, 7); // WiFi 통신을 위한 객체 선언

Servo myservo; //서보모터 객체
int state = 1;
int Dig_state;

int PIR_Pin = 10; //PIR 핀 10으로 설정

int Vibration_Pin = 11; //진동센서 핀 11로 설정

const int MPU_addr = 0x68; // 자이로 센서 I2C 주소
int16_t AcX, AcY, AcZ, Tmp, GyX, GyY, GyZ;

LiquidCrystal_I2C lcd(0x27, 20, 4); // LCD 시작주소



void setup() {
  // put your setup code here, to run once:

  //서보모터
  myservo.attach(2);   //서보모터 시그널 핀 번호 설정
  myservo.write(0);     //서보모터 초기 각도 0도 설정
  myservo.detach();
  myservo.write(90);
  myservo.detach();


  //진동 센서
  pinMode(Vibration_Pin, INPUT); //진동센서 핀 입력

  //자이로 센서
  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);  // PWR_MGMT_1 register
  Wire.write(0);     // set to zero (wakes up the MPU-6050)
  Wire.endTransmission(true);

  //LCD
  lcd.init();
  lcd.backlight();

  Serial.begin(9600);
  esp8266.begin(9600); //WiFi 시리얼 개방


  //WiFi
  sendData("AT+CWMODE=3\r\n", 1000, DEBUG); // configure as access point (working mode: AP+STA)
  //sendData("AT+CWLAP\r\n", 3000, DEBUG); // list available access points
  sendData("AT+CWJAP=\"AndroidHotspot5044\",\"0325274715\"\r\n", 6000, DEBUG); // join the access point
  delay(3000);
  sendData("AT+CIFSR\r\n", 1000, DEBUG); // get ip address
  sendData("AT+CIPMUX=1\r\n", 1000, DEBUG); // configure for multiple connections
  sendData("AT+CIPSERVER=1\r\n", 1000, DEBUG); // turn on server on port 80
  sendData("AT+CIPSTART=0,\"TCP\",\"101.101.219.143\",8080\r\n", 1000, DEBUG); //Connect to Server



}

void loop() {
  // put your main code here, to run repeatedly:

  lcd.setCursor(3, 0);
  lcd.print("<Perfect Safe>");


  int Dig_PIR = digitalRead(PIR_Pin); //PIR 센서
  int Dig_Vib = pulseIn (Vibration_Pin, HIGH); //진동 센서

  //자이로 센서
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 14, true); // request a total of 14 registers
  GyX = Wire.read() << 8 | Wire.read(); // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
  GyY = Wire.read() << 8 | Wire.read(); // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  GyZ = Wire.read() << 8 | Wire.read(); // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)

  //LCD
  if (Dig_PIR == 0 && Dig_Vib == 0 )
  {
    lcd.noBacklight();
    lcd.setCursor(0, 2);
    lcd.print("     Safe Now       ");
  }
  else if ((Dig_PIR == 1) || ( Dig_Vib != NULL) && (GyX != NULL || GyY != NULL || GyZ != NULL))
  {
    lcd.backlight();
    lcd.setCursor(0, 2);
    lcd.print("***Alert Movement***");
    lcd.noBacklight();
  }




  //WiFi_웹페이지에 보내는 문장
  String dataCommand = "GET /process.jsp?&motor=";
  dataCommand += Dig_state;
  dataCommand += "&oscill=";
  dataCommand += Dig_Vib;
  dataCommand += "&gyro_x=";
  dataCommand += GyX;
  dataCommand += "&gyro_y=";
  dataCommand += GyY;
  dataCommand += "&gyro_z=";
  dataCommand += GyZ;
  dataCommand += "&infra=";
  dataCommand += Dig_PIR;
  dataCommand += "&user=";
  dataCommand += 1;
  dataCommand += "&equ=";
  dataCommand += 1;
  dataCommand += "&state=";
  dataCommand += 1;
  dataCommand += " HTTP/1.1\r\nHost: 101.101.219.143\r\n\r\n";

  Serial.println("");
  Serial.println(dataCommand);

  //WiFi_길이파악을 위해 얘를 나중에 생성
  String sendCommand = "AT+CIPSEND=0,";
  sendCommand += dataCommand.length();
  sendCommand += "\r\n";
  sendData(sendCommand, 1000, DEBUG);
  sendData(dataCommand, 1000, DEBUG);

  delay(5000);

  //WiFi_서버연결
  sendData("AT+CIPSTART=0,\"TCP\",\"101.101.219.143\",8080\r\n", 1000, DEBUG); //Connect to Server




}

String sendData(String command, const int timeout, boolean debug)
{
  String response = "";
  esp8266.print(command); // send the read character to the esp8266
  long int time = millis();

  while ( (time + timeout) > millis()) {
    while (esp8266.available()) {
      // The esp has data so display its output to the serial window
      char c = esp8266.read(); // read the next character.
      response += c;
    }
  }

  if (debug) {
    Serial.print(response);
  }
  return response;
}
