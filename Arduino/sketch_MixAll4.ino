#include <SoftwareSerial.h> //시리얼 통신 라이브러리 호출
#include <Servo.h> //서보모터 라이브러리
#include <Wire.h> //자이로센서 라이브러리
#include <LiquidCrystal_I2C.h> // LCD 라이브러리

#define DEBUG true
SoftwareSerial esp8266(6, 7); // WiFi 통신을 위한 객체 선언

Servo myservo; //서보모터 객체
int Dig_state = 0;
String WiFi_status01, WiFi_status02; // status01 닫힘, status02 열림

int PIR_Pin = 10; //PIR센서 핀 10으로 설정

int Vibration_Pin = 11; //진동센서 핀 11로 설정

int Motor_Pin = 2; //서보모터 핀 11로 설정

const int MPU_addr = 0x68; // 자이로 센서 I2C 주소
int16_t AcX, AcY, AcZ, Tmp, GyX, GyY, GyZ;

LiquidCrystal_I2C lcd(0x27, 20, 4); // LCD 시작주소


void setup() {
  // put your setup code here, to run once:

  //진동 센서
  pinMode(Vibration_Pin, INPUT); //진동센서 핀 입력

  //서보모터
  myservo.attach(Motor_Pin);   //서보모터 시그널 핀 번호 설정

  //자이로 센서
  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);  // PWR_MGMT_1 register
  Wire.write(0);     // set to zero (wakes up the MPU-6050)
  Wire.endTransmission(true);

  //LCD
  lcd.init();
  lcd.backlight();
  lcd.setCursor(3, 0);
  lcd.print("<Perfect Safe>");

  Serial.begin(9600);
  esp8266.begin(9600); //WiFi 시리얼 개방

  //WiFi
  sendData("AT+CWMODE=3\r\n", 1000, DEBUG); // configure as access point (working mode: AP+STA)
  //sendData("AT+CWLAP\r\n", 3000, DEBUG); // list available access points
  sendData("AT+CWJAP=\"AndroidHotspot5044\",\"0325274715\"\r\n", 6000, DEBUG); // join the access point
  delay(1500);
  
  lcd.backlight();
  lcd.setCursor(0, 1);
  lcd.print("   WiFi Connected");
  sendData("AT+CIFSR\r\n", 1000, DEBUG); // get ip address
  sendData("AT+CIPMUX=1\r\n", 1000, DEBUG); // configure for multiple connections
  sendData("AT+CIPSERVER=1\r\n", 1000, DEBUG); // turn on server on port 80
  sendData("AT+CIPSTART=0,\"TCP\",\"101.101.219.143\",8080\r\n", 1000, DEBUG); //Connect to Server

}

void loop()
{
  // put your main code here, to run repeatedly:

  int Dig_PIR = digitalRead(PIR_Pin); //PIR 센서 = 막히면 1 / 없으면 0
  int Dig_Vib = pulseIn (Vibration_Pin, HIGH); //진동 센서

  //자이로 센서
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 14, true); // request a total of 14 registers
  GyX = Wire.read() << 8 | Wire.read(); // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
  GyY = Wire.read() << 8 | Wire.read(); // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  GyZ = Wire.read() << 8 | Wire.read(); // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)


  if (Dig_PIR == 0 && Dig_Vib == 0 )
  {
    lcd.noBacklight();
    lcd.setCursor(0, 2);
    lcd.print("      Safe Now       ");
  }
  else if ((Dig_PIR == 1) || ( Dig_Vib != NULL) && (GyX != NULL || GyY != NULL || GyZ != NULL))
  {
    lcd.backlight();
    lcd.setCursor(0, 2);
    lcd.print("***Alert Movement***");
    delay(150);
    lcd.noBacklight();
    delay(150);
    lcd.backlight();
    delay(150);
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
  //  dataCommand += "&user=";
  //  dataCommand += 1;
  //  dataCommand += "&equ=";
  //  dataCommand += 1;
  //  dataCommand += "&state=";
  //  dataCommand += 1;
  dataCommand += " HTTP/1.1\r\nHost: 101.101.219.143\r\n\r\n";


  //WiFi_길이 파악을 위해 얘를 나중에 생성
  String sendCommand = "AT+CIPSEND=0,";
  sendCommand += dataCommand.length();
  sendCommand += "\r\n";
  sendData(sendCommand, 1000, DEBUG);
  //sendData(dataCommand, 1000, DEBUG);


  String WiFi_response = sendData(dataCommand, 1000, DEBUG); // WiFi 데이터 수신

  int index = WiFi_response.indexOf("GMT"); //indexOf(찾을 문자)

  int index_status01 = WiFi_response.indexOf("OS01", index); //indexOf(찾을 문자, 검색 시작 위치)
  int index_status02 = WiFi_response.indexOf("OS02", index);
  WiFi_status01 = WiFi_response.substring(index_status01, index_status01 + 4); // substring(추출 시작 위치, 추출 끝 위치)
  WiFi_status02 = WiFi_response.substring(index_status02, index_status02 + 4);


  servo_WiFi();


  if (WiFi_status01 == "OS01") // 서보모터 닫힘
  {
    servo_CLOSE();
  }
  else if (WiFi_status02 == "OS02") //서보모터 열림
  {
    servo_OPEN();
  }


  delay(2000);
  
  String str_close = sendData("AT+CIPCLOSE=0\r\n", 1000, DEBUG);
  int index_disconnect = str_close.indexOf("CLOSED"); //indexOf(찾을 문자)


  //WiFi_서버 연결 끊길시 재연결
  if ( index_disconnect == 18 )
  {
    sendData("AT+CIPSTART=0,\"TCP\",\"101.101.219.143\",8080\r\n", 1000, DEBUG); //Connect to Server
  }

}

void servo_OPEN()
{

  Dig_state = 1;

  myservo.attach(Motor_Pin);  // attaches the servo on pin 9 to the servo object
  myservo.write(10);
  delay(500);
  myservo.detach();

  lcd.backlight();
  lcd.setCursor(5, 3);
  lcd.print("Safe_OPEN  ");
}

void servo_CLOSE()
{

  Dig_state = 0;

  myservo.attach(Motor_Pin);  // attaches the servo on pin 9 to the servo object
  myservo.write(100);
  delay(500);
  myservo.detach();

  lcd.noBacklight();
  lcd.setCursor(5, 3);
  lcd.print("Safe_ CLOSE");
}


void servo_WiFi() //서보모터와 WiFi_status 비교
{

  if (Dig_state = 0 && WiFi_status02 == "OS02") // 서보모터는 닫힘 && WiFi_status02는 열림 상태
  {

    Dig_state = 1;

    lcd.backlight();
    lcd.setCursor(5, 3);
    lcd.print("Safe_OPEN  ");
  }
  else if (Dig_state = 1 && WiFi_status01 == "OS01") // // 서보모터는 열림 && WiFi_status01는 닫힘 상태
  {
    Dig_state = 0;

    lcd.noBacklight();
    lcd.setCursor(5, 3);
    lcd.print("Safe_ CLOSE");

  }
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
