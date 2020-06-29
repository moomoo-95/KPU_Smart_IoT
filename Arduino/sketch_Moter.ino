#include <Servo.h>  // 서보모터 라이브러리를 불러옵니다.
Servo myservo;      // 서보모터에 myservo라고 이름을 붙여줍니다.
void setup() {
  myservo.attach(9);  // myservo를 9번으로 선언하고 작동할 준비를 합니다.
}
void loop() {
  myservo.write(0);  // myservo를 0도가 되도록 움직입니다.
  delay(1000);        // 1초동안 기다립니다.
  myservo.write(90); // myservo를 180도가 되도록 움직입니다.
  delay(1000);        // 1초동안 기다립니다.
}
