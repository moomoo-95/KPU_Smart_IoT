// 적외선 센서 핀번호 선언
int motion = 2; 
// 13번 고정 LED 핀번호 선언


void setup() {
  // 적외선센서의 핀을 INPUT모드로 선언
    pinMode(motion,INPUT); 
  
  // 시리얼 통신 속도 설정
    Serial.begin(9600);
}

void loop() {

  
  delay(500);
  
  // 적외선 인체감지 센서에서 값을 읽는다
  // 막으면 1 , 없으면 0
  int sensor = digitalRead(motion); 
  // 센서값을 시리얼 모니터에 출력
  Serial.println(sensor); 
  
 
}
