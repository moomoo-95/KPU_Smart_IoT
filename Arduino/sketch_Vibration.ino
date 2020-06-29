//--- 진동센서 SW-420 Test ---//
 
int ledPin =13;
int vib =4;
 
void setup(){
  pinMode(ledPin, OUTPUT);
  pinMode(vib, INPUT); //센서핀 입력
  Serial.begin(9600); //시리얼통신 설정 9600
  Serial.println("----------------------vibration demo------------------------");
}
void loop(){
  long measurement =TP_init();
  delay(50);
  Serial.print("measurment = ");
  Serial.println(measurement);
  if (measurement > 1000){
    digitalWrite(ledPin, HIGH);
  }
  else{
    digitalWrite(ledPin, LOW); 
  }
}
 
long TP_init(){
  delay(10);
  long measurement=pulseIn (vib, HIGH);
  return measurement;
}
