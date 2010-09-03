
int SS = 10;
int MOSI = 11;
int MISO = 12;
int CLK = 13;

void setup() {
  byte clr = 0;
  Serial.begin(9600);
  pinMode(SS,OUTPUT);
  pinMode(MOSI,OUTPUT);
  pinMode(MISO,INPUT);
  pinMode(CLK,OUTPUT);
  digitalWrite(SS,HIGH);
  SPCR = (1<<SPE)|(1<<MSTR)|(1<<CPOL)|(1<<CPHA); 
  clr = SPDR;
  clr = SPSR;
  delay(100);
  // 11 Power up
  // 00 40 hz acceleration data
  // 0  Test mode off
  // 111 Three axis enabled
  //11000111
  spiWrite(0x20,0b11000111);
  //char ctrl = spiRead(0x20);
  //  Serial.println(ctrl,DEC);
}

char spiTransfer(char data){
  SPDR = data;
  while(!(SPSR&(1<<SPIF))){};
  return SPDR;
}

char spiRead(char reg){
  char in;
  char data = 128|reg;
  digitalWrite(SS,LOW);
  spiTransfer(data);
  in = spiTransfer(0xDEAD);
  digitalWrite(SS,HIGH);
  return in;
} 
void spiWrite(char reg,char data){
  digitalWrite(SS,LOW);
  spiTransfer(reg);
  spiTransfer(data);
  digitalWrite(SS,HIGH);
}

void loop() {
  char c = Serial.read();
  char id = spiRead(0xF); //WHO_AM_I  
  if (id == 0x3a){
    char x_l = spiRead(0x28);
    char x_h = spiRead(0x29);
    char y_l = spiRead(0x2a);
    char y_h = spiRead(0x2b);
    char z_l = spiRead(0x2c);
    char z_h = spiRead(0x2d);
    Serial.print(x_l);
    Serial.print(x_h);
    Serial.print(y_l);
    Serial.print(y_h);
    Serial.print(z_l);
    Serial.print(z_h);
    Serial.println();
  }
  else{
    Serial.println("Accelerometer read error.");
    delay(25);
  }
}
