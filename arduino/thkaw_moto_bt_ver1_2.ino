/*
  Multicolor Lamp (works with Amarino and the MultiColorLamp Android app)
  
  - based on the Amarino Multicolor Lamp tutorial
  - receives custom events from Amarino changing color accordingly
  
  author: Bonifaz Kaufmann - December 2009
  
  
  NTL_Bluetooth_Car
  
  Extend Moto Function By Nathaniel - 2012/03/17
  E-Mail: s99115247@stu.edu.tw 
*/
 
#include <MeetAndroid.h>

// declare MeetAndroid so that you can call functions with it
MeetAndroid meetAndroid;

//moto A
int dir1PinA = 13;
int dir2PinA = 12;
int speedPinA = 10;

//moto B
int dir1PinB = 11;
int dir2PinB = 8;
int speedPinB = 9;

int Power_Val = 0; //用來儲存馬達出力程度;

void setup()  
{
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(9600); 
  
  // register callback functions, which will be called when an associated event occurs.
  
  meetAndroid.registerFunction(Forward, 'F');
  meetAndroid.registerFunction(Back, 'B');
  meetAndroid.registerFunction(Left, 'L');
  meetAndroid.registerFunction(Right, 'R');
  meetAndroid.registerFunction(Power, 'P');
  meetAndroid.registerFunction(Stop, 'S');

  //set Motor PinMode
  pinMode(dir1PinA, OUTPUT);
  pinMode(dir2PinA, OUTPUT);
  pinMode(speedPinA, OUTPUT);
  
  pinMode(dir1PinB, OUTPUT);
  pinMode(dir2PinB, OUTPUT);
  pinMode(speedPinB, OUTPUT);
  


}

void loop()
{
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
}

/*
 * 當APP送相對應命令來，下面的發法依照命令來選取並且執行
 */
void Stop(byte flag, byte numOfValues)
{
  StopDrive();
}

void Forward(byte flag, byte numOfValues)
{
  ForwardDrive();
}

void Back(byte flag, byte numOfValues)
{
  BackDrive();
}

void Left(byte flag, byte numOfValues)
{
  LeftDrive();
}

void Right(byte flag, byte numOfValues)
{
  RightDrive();
}

void Power(byte flag, byte numOfValues)
{
  Power_Val = meetAndroid.getInt();
}


//以下是馬達部分
void MOTODRIVE(char motor,int speed,int dir) //控制馬達用副程式
  {
     if(motor == 'R')
       {
         analogWrite(speedPinA, speed);
         if(dir == 1) 
         {
           digitalWrite(dir1PinA, LOW);
           digitalWrite(dir2PinA, HIGH);
         }
         else 
         {
           digitalWrite(dir1PinA, HIGH);
           digitalWrite(dir2PinA, LOW); 
         }     
       }
       
      if(motor == 'L')
       {
         analogWrite(speedPinB, speed);
         if(dir == 1) 
         {
           digitalWrite(dir1PinB, LOW);
           digitalWrite(dir2PinB, HIGH);
         }
         else 
         {
           digitalWrite(dir1PinB, HIGH);
           digitalWrite(dir2PinB, LOW); 
         }     
       }
  }
  
 
  
  
  void TESTDrive()//測試用的馬達動作
  {
    MOTODRIVE('R',0,0);
    MOTODRIVE('L',0,0);
    delay(50);
    MOTODRIVE('L',(Power_Val),1);
    delay(2000);
    MOTODRIVE('L',(Power_Val),0);
     delay(200);
  }
  
  void TESTDrive2()//測試用的馬達動作
  {
    MOTODRIVE('R',0,0);
    MOTODRIVE('L',0,0);
    delay(10);
    MOTODRIVE('R',(Power_Val),1);
    delay(5000);
    MOTODRIVE('R',(Power_Val),0);
     delay(200);
  }
  
   void ForwardDrive()
  {
   MOTODRIVE('R',(Power_Val),0);
   MOTODRIVE('L',(Power_Val),0);
  }
  
  void BackDrive()
  {
   MOTODRIVE('R',(Power_Val),1);
   MOTODRIVE('L',(Power_Val),1);
  }
  
   void LeftDrive()//新版左轉
  {
    MOTODRIVE('R',0,0);
    MOTODRIVE('L',0,0);
    delay(50);
    MOTODRIVE('L',(Power_Val),0);
   }
  
  void RightDrive()//新版右轉
  {
    MOTODRIVE('R',0,0);
    MOTODRIVE('L',0,0);
    delay(50);
    MOTODRIVE('R',(Power_Val),0);
   }
  
  void StopDrive()
  {
    MOTODRIVE('R',0,0);
    MOTODRIVE('L',0,0);
    delay(50);
  }
