#define SSID    "AndroidHotspot5044"
#define PASS    "0325274715"
#define DST_IP  "101.101.219.143"     //baidu.com

void setup()
{
  unsigned int i=0;
  // Open Serial1 communications and wait for port to open:
  Serial.begin(115200);
  Serial.setTimeout(5000);
  Serial.begin(115200); 
  Serial.println("ESP8266 Test Arduino code, 115200\r\n");
 
  Serial.println("AT");
  if(Serial.find("OK"))
    Serial.print("ESP8266 is alive\n");
  else {
    Serial.print("Module have no response\r\n");
    while(1);           //stay here if no module detected
  }
  
  Serial.println("AT+RST");
  //check if the WiFi always exists
  if(Serial.find("ready"))
    Serial.print("Reset is done\n");
  else {
    Serial.print("Reset failed\r\n");
    while(1);           //stay here if no module detected
  }
 
  Serial.println("AT+CWMODE=1");
  //1 means Station mode
  //2 means AP mode
  //3 means AP + Station mode
  
  //check if the WiFi always exists
  if(Serial.find("OK"))
    Serial.print("Station mode done\n");
  else {
    Serial.print("Station mode failed\r\n");
    while(1);           //stay here if no module detected
  }
  
  //connect to the AP 5 times
  boolean connected=false;
  for(int i=0;i<5;i++) {
    if(connectWiFi()) {
      connected = true;
      break;
    }
  }
  
  if (!connected){while(1);}      //fail to connect. hang here
  delay(2000);
  
  //print the ip addr
  Serial.println("AT+CIFSR");
  while(1) {
    if(Serial.available())
      Serial.write(Serial.read()); 
    if(i++ > 4000) break;     //just set enough counter, don't insert any delay function
  }   
  //set the single connection mode
  Serial.println("AT+CIPMUX=0");
  Serial.println("\r\nsingle connection mode\r\n");
}

void loop()
{
  unsigned int i=0, j=0;
  String cmd = "AT+CIPSTART=\"TCP\",\"";
  cmd += DST_IP;
  cmd += "\",8080";
  Serial.println(cmd);
  Serial.println(cmd);

  if(Serial.find("Error")) return;

  cmd = "GET / HTTP/1.0\r\n\r\n";
  
  Serial.print("AT+CIPSEND=");
  Serial.println(cmd.length());
  Serial.print("AT+CIPSEND=");
  Serial.println(cmd.length());
  
  if(Serial.find(">"))
  {
    Serial.print(">");
  }else
  {
    Serial.println("AT+CIPCLOSE");
    Serial.println("connect timeout");
    delay(1000);
    return;
  }
  
  Serial.print(cmd);
  Serial.println(cmd);
  Serial.find("SEND OK");
  
  while((i< 60000) && (j<60000)) {
    i++;j++;
    if(Serial.available())
    Serial.write(Serial.read());
  }
  Serial.println("\r\n====\r\n");
  delay(1000);
}

boolean connectWiFi()
{  
  String cmd="AT+CWJAP=\"";
  cmd+=SSID;
  cmd+="\",\"";
  cmd+=PASS;
  cmd+="\"";

  Serial.println(cmd);
  Serial.println(cmd);

  if(Serial.find("OK"))
  {
    Serial.println("OK, Connected to WiFi.");
    return true;
  } else {
    Serial.println("Can not connect to the WiFi.");
    return false;
  }
}
