
#include "MeOrion.h"

MeFlameSensor FlameSensor1(PORT_8);
MeHumiture humiture(PORT_6);
MePort port(PORT_3);

Servo myservo1;  // create servo object to control a servo
Servo myservo2;

int16_t servo1pin =  port.pin1();//attaches the servo on PORT_3 SLOT1 to the servo object
int16_t servo2pin =  port.pin2();//attaches the servo on PORT_3 SLOT2 to the servo object

int dirPin = mePort[PORT_1].s1;//the direction pin connect to Base Board PORT1 SLOT1
int stpPin = mePort[PORT_1].s2;//the Step pin connect to Base Board PORT1 SLOT2

int dirPin2 = mePort[PORT_2].s1;//the direction pin connect to Base Board PORT1 SLOT1
int stpPin2 = mePort[PORT_2].s2;//the Step pin connect to Base Board PORT1 SLOT2

int inputCounter;
char bufferedInput[200];
int col;
int slowcounter = 0;

void readToBuffer() {
  clearBuffer();
  inputCounter = 0;
  while (Serial.available() > 0) {
    bufferedInput[inputCounter] = Serial.read();
    inputCounter++;
  }
  if (inputCounter == 127 || inputCounter == 128)
    while (Serial.available() > 0) {
      bufferedInput[inputCounter] = Serial.read();
      inputCounter++;
    }
}

void clearBuffer()
{
  for (inputCounter = 0; inputCounter < 150; inputCounter++) {
    bufferedInput[inputCounter] = '\0';
  }
}

void setup() {
  Serial.begin(9600);
  myservo1.attach(servo1pin);  // attaches the servo on servopin1
  myservo2.attach(servo2pin);
  pinMode(dirPin, OUTPUT);
  pinMode(stpPin, OUTPUT);

  pinMode(dirPin2, OUTPUT);
  pinMode(stpPin2, OUTPUT);

  myservo1.write(0);
  myservo2.write(67);

  buzzerOn();
        delay(100);
        buzzerOff();
        delay(100);
        buzzerOn();
        delay(100);
        buzzerOff();
}

void loop() {

  //myservo1.write(0);
  //myservo2.write(0);

  //myservo1.write(180);
  //myservo2.write(180);

  //step(1,200);//run 200 step
  //delay(1000);
  //step(0,200);
  //delay(1000);

  delay(1);

  slowcounter++;
  if (slowcounter == 1000) {
    humiture.update();
    Serial.print("Temp=");
    Serial.println( humiture.getTemperature() );
    slowcounter = 0;
  }
  if (FlameSensor1.readDigital() == Fire)
  {
    Serial.println("Fire");
    //Serial.println("move up a bit");
    //step(1,25);
  } else {
    //Serial.println("move down a bit");
  }

  if (Serial.available()) {
    delay(10);
    readToBuffer();

    if(bufferedInput[0] == 'C'){
      myservo2.write(67);
    } else if(bufferedInput[0] == 'O'){
      myservo2.write(10);
    }

    else if (bufferedInput[0] == 'b') {
        buzzerOn();
        delay(100);
        buzzerOff();
        delay(100);
        buzzerOn();
        delay(100);
        buzzerOff();
        delay(100);
        buzzerOn();
        delay(100);
        buzzerOff();
        delay(100);
    } else if (bufferedInput[0] == 'q') {
        buzzerOn();
        delay(200);
        buzzerOff();
        delay(200);
        buzzerOn();
        delay(200);
        buzzerOff();
    }

    // STEPPER
    else if (bufferedInput[0] == 'u') {
      //Serial.println("move up a bit");

      if (bufferedInput[1] == 'm') {
        step(0, 400);
      }
      else if (bufferedInput[1] == 'h') {
        step(0, 200);
      } else if (bufferedInput[1] == 'x') {
        step(0, 1000);
      }
      else {
        step(0, 50);
      }

    } else if (bufferedInput[0] == 'd') {
      //Serial.println("move down a bit");

      if (bufferedInput[1] == 'm') {
        step(1, 400);
      }
      else if (bufferedInput[1] == 'h') {
        step(1, 200);
      } else if (bufferedInput[1] == 'x') {
        step(1, 1000);
      }
      else {
        step(1, 50);
      }

      // SERVO
    } else if (bufferedInput[0] == 's') {
      if (bufferedInput[1] == '0') {
        myservo1.write(-100);
      } else {
        myservo1.write(280);
      }
    }
  }
}

void step(boolean dir, int steps)
{
  digitalWrite(dirPin, dir);
  digitalWrite(dirPin2, !dir);
  delay(50);
  for (int i = 0; i < steps; i++)
  {
    digitalWrite(stpPin, LOW);
    digitalWrite(stpPin2, LOW);
    delayMicroseconds(800);
    digitalWrite(stpPin, HIGH);
    digitalWrite(stpPin2, HIGH);
    delayMicroseconds(800);
  }
}

