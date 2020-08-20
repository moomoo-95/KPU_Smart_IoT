#include "timer.h"

#include "SoftReset.h"



int i=0;

int p=0;

void setup() {

  // put your setup code here, to run once:

  Serial.begin(9600);



}



void loop() {

  // put your main code here, to run repeatedly:

  while(1==1){

  Serial.println(p);

  p++  ;

  if(millis()>400){

    soft_restart();

  }

  }

  



}
