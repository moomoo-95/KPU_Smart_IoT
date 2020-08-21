#include <SoftwareSerial.h>
#define DEBUG true

SoftwareSerial esp8266(6, 7); // make RX Arduino line is pin 2, make TX Arduino line is pin 3.
// This means that you need to connect the TX line from the esp to the Arduino's pin 2
// and the RX line from the esp to the Arduino's pin 3

int Relaypin = 5;
int temp = 65;
int humi = 56;
void setup() {
  pinMode(Relaypin, OUTPUT);
  Serial.begin(9600);
  esp8266.begin(9600); // your esp's baud rate might be different

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

  int Dig_state = 10;
  int Dig_Vib = 20;
  int Dig_GyX = 30;
  int Dig_GyY = 40;
  int Dig_GyZ = 50;
  int Dig_PIR = 60;


  String Dig_ALL = String(Dig_state) + "/"  + String(Dig_Vib) + "/" + String(Dig_GyX) + "/" + String(Dig_GyY) + "/" + String(Dig_GyZ) + "/" + String(Dig_PIR);
  Serial.println(Dig_ALL);

  //웹페이지에 보내는 문장
  String dataCommand = "GET /process.jsp?&motor=";
  dataCommand += Dig_state;
  dataCommand += "&oscill=";
  dataCommand += Dig_Vib;
  dataCommand += "&gyro_x=";
  dataCommand += Dig_GyX;
  dataCommand += "&gyro_y=";
  dataCommand += Dig_GyY;
  dataCommand += "&gyro_z=";
  dataCommand += Dig_GyZ;
  dataCommand += "&infra=";
  dataCommand += Dig_PIR;
  dataCommand += "&user=";
  dataCommand += 1;
  dataCommand += "&equ=";
  dataCommand += 1;
  dataCommand += "&state=";
  dataCommand += 1;
  dataCommand += " HTTP/1.1\r\nHost: 101.101.219.143\r\n\r\n";

  //길이파악을 위해 얘를 나중에 생성
  String sendCommand = "AT+CIPSEND=0,";
  sendCommand += dataCommand.length();
  sendCommand += "\r\n";
  sendData(sendCommand, 1000, DEBUG);
  sendData(dataCommand, 1000, DEBUG);

  //  if (esp8266.available()) {
  //    Serial.println("Available()");
  //    if (esp8266.find("+IPD,")) {
  //      delay(1000);
  //      Serial.println("==================");
  //      Serial.println(esp8266.read());
  //      Serial.println("==================");
  //    }
  //  }
  //
  //  Serial.print(".\n");
  //  if (esp8266.available()) { // check if the esp is sending a message
  //    Serial.println("Available()");
  //
  //    if (esp8266.find("+IPD,")) {
  //      delay(1000); // wait for the serial buffer to fill up (read all the serial data)
  //      // get the connection id so that we can then disconnect
  //      int connectionId = esp8266.read() - 48; // subtract 48 because the read() function returns
  //      // the ASCII decimal value and 0 (the first decimal number) starts at 48
  //      esp8266.find("pin="); // advance cursor to "pin="
  //      int pinNumber = (esp8266.read() - 48) * 10; // get first number i.e. if the pin 13 then the 1st number is 1, then multiply to get 10
  //      pinNumber += (esp8266.read() - 48); // get second number, i.e. if the pin number is 13 then the 2nd number is 3, then add to the first number
  //
  //      if (pinNumber == 11) {
  //        digitalWrite(Relaypin, HIGH);
  //        Serial.println("Relay ON");
  //      }
  //      if (pinNumber == 12) {
  //        digitalWrite(Relaypin, LOW);
  //        Serial.println("Relay OFF");
  //      }
  //
  //      // make close command
  //      String closeCommand = "AT+CIPCLOSE=";
  //      closeCommand += connectionId; // append connection id
  //      closeCommand += "\r\n";
  //      sendData(closeCommand, 1000, DEBUG); // close connection
  //    }
  //  }
  delay(1000);
}

/*
  Name: sendData
  Description: Function used to send data to ESP8266.
  Params: command - the data/command to send; timeout - the time to wait for a response; debug - print to Serial window?(true = yes, false = no)
  Returns: The response from the esp8266 (if there is a reponse)
*/
String sendData(String command, const int timeout, boolean debug) {
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
