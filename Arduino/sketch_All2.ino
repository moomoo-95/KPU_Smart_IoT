#include <Wire.h>
#include <LiquidCrystal_I2C.h> // LCD
#include <Servo.h> // Moter

LiquidCrystal_I2C lcd(0x27, 20, 4);

// Gyro
const int MPU_addr = 0x68; // I2C address of the MPU-6050
int16_t AcX, AcY, AcZ, Tmp, GyX, GyY, GyZ;

// Moter
Servo myservo;

// PIR
int PIR_pin = 10;

// Vibration
int ledPin = 13; //안씀
int Vib_pin = 4;



void setup()
{
  Serial.begin(9600);

  // LCD
  lcd.init();
  lcd.backlight();
  // Gyro
  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);  // PWR_MGMT_1 register
  Wire.write(0);     // set to zero (wakes up the MPU-6050)
  Wire.endTransmission(true);

  // Moter
  myservo.attach(9);

  // PIR
  pinMode(PIR_pin, INPUT);

  // Vibration
  pinMode(ledPin, OUTPUT);
  pinMode(Vib_pin, INPUT); //센서핀 입력


}


void loop()
{



  if (Serial.available() > 0) {
    // read incoming serial data:
    char inChar = Serial.read();
    if (inChar == '1')
    {



      myservo.write(0);  // myservo를 0도가 되도록 움직입니다.

      lcd.setCursor(0, 0);
      lcd.print("Perfect Safe");
      lcd.setCursor(0, 1);
      lcd.print("Nice Working");
      lcd.setCursor(5, 3);
      lcd.print("<<OPEN>>");

      Motor_OPEN();
      PIR();
      Gyro();
      Vibration();
      Motor_CLOSE();


      Serial.println(" ");
      Serial.println(" ");






    }
    else if (inChar == '2')
    {
      myservo.write(90); // myservo를 180도가 되도록 움직입니다.



      lcd.setCursor(0, 0);
      lcd.print("Perfect Safe");
      lcd.setCursor(0, 1);
      lcd.print("Nice Working");
      lcd.setCursor(5, 3);
      lcd.print("<<CLOSE>>");

      Motor_CLOSE();

      Serial.println(" ");
      Serial.println(" ");




    }
  }


}



void Gyro() {
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

  delay(1000);
}



void Motor_OPEN() {

  Serial.println("<<서보 모터 값>>");
  myservo.write(0);  // myservo를 0도가 되도록 움직입니다.
  delay(1000);        // 1초동안 기다립니다.
  Serial.println("서보모터: 0도(금고 열림)");
  Serial.println("--------------------------------------------------------------");


}

void Motor_CLOSE() {

  Serial.println("<<서보 모터 값>>");
  myservo.write(90); // myservo를 90도가 되도록 움직입니다.
  delay(1000);        // 1초동안 기다립니다.
  Serial.println("서보모터: 90도(금고 닫힘)");
  Serial.println("--------------------------------------------------------------");


}


void PIR() {

  Serial.println("<<PIR 값>>");

  // 적외선 인체감지 센서에서 값을 읽는다
  // 막으면 1 , 없으면 0
  int sensor = digitalRead(PIR_pin);

  if (sensor = 1 ) // 막혔을 시
  {
    Serial.print(sensor);
    Serial.println(" = Block");
  }
  else {
    Serial.print(sensor);
    Serial.println(" = Open");
  }

  Serial.println("--------------------------------------------------------------");
  delay(1000);
}


void Vibration() {
  Serial.println("<<진동 값>>");
  long measurement = TP_init();

  Serial.print("measurment(Vibration) = ");
  Serial.println(measurement);

  /*
    if (measurement > 1000) {
    digitalWrite(ledPin, HIGH);
    }
    else {
    digitalWrite(ledPin, LOW);
    }
  */

  Serial.println("--------------------------------------------------------------");
  delay(1000);
}

long TP_init() {
  delay(10);
  long measurement = pulseIn (Vib_pin, HIGH);
  return measurement;
}
