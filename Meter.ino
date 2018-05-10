/**
 * Global Variables
 */
const int analogIn = A0;     //A0 pin to read in the voltage values from the sensor.                 
int mVperAmp = 100;          //mV to amperage conversion.  100mV = 1 amp.
int rawValue= 0;             //Raw value being read in from A0.
int aCSoffset1 = 2500;   //Offset to measure AC voltage.
double voltage = 0;          //Voltage values
double amps = 0;             //Amperage values

/**
 * setup()
 * 
 * Start baudrate at 115200
 */
void setup()
{
  Serial.begin(115200);
}

/**
 * loop()
 * 
 * Loop through and read A0.  Convert the reading into mV and then convert it into amps.
 */
void loop(){

 //Read in the value from A0.
 rawValue = analogRead(analogIn);     
 //Convert the value into mV.            
 voltage = (rawValue / 1024.0) * 5000;
 //Calculate the amperage from the mV
 amps = ((voltage - aCSoffset1) / mVperAmp);

 //Starting character sent through serial
 Serial.print("s");
 //Amperage value out to 3 digits after the decimal.
 Serial.print(amps,3);
 //Ending character sent through serial.
 Serial.println("f");
 //Delay for 1/2 second.
 delay(500);
}
