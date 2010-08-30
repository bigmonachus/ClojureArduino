
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
  
  spiWrite(0x20,135);
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
    Serial.println("Hello World!");
  }
  else{
    Serial.println("Accelerometer read error.");
    delay(100);
  }
}
