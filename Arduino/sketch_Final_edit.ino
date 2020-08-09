#include <SoftwareSerial.h> //시리얼 통신 라이브러리 호출
#include <Servo.h> //서보모터 라이브러리
#include <Wire.h> //자이로센서 라이브러리
#include <LiquidCrystal_I2C.h> // LCD 라이브러리

#define BT_RXD 13
#define BT_TXD 12
SoftwareSerial bluetooth(BT_RXD, BT_TXD);;  //시리얼 통신을 위한 객체 선언
String myString = ""; // 블루투스 통신으로 받는 문자열


Servo myservo; //서보모터 객체
int state = 1;
int Dig_state;


int PIR_Pin = 10; //PIR 핀 10으로 설정

int Vibration_Pin = 11; //진동센서 핀 11로 설정

const int MPU_addr = 0x68; // 자이로 센서 I2C 주소
int16_t AcX, AcY, AcZ, Tmp, GyX, GyY, GyZ;


LiquidCrystal_I2C lcd(0x27, 20, 4); // LCD 시작주소

void setup()
{
  //서보모터
  myservo.attach(2);   //서보모터 시그널 핀 번호 설정
  myservo.write(0);     //서보모터 초기 각도 0도 설정

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
  bluetooth.begin(9600); //블루투스 시리얼 개방
}

void loop()
{
  lcd.setCursor(3, 0);
  lcd.print("<Perfect Safe>");

  PIR();
  Vibration();
  Gyro();
  LCD();
  Bluetooth();
  Serial.println(" ");
  Serial.println(" ");


  while (bluetooth.available())
  {
    bluetooth.read();
    if (state == 1) {
      myservo.write(100); //서보모터 각도 100도로 설정
      state = 0;
     Dig_state = 1;

      lcd.backlight();
      lcd.setCursor(5, 3);
      lcd.print("Safe_OPEN  ");
    }
    else {
      myservo.write(0);
      state = 1;
      Dig_state = 0;


      lcd.backlight();
      lcd.setCursor(5, 3);
      lcd.print("Safe_CLOSE");
    }
  }


}


void PIR() //PIR 함수
{

  Serial.println("<<PIR 값>>");

  // 적외선 인체감지 센서에서 값을 읽는다.
  // 막으면 1 , 없으면 0
  int sensor = digitalRead(PIR_Pin);

  if (sensor == 1)
  {
    Serial.print(sensor);
    Serial.println(" = Movement");

  }
  else {
    Serial.print(sensor);
    Serial.println(" = NO Movement");
  }
  Serial.println("--------------------------------------------------------------");

}


void Vibration() //진동 센서 함수
{
  Serial.println("<<진동 값>>");
  long measurement = pulseIn (Vibration_Pin, HIGH);
  Serial.print("measurment(Vibration) = ");
  Serial.println(measurement);

  Serial.println("--------------------------------------------------------------");
}


void Gyro() //자이로 센서 함수
{
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 14, true); // request a total of 14 registers

  AcX = Wire.read() << 8 | Wire.read(); // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)
  AcY = Wire.read() << 8 | Wire.read(); // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  AcZ = Wire.read() << 8 | Wire.read(); // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)
  Tmp = Wire.read() << 8 | Wire.read(); // 0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)
  GyX = Wire.read() << 8 | Wire.read(); // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
  GyY = Wire.read() << 8 | Wire.read(); // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  GyZ = Wire.read() << 8 | Wire.read(); // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)

  Serial.println("<<자이로센서 값>>");
  Serial.print("AcX = "); Serial.print(AcX);
  Serial.print(" | AcY = "); Serial.print(AcY);
  Serial.print(" | AcZ = "); Serial.print(AcZ);
  Serial.print(" | Tmp = "); Serial.print(Tmp / 340.00 + 36.53); //equation for temperature in degrees C from datasheet
  Serial.print(" | GyX = "); Serial.print(GyX);
  Serial.print(" | GyY = "); Serial.print(GyY);
  Serial.print(" | GyZ = "); Serial.println(GyZ);
  Serial.println("--------------------------------------------------------------");

}

void LCD()
{
  int sensor = digitalRead(PIR_Pin);
  long measurement = pulseIn (Vibration_Pin, HIGH);
  GyX = Wire.read() << 8 | Wire.read(); // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
  GyY = Wire.read() << 8 | Wire.read(); // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  GyZ = Wire.read() << 8 | Wire.read(); // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)

  if (sensor == 0 && measurement == 0 )
  {
    lcd.noBacklight();
    lcd.setCursor(0, 2);
    lcd.print("     Safe Now       ");


  }
  else if ((sensor == 1) || ( measurement != NULL) && (GyX != NULL || GyY != NULL || GyZ != NULL))
  {
    lcd.backlight();
    lcd.setCursor(0, 2);
    lcd.print("***Alert Movement***");
    lcd.noBacklight();

  }

}


void Bluetooth()
{

  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 14, true); // request a total of 14 registers
  
  int Dig_PIR = digitalRead(10);
  int Dig_Vib = pulseIn (Vibration_Pin, HIGH);
  int Dig_GyX = Wire.read() << 8 | Wire.read();
  int Dig_GyY = Wire.read() << 8 | Wire.read();
  int Dig_GyZ = Wire.read() << 8 | Wire.read();

 
  String Dig_ALL = String(Dig_state)+ "/"  + String(Dig_Vib) + "/" + String(Dig_GyX) + "/" + String(Dig_GyY) + "/" + String(Dig_GyZ)+ "/" + String(Dig_PIR);
  Serial.println(Dig_ALL);
  bluetooth.println(Dig_ALL);
 

 /*

  bluetooth.print("Servo: ");
  bluetooth.println(Dig_state);
  bluetooth.print("PIR: ");
  bluetooth.println(Dig_PIR);
  bluetooth.print("Vib: ");
  bluetooth.println(Dig_Vib);
  bluetooth.print("Gyro: ");
  bluetooth.print(Dig_GyX);
  bluetooth.print(", ");
  bluetooth.print(Dig_GyY);
  bluetooth.print(", ");
  bluetooth.println(Dig_GyZ);
  
 */

  


}
