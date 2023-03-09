#include "usart.h"
#include "myPrintf.h"
#include <inavr.h> //include biblioteca inavr.h
#include <ioavr.h> //include biblioteca iom16.h

//#define transmitHelloWorldLaPrimireaSpeSeriala //BUN
//#define myPrintfLab5  //BUN
//#define timer1OverflowGeneral //BUN INTRA IN INTRERUPERE
//#define intrerupereExternaINT1 //BUN MERGE POZA CU GENERATORUL
//#define USARTcuIntreruperi //BUN MERGE
//#define CRC16peFlash //BUN MERGE
//#define CRC32peFlash //BUN MERGE
//#define TCNT0_50Hz //BUN MERGE
//#define TCNT1_1kHz //BUN MERGE
//#define semnal_1KHz_10KHz_Crestere_Plus_Minus //BUN MERGE
//#define semnal_1KHz_FU_5_95_Crestere_Plus_Minus //BUN MERGE
//#define semnal_1KHz_15KHz_FU_5_95_Crestere_Plus_Minus  //MERGE APROXIMATIV ERORI DESTUL DE MARI
//#define masurareImpulsFaraIntreruperi // ERORI DESTUL DE MARI
//#define masurareImpulsCuIntreruperi //CU CAT CRESTE FRECVENTA CRESTE EROAREA
//#define convertorStringToLong0xOrInt    //UTIL CU SERIALA
//#define CRCfunctie2Ret //BUN
//#define CRC16_RAM_String_Sir_nr         //sir din memorie 
//#define functionareWD //BUN resetare se vede cu led
//#define frecventaRealaWatchDog //BUN
//#define luminozitateCrescatoare //NU E FOLOSIT
//#define CRC16_RAM_String_Sir_primit_pe_seriala //BUN MERGE __PB1__
//#define timer1_2Khz_FU_5_95_pas_3_in_2_secunde //BUN
//#define timer120Khz_FU_5_95_pas_1_in_1_secunde //BUN
//#define CRC16_flash_0xXXXX_0xYYYY //BUN problema 4
//#define WDfrecventa5Hz_FU_66 //BUN  
//#define WDfrecventa_20Hz_FU_33  //__PB3__
//#define WD_timer_ms_101 //__PB7__
//#define WD_timer_hz_001 //__PB10__
#define crestere1_O_SECUNDA // crestre cu 1% la fiecare secunda
//#define WDfrecventa10Hz_FU_33 //__PB__11
//#define CRC16_RAM_0xXXXX_0xYYYY //__PB__22 a nu de da adrese din prima parte a memoriei ce pot fi scrise
//#define timer1_20kHZ_FU_creste_scade_1_in_o_secunda //__PB__2
//#define timer1_6kHZ_FU_creste_scade_1_in_o_secunda
//#define timer1_5_1KHz_FU_seriala 
//#define nrApasariReset
//#define timpDouaApasariReset
//#define timpDeLaUltimulReset 
//#define TCNT1_1kHz_v2

/*cand dau reset se afiseaza cat a trecut de la reseu-ul anterior*/

/*******************************************************************************
Sa se transmita prin comunicatie seriala un sir daca se primeste litera s sau S.
********************************************************************************/
#ifdef transmitHelloWorldLaPrimireaSpeSeriala

void main( void )
{
  unsigned char string[]="Hello world!", aux;
  unsigned int i=0;
  
  USART_initialize(BAUD_RATE);          //initializare parametri transmisie
  
  while(1)
    
  {
    aux=USART_Receive();                //functia returneaza un unsigned char cand primeste date
    i=0;
    if(aux=='s'||aux=='S')              //daca am primit s sau S
    {
      while(string[i]!='\0')            //cat nu am terminat sirul 
      {
        USART_transmit(string[i]);      //transmit sirul caracter cu caracter
        i++;
      }
    }
  }
}
#endif



/*******************************************************************************
//MyPrintf -> transmitere pe seriala int, hexazecimal, sir de caractere
********************************************************************************/
#ifdef myPrintfLab5

void main( void )
{
  USART_initialize(BAUD_RATE);          //initializare parametri transmisie
  unsigned int tip, nr_car;             //parametri pentru myPrintf
  
  
  //transmitere sir de caractere
  char sir[] = "Anul";
  tip = 3;
  nr_car = 4;
  myprint(tip,nr_car,sir);
  USART_transmit('\r');                 //trecere la linie noua
  USART_transmit('\n');
  
  //transmitere intreg 
  int nr = 6;
  int *pnr = &nr;
  tip = 0;
  nr_car = 1;
  myprint(tip,nr_car,pnr);
  USART_transmit('\r');
  USART_transmit('\n');
  
  //transmitere intreg 
  int nrNeg = -6;
  int *pnrNeg = &nrNeg;
  tip = 0;
  nr_car = 2;
  myprint(tip,nr_car,pnrNeg);
  USART_transmit('\r');
  USART_transmit('\n');
  
  //long long
  long long nrHexa = 0x123A;
  long long * pnrHexa = &nrHexa;
  tip = 1;
  nr_car = 5;
  myprint(tip,nr_car,pnrHexa);
  USART_transmit('\r');
  USART_transmit('\n');
  
  while(1);         
}
#endif


/*******************************************************************************
//Timer1 OVERFLOW verificare intrare in rutina de intrerupere
********************************************************************************/
#ifdef timer1OverflowGeneral

int test = 0;

#pragma vector = TIMER1_OVF_vect        //asociere vector de intrerupere functie de tratare
__interrupt void isr_TIMER1_overflow(void)
{
  test += 1;
}

int main()
{
  __disable_interrupt();
  
  //WGM13 WGM12 WGM11 WGM10 -> 1110(14) Fast PWM
  //CS12 CS11 CS10 =  001 -> fara prescaler frecventa numarare 4MHz 
  //COM1A1 = 1 -> Reseteazã OC1A/OC1B la potrivire, seteazã OC1A/OC1B la BOTTOM, (mod neinversabil)
  //TOIE1 intrerupere TIMER1 ovf 
  
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);
  TIMSK|=(1<<TOIE1);
  
  __enable_interrupt();
  
  while(1)
  {
    ;
  }
}
#endif

/*******************************************************************************
intrerupereINT1 test general
*******************************************************************************/
#ifdef intrerupereExternaINT1
int test = 0;

//asociere vector de intrerupere cu functie definita de mine
#pragma vector = INT1_vect
__interrupt void isr_INT1(void)
{
  test += 1; // putem pune un breakpoint aici
}

int main( void )
{
  __disable_interrupt();//dezactiveaza intreruperile
  
  //INT1 este pe pinul PD3 
  DDRD |= (1 << PD1); // seteaz ca intrare
  MCUCR |= ((1 << ISC11) | (1 << ISC10)); // Pe frontul pozitiv al lui INT1 se genereaza o cerere de intrerupere 
  GICR |= (1 << INT1); //activeaz? intreruperea externa pe INT1 
  
  __enable_interrupt(); //activeaza intreruperea globala
  
  while (1)
  {
    ;
  }
}
#endif

/*******************************************************************************
//usart cu intreruperi 
********************************************************************************/
#ifdef USARTcuIntreruperi

char data;          //data receptionata
char flag;          //data a fost citita sau nu(scrisa)

// rutina de intrerupere la primirea de date pe seriala
#pragma vector = USART_RXC_vect
__interrupt void isr_USART_RXC_vect(void)
{
  // cand am primit date, le salvam in variabila data
  data = UDR;
  
  // setam flagul pe 1, adica exista ceva primit
  flag = 1;
}

void main( void )
{
  unsigned char string[] = "Hello world!";
  unsigned int i = 0;
  unsigned char primit;
  
  USART_initialize(BAUD_RATE);
  
  // activam intreruperea globala si intreruperea pentru primirea unor valori pe seriala
  UCSRB |= (1 << RXCIE);
  __enable_interrupt();
  
  
  while(1)
  {
    if (flag == 1) // primim data de pe serial (daca exista)
    {
      flag = 0;
      //in data e octetul primit pe seriala
      primit = data;                    //evitam cazul in care mai primesc ceva pe seriala si se schimba data
      i = 0; 
      if(primit == 's' || primit == 'S') // doar daca am primit caracterele 's' sau 'S' afisam 'Hello World'
      {
        while(string[i] != '\0') // cat timp nu s-a terminat sirul
        {
          USART_transmit(string[i]); // trimitem pe seriala acel caracter
          i++;
        }
      }
    }
    else
    {
      // nu am primit nimic
      ;
    }
  }
}

#endif

/*******************************************************************************
calcul CRC16 pe toata memoria flash
*******************************************************************************/
#ifdef CRC16peFlash
#include "crc16.h"

void main()
{
  unsigned int real_crc = *(__flash unsigned int *)(0x4000-2);
  //din serari pot seta ca CRC -ul flash-ului sa fie memorat la ultima adresa din Flash
  //CRC16 este pe 16 biti deci unsigned int 
  unsigned int myCRC = crc16__flash(CRC16_CCITT,0,0,(0x4000-2),MSBF);
  //functie care calculeaza efectiv CRC-ul
  unsigned int myCRC_t = crc16wtable__flash(0,0,0x4000-2);
  //functie care foloseste un tabel de valori pre calculate
  
  while(1)
  {
    ;
  }
  
}
#endif



/*******************************************************************************
calcul CRC32 pe toata memoria flash
*******************************************************************************/
#ifdef CRC32peFlash
#include "crc32.h"

void main(){
  unsigned long real_crc = *(__flash unsigned long *)(0x4000-4);
  //din setari se salveaza CRC-ul pe ultimi 4 octeti ai memoriei flash
  unsigned long my_real_crc = crc32__flash(CRC32P,0,0,0x4000-4,MSBF);
  //CRC32 calculat manual 
  unsigned long my_crc_t = crc32wtable__flash(0,0,0x4000-4);
  //CRC32 cu valori precalculate
  
  while(1)
  {
    
  }
}

#endif



/*******************************************************************************
//generare frecventa de 50Hz folosind TCNT0
********************************************************************************/
#ifdef TCNT0_50Hz

typedef unsigned short uint8_t;

// pentru a avea o frecventa de 50Hz numaram 78-79 de valori de la 178 la 255
#pragma vector = TIMER0_OVF_vect
__interrupt void Timer0_ovf()
{
  TCNT0 = 178;
}

void timer0_init()
{
  // initializare TIMER0
  //WGM01,WGM00 = 11 -> fast numara de la 0 la 0xFF 
  TCCR0 |= (1<<WGM00) | (1<<WGM01);
  
  // COM01 resetare OC0 la compare match
  TCCR0 |= (1<<COM01);
  
  //clk pt timer 0 = CLK_IO/1024 - cu prescaler
  TCCR0 |= (1<<CS00 | 1<<CS02);
  
  // setare pinul OC0 ca output (este pinul PB3)
  DDRB |= (1<<PB3);
  
  //intrerupere ovf pentru a seta cat numaram 
  TIMSK |= (1 << TOIE0);
}

void main()
{
  timer0_init(); // initializarea timer-ului
  
  __enable_interrupt();
  
  uint8_t duty;
  duty = 213; // duty cycle = 45% si deoarece Timer0 este pe 8 biti acesta poate numara pana la 255. 
  //numaram in 178-255 deci la 45/100 * 78 + 178 schimbarea de palier
  
  while(1) 
  {
    OCR0 = duty; // Output Compare Register
  }
}
#endif



/*******************************************************************************
//generare frecventa de 1KHz folosind TCNT1 factor 50%
********************************************************************************/
#ifdef TCNT1_1kHz

int cnt=0; //variabila pentru ?ntrerupere

//asociere vector de intrerupere cu functie
#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  cnt++;
}

//configurarea timer 1
void timer1_INIT()
{
  /*
  Modul Fast PWM: WGM13:10=1110;
  Fara prescaler: CS12:10=001; precizie mare
  Compare Output Mode: 10
  */
  
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);       //Seteazã OC1A/OC1B la potrivire, reseteazã OC1A/OC1B la BOTTOM, (mod inversabil)
  TIMSK|=(1<<TOIE1);
}

void main()
{
  //Alegerea pinului de iesire
  DDRD |= (1<<PD5);
  PORTD &= ~(1<<PD5);                   //initial 0 la potrivire il setez
  
  //Initializarea timer-ului
  timer1_INIT();
  
  //Valoarea care se incarca in registru in functie de frecventa necesara
  
  //ICR1 = 4000 teoretic se numara de la 0 la ICR1 in FAST PWM
  
  ICR1=3975; // 4021 = valoarea calculata
  
  //Factor de umplere de 50% se seteaza OC1A la potrivire 
  OCR1A=ICR1>>1;
  
  //pornirea intreruperii nu se folosesc 
  __enable_interrupt();
  
  while(1)
  {
    
  }
}
#endif



/*******************************************************************************
semnal cu frecventa intre 1KHz si 10KHz creste scade dupa +- transmis pe seriala
*******************************************************************************/

#ifdef semnal_1KHz_10KHz_Crestere_Plus_Minus

int count=2;        //la primul + am injumatatire initial semnal cu count 1

#pragma vector=USART_RXC_vect
__interrupt void usart_interrupt(void)
{
  switch(UDR)
  {
    
  case '+':
    //frecventa creste
    ICR1=(unsigned int)(3975/count);
    count++;
    OCR1A=ICR1>>1;  //noua valoare pentru factorul de umplere 
    if(count > 10)
      count = 10;
    break;
    
  case '-':
    ICR1=(unsigned int)(3975/count);
    count--;
    OCR1A=ICR1>>1;
    if(count < 1)
      count = 1;
    break;
  default:
    break;
  }
}


//intrerupere la timer 1 overflow nu e folosita
#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  ;
}

//configurarea timer 1
void timer1_INIT()
{
  
  /*
  Modul Fast PWM: WGM13:10=1110;
  Fara prescaler: CS12:10=001;
  Compare Output Mode: 10
  Intrerupere overflow
  */
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);
  TIMSK|=(1<<TOIE1);
}


void main()
{
  
  USART_initialize(BAUD_RATE);          //initializare comunicare seriala 
  
  //Alegerea pinului de iesire
  DDRD |= (1<<PD5);
  PORTD &= ~(1<<PD5);
  
  //Initializarea timer-ului
  timer1_INIT();
  
  //Valoarea care se incarca in registru in functie de frecventa necesara
  //pentru 1KHz este aproximativ 4000
  
  ICR1=3975;
  
  //Factor de umplere de 50%
  OCR1A=ICR1>>1;
  
  //pornirea intreruperii
  __enable_interrupt();
  
  while(1);
}
#endif


/*******************************************************************************
//masurare impuls fara intreruperi
********************************************************************************/
#ifdef masurareImpulsFaraIntreruperi

unsigned long long int nr_us = 0;
unsigned long long int nr_cicli = 0;
unsigned long long int nrOvf = 0;

//in total se poate masura 0xFFFFFFFFFFFFFFFF * 65,535 + 65535 = 0x10000000000000000 * 65537 * 0.25 us ca si timp 
//iar minim 0.25 us teoretic 

//functie pentru overflow la vector 
#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  nrOvf++;
}

//masurare impuls fara intrerupere
/*IDEE LA FORNT POZITIV PORNESC UN TIMER SI IL OPRESC CAND AM URMATORUL FRONT POZITIV
INMULTIND CAT A NUMARAT CU PERIODA UNUI CICLU REZULTA TIMPUL(PERIOADA) SEMNALULUI 
DE INPUT. Pentru a prinde unde trebuie(cat mai la inceput) frountul pozitiv 
inainte astept activ pana apare ????*/ 

void main( void )
{
  USART_initialize(BAUD_RATE);          //initializare transmisie pe seriala 
  TCCR1A=0;                             //initializare timer mod normal si dezactivare clock timer-ul nu numara 
  TCCR1B=0;
  DDRD &= ~(1<<PD2);                    //pinul PD2 este de input
  TIMSK |= (1<<TOIE1);                  //intrerupere pentru INT1 
  
  
  while((PIND & (1<<PD2)) == (1<<PD2)); //asteptam frontul negativ

  while((PIND & (1<<PD2))==0); //asteptam frontul pozitiv
  {
    //a aparut frontul pozitiv
    TCCR1B|=(1<<CS10);                  //pornire clock timer fara presclaer(precizie mai buna)
  }
  
  while((PIND & (1<<PD2))==(1<<PD2));   //asteptam frontul negativ
  
  while((PIND & (1<<PD2))==0);          //asteptam frontul pozitiv pentru a termina de masurat
  {
    //a aparut frontul pozitiv
    nr_cicli=TCNT1;                     //salvam numarul de ciclii
  }
  
  //calculam perioada in microsecunde
  nr_us=(nrOvf *65535 + nr_cicli) * 249689/ 1000000  ; //nr_us=nrciclii* (perioada unui ciclu, adica 0.25 microsecunde) * prescaler(1)
  
  //aici se poate trimite pe seriala perioada.
  //nr_us este in microsecunde 1000000/nr_us este in Hz dar pentru precizie transmit in mHz 
  
  USART_transmit_ulonglong( (unsigned long long)1000000000/nr_us);
  USART_transmit('m');
  USART_transmit('H');
  USART_transmit('z');
  USART_transmit('\r');
  USART_transmit('\n');
  
  // 1/nr_us * 1000000 = frecventa de la generator
  
}
#endif




/*******************************************************************************
//masurate impuls cu intreruperi
//cu cat creste frecventa creste eroarea
*******************************************************************************/
#ifdef masurareImpulsCuIntreruperi

unsigned int nr_t;
unsigned long long nrOvf = 0;
unsigned char flag = 0;
unsigned long long nrOvf2 = 0;

#pragma vector = INT1_vect
__interrupt void ISR_INT1()//mereu masor intre doua frounturi
{
  //intreruperea se apeleaza la fiecare front pozitiv, 
  //deci de fiecare data trebuie sa preluam valoarea TCNT1 si sa-l resetam
  nr_t = TCNT1;
  TCNT1 = 0;
  
  flag = 1;         //am avut front 
  
  nrOvf2 = nrOvf;   //numarul de ovf folosit in calcule 
  nrOvf = 0;        //numarul ce poate fi modificat de rutina
}

void timer1_init()
{
  // in modul normal => WGM1[3:0] = 4b'0000 ma intereseaza numararea si atat 
  // fara prescaler => CS1[2:0] = 3'b001 cresc precizia 
  TCCR1B |= (1 << CS10);
  TIMSK|=(1<<TOIE1);//intrerupere pentru ovf 
}

//asociere rutina de intrerupere cu vector 
#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  nrOvf++;
}

void main()
{ 
  unsigned long long int period;
  unsigned long long int frecventa;
  
  // initializare USART
  USART_initialize(BAUD_RATE);          
  TCNT1 = 0;
  
  // initializare TIMER/COUNTER1
  timer1_init();
  
  // activarea intreruperii externe INT1
  GICR |= (1 << INT1);
  
  // configurarea modului de activare a intreruperii INT1 (activare la front pozitiv)
  MCUCR |= (1 << ISC11) | (1 << ISC10);
  
  // activarea globala a intreruperilor
  __enable_interrupt();
  
  while(1)
  {
    if(flag == 1) 
      // daca flag este nenul, inseamna ca am avut front prinul numar transmis nu se pune :)
    {
      //perioada procesoe = 24.86382 ns A SE CALCULA
      period = (unsigned long long int) ((nrOvf2 *65535 + nr_t) * 249689 / 1000000); //in microsecunde 
      
      frecventa = 1000000000/period;    //in KHz
      
      flag = 0;
      USART_transmit_ulonglong(frecventa/1000);             //valoare in Hz
      USART_transmit('.');
      USART_transmit_ulonglong(frecventa % 1000);           //zecimale
      USART_transmit('H');
      USART_transmit('z'); 
      USART_transmit(0x0d);
      USART_transmit(0x0a);
    }
  }
  
}
#endif

/*******************************************************************************
//semnal cu frecventa 1KHz si factor de umplere 5% 95% 
creste scade dupa +- transmis pe seriala
*******************************************************************************/
#ifdef semnal_1KHz_FU_5_95_Crestere_Plus_Minus

unsigned long long int count=5; //calcului ICR1 * count sa nu dea overlow

#pragma vector=USART_RXC_vect
__interrupt void usart_interrupt(void)
{
  switch(UDR)
  {
    
  case '+':
    //FU creste
    count += 5;
    if(count > 95)
      count = 95;
    OCR1A=(unsigned int)( count * ICR1/100);
    break;
    
  case '-':
    count -= 5;
    if(count < 5)
      count = 5;
    OCR1A=(unsigned int)( count * ICR1/100);
    break;
  default:
    break;
  }
}

//intrerupere overflow nu este folosita
#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  ;
}

//configurarea timer 1
void timer1_INIT()
{
  
  /*
  Modul Fast PWM: WGM13:10=1110;
  Fara prescaler: CS12:10=001;          //Seteazã OC1A/OC1B la potrivire, reseteazã OC1A/OC1B la BOTTOM, (mod inversabil)
  Compare Output Mode: 10
  Activare intreruperi
  */
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);
  TIMSK|=(1<<TOIE1);
}


void main()
{
  
  USART_initialize(BAUD_RATE);          //initializare pentru transmitere pe seriala
  //Alegerea pinului de iesire
  DDRD|=(1<<PD5);
  PORTD &= ~(1<<PD5);
  
  //Initializarea timer-ului
  timer1_INIT();
  //Valoarea care se incarca in registru in functie de frecventa necesara
  ICR1=3975;
  //Factor de umplere de 50%
  OCR1A=(unsigned int)(ICR1 * 5 / 100);
  
  //pornirea intreruperii
  __enable_interrupt();
  
  while(1)
  {
    
  }
}
#endif



/*******************************************************************************
//semnal cu frecventa [1KHz,15KHz] si factor de umplere [5%-95%] 
creste scade dupa +- transmis pe seriala +-500Hz, FU +- 3%
*******************************************************************************/
#ifdef semnal_1KHz_15KHz_FU_5_95_Crestere_Plus_Minus

unsigned long long int countFU=5;       //nu sunt necesare numere asa mari la FU
unsigned long long int countHz = 1000;  // calcul frecventa noua

#pragma vector=USART_RXC_vect
__interrupt void usart_interrupt(void)
{
  switch(UDR)
  {
  case '+':
    //FU creste
    countFU += 3;
    countHz += 500;
    
    if(countFU > 95)
      countFU = 95;
    if(countHz > 15000)
      countHz = 15000;
    
    ICR1=(unsigned int)((unsigned long long )3975 * 1000 /countHz);             //noua frecventa
    OCR1A=(unsigned int)( countFU * ICR1 /100);             //noul FU
    break;
    
  case '-':
    countFU -= 3;
    countHz -= 500;
    
    if(countFU <= 5)
      countFU = 5;
    if(countHz <= 1000)
      countHz = 1000;
    
    ICR1=(unsigned int)((unsigned long long )3975 * 1000/countHz);
    OCR1A=(unsigned int)(countFU * ICR1/100);
    break;
  default:
    break;
  }
}

//nu este folosit
#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  ;
}

//configurarea timer 1
void timer1_INIT()
{
  
  /*
  Modul Fast PWM: WGM13:10=1110;
  Fara prescaler: CS12:10=001;
  Compare Output Mode: 10
  */
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);
  TIMSK|=(1<<TOIE1);
}


void main()
{
  
  USART_initialize(BAUD_RATE);
  //Alegerea pinului de iesire
  DDRD|=(1<<PD5);
  PORTD &= ~(1<<PD5);
  
  //Initializarea timer-ului
  timer1_INIT();
  //Valoarea care se incarca in registru in functie de frecventa necesara
  ICR1=3975;
  //Factor de umplere de 5%
  OCR1A=(unsigned int)(ICR1* 5 /100);
  
  //pornirea intreruperii
  __enable_interrupt();
  
  while(1)
  {
    
  }
}
#endif

//-------------------------------------------------------------------------------------------------------------------------------------------------
//folosita cand primesc pe seriala un sir in hexa sau un intreg
//--------------------------------------------------------------------------------------------------------------------------------------------------
#ifdef convertorStringToLong0xOrInt

void stringToInt(int* rez,unsigned char sir[],unsigned int nrCar)
{
  int nr = 0,i,putere = 1;
  
  if(sir[0] == '-' || sir[0] == '+')
  {
    for(i=nrCar-1;i>=1;i--)
    {
      nr += (sir[i] - '0') * putere;
      putere *= 10;
    }
    if(sir[0] == '-')
      *rez = -nr;
    else
      *rez = nr;
  }
  else
  {
    for(i=nrCar-1;i>=0;i--)
    {
      nr += (sir[i] - '0') * putere;
      putere *= 10;
    }
    *rez = nr;
  }
}

//nu merge
void stringToHex(long long* nr, unsigned char sir[],unsigned int nrCar)
{
  long long rez;    //nr cu semn 
  unsigned int putere = 1;
  
  if(sir[0] == '-' || sir[0] == '+')
  {
    //sir[1],sir[2] = '0x' incep calculul de la nrCar-1
    for(int i=nrCar - 1;i >= 3;i-- )
    {
      if(sir[i] <= '9')
      {
        rez += (sir[i] - '0') * putere;
        putere *= 16;
      }
      else
      {
        rez += (sir[i] - 'A' + 10) * putere;
        putere *= 16;
      }
    }
  }
  else
  {
    for(int i=nrCar - 1;i >= 2;i-- )
    {
      if(sir[i] <= '9')
      {
        rez += (sir[i] - '0') * putere;
        putere *= 16;
      }
      else
      {
        rez += (sir[i] - 'A' + 10) * putere;
        putere *= 16;
      }
    }
  }
  if(sir[0] == '-')
    *nr = -rez;
  else
    *nr = rez;
}

unsigned long long  hexStrToInt(char *hex) {
  unsigned long long val = 0;
  while (*hex) {
    // extrag cifrele din memorie, sir terminat cu '\0'
    unsigned char byte = *hex++; 
    // transformare caracter in echivalentul lui 
    if (byte >= '0' && byte <= '9') byte = byte - '0';
    else if (byte >= 'a' && byte <='f') byte = byte - 'a' + 10;
    else if (byte >= 'A' && byte <='F') byte = byte - 'A' + 10;    
    // calculul il fac in hexa inmultesc cu 16 si adaug numarul meu(ultimi 4 biti - cifra hexa) 
    val = (val << 4) | (byte & 0xF);
  }
  return val;
}

int main( void )
{
  
  //merge
  int nrFaraSemn = 0, nrCuSemnMinus = 0, nrCuSemnPlus = 0;
  unsigned int nrCarFaraSemn = 1,nrCarCuSemnMinus = 2, nrCarCuSemnPlus = 2;          //plus pentru semnul minus
  unsigned char sirFaraSemn[5] = "0"; //plus terminator de sir si eventualul semn
  unsigned char sirCuSemnMinus[6] = "-0";
  unsigned char sirCuSemnPlus[6] = "+0";
  
  stringToInt(&nrFaraSemn,sirFaraSemn,nrCarFaraSemn);
  stringToInt(&nrCuSemnMinus,sirCuSemnMinus,nrCarCuSemnMinus);
  stringToInt(&nrCuSemnPlus,sirCuSemnPlus,nrCarCuSemnPlus);
  
  //merge
  long long nrHex1 = 0,nrHex2 = 0,nrHex3 = 0;
  char sir1[7] = "0x1234", sir2[8] = "0x0123",sir3[8] = "0x0000";
  
  nrHex1 = hexStrToInt(sir1 + 2);
  nrHex2 = hexStrToInt(sir2 + 2);
  nrHex3 = hexStrToInt(sir3 + 2);
  
  return 0;
}

#endif

/*******************************************************************************
//sa re calculeze CRC-ul blocului ocupat de functia
*******************************************************************************/
#ifdef CRCfunctie2Ret
#include "crc16.h"

//functia cu doua ret-uri
unsigned char min(unsigned char u,unsigned char y)
{
  if(u < y) return (u);
  else return (y);
}

//functie pentru calculul numarului de instructiuni ale unei functii
//stiu ca functia are doua instructiuni ret si se termina cu un RET 

unsigned int lenFun(unsigned int addrStart)
{
  unsigned int nr = 0;                  //parametru de return 
  unsigned int cod;                     //codificare instructiune 
  unsigned int ret = 0;                 //contorizare numar de ret 
  
  //codificare pe doi octeti 
  //ret are codificarea 9508, in memorie e 0895
  while(ret != 2)   //nu am gasit doua ret-uri mai caut....
  {
    cod = *(__flash unsigned int*)addrStart;                //extragere 16 biti din memoria FLASH 
    addrStart += 2;                     //trec la urmatorul octet 
    nr++;                               //cresc numarul de instructiuni
    if(cod == 0x9508)                   //daca codul e pentru ret
    {
      ret++;
    }
  }
  return nr;                            //returnez numarul de instructiuni
}

int main( void )
{
  //202f211710f4022f0895012f0895 instructiunea din memorie
  unsigned int addrMin = (unsigned int)min;//nr cuvinte, cast de la pointer la functie 16 biti la pointer la unsigned int
  addrMin *= 2;     //nr octeti
  unsigned int nrInstructiuni;
  //acum trebuie determinata lungimea memoriei ea are doua ret deci functia se termina dupa al doilea ret 
  nrInstructiuni = lenFun(addrMin);
  
  //crc transmit nr de octeti nu de cuvinte
  unsigned int crc16_lent = crc16__flash(CRC16_CCITT,0,addrMin,2*nrInstructiuni,MSBF);
  unsigned int crc16_rapid = crc16wtable__flash(0,addrMin,2*nrInstructiuni);
  
  //se poate transmite pe seriala :)
  while(1)
  {
    ;
  }
}
#endif

//------------------------------------------------------------------------------------------------------------------------------------------------
//calcul CRC16 pentru un sir stocat in ram
//--------------------------------------------------------------------------------------------------------------------------------------------------
#ifdef CRC16_RAM_String_Sir_nr
#include "crc16.h"

unsigned int nrCar(unsigned char sir[])
{
  unsigned int nr = 0;
  while(sir[nr] != 0)
  {
    nr++;
  }
  return nr;
}


int main( void )
{
  unsigned char sir[] = "Sir de transmis pe seriala!";
  unsigned int nr = nrCar(sir);
  
  //pentru conversie corect pointer pe 2 octeti, in memoria 0x0 - 0xFFFF
  unsigned int crc16_lent = crc16__data(CRC16_CCITT,0,(unsigned int)sir,nr,MSBF);
  unsigned int crc16_rapid = crc16wtable__data(0,(unsigned int)sir,nr);
  
  return 0;
}


/*

////CRC pe memoria RAM unde am memorat un sir de unsigned int 
//0x0001 in memorie e 0x0100 eu extrag ca si char
int main( void )
{
//unsigned int 16 bits -> o cifra 2 octeti
unsigned int sir[] = {1,2,3,4,5,6,7,8,9};
unsigned int nr = 18;

unsigned int crc16_lent = crc16__data(CRC16_CCITT,0,(unsigned int)sir,nr,MSBF);
unsigned int crc16_rapid = crc16wtable__data(0,(unsigned int)sir,nr);

return 0;
}
*/
#endif



/*******************************************************************************
//demonstrare functionare WD, problema lab nu pentru examen :), am scos delay_cycles
*******************************************************************************/
#ifdef functionareWD
__no_init unsigned char alternare;


void main()
{
  //__disable_interrupt();
  
  // Setare perioada de time-out 2.1s
  WDTCR |= (1 << WDP2) | (1 << WDP1) | (1 << WDP0);
  
  //Activare watchdog timer
  WDTCR |=(1 << WDE);
  
  // Setare PD5 ca pin de iesire
  DDRD |= (1 << PD5);
  
  //__enable_interrupt();
 
  if(alternare != 0)                    //se poate vedea resetarea pinului
  {
    PORTD |= (1 << PD5);
    alternare = 0;
  }
  else
  {
    PORTD &= ~(1 << PD5);
    alternare = 1;
  }
  // bucla infinita
  while(1)
  {
    
  }
}
#endif



/*******************************************************************************
calcul frecventa reala watchdog-ului 
teoretic 1MHz +- 30 % = [700,000-1,300,000]
prima afisare este aiurea :)
*******************************************************************************/
#ifdef frecventaRealaWatchDog

//variabile ce vor fi mentinute depa reset 
__no_init unsigned long long int overflowsNo;//numar de overflow-uri
__no_init unsigned int timerVal;//valoarea curenta a timer-ului

//algoritmul : se starteaza watchdog-ul si termer-ul in acelasi timp(aproximativ)
//se memoreaza pana la reset valoarea curenta a timer-ului dupa reset stiu nrOvf si 
//valoarea din timer deci numarul de cicli pana la reset 
//nr cicli = nrOvf * 65535 + timerVal 
//nrCicli * perioada ciclu = perioada timeout
//perioada timeout / nr cicli pt timeout = perioada watchdog

//asociere rutina de intrerupere cu vector 
#pragma vector = TIMER1_OVF_vect
__interrupt void isr_TIMER1_OVF_vect()
{
  overflowsNo++; 
}


void main( void )
{
  unsigned long long timePass = overflowsNo;
  
  USART_initialize(BAUD_RATE);          //initializare seriala 
  //DDRD |= (1 << PD1);                 //pd1 pin de iesire ? nu se foloseste 
  
  timePass *= 65536;
  timePass += timerVal;
  timePass = timePass * 249689 / 1000000; //perioada time-out in us
  
  unsigned long long freq = 524288;     //nr de cicli facuti pana la reset de watchdog
  freq = freq * 1000000000 / timePass;   //in mili secunde, idee de aproximare cat mai corecta nu da ovf 0xFFFFFFFFFFFFFFFF e suficient de mare :) 
  
  //frecventa
  USART_transmit_ulonglong(freq / 1000);//frecventa in Hz
  //USART_transmit('.');
  //USART_transmit_ulonglong(freq % 1000000);//cifrele dupa zecimala
  
  USART_transmit('H');
  USART_transmit('z');
  USART_transmit('\r');
  USART_transmit('\n');
  
  
  //perioada
  /*
  unsigned long long t = 1000000000/ freq ;//in secunde
  USART_transmit_ulonglong(t  / 1000);
  USART_transmit('.');
  USART_transmit_ulonglong(t  % 1000);
  USART_transmit('m');
  USART_transmit('s');
  USART_transmit('\r');
  USART_transmit('\n');
  */
  
  overflowsNo = 0;  
  timerVal = 0;
  
  //101 mod time out 
  WDTCR |= (1 << WDP2) |  (1 << WDP0);
  TIMSK |= (1 << TOIE1);                //intrerupere ovf
  TCCR1B |= (1 << CS10);                //nu folosec prescaler
  WDTCR |= (1 << WDE);                  //pornire watchdog trebuie dupa fiecare reset :) nu ca am pornit o data si gata
  __enable_interrupt();
  
  while(1)
    timerVal = TCNT1;                   //retin mereu valoarea curenta
}

#endif

//--------------------------------------------------------------------------------------------------------------------------------------------------
//cresterea luminozitatii unui led
//-------------------------------------------------------------------------------------------------------------------------------------------------
#ifdef luminozitateCrescatoare
void Init()
{
  /*
  Timer Clock = CPU Clock (fãrã prescalare)
  Mode = Fast PWM
  */
  TCCR0|=(1<<WGM00)|(1<<WGM01)|(1<<COM01)|(1<<CS00);
  //Setare pin OC0 ca output
  DDRB|=(1<<PB3);
}
void SetOutput(unsigned int duty)
{
  OCR0=duty; //Output Compare registrer
}
void Wait()//este necesarã o funcþie de aºteptare
{
  __delay_cycles(3200);
}
void main()
{
  unsigned int brightness=0;
  Init();
  while(1)
  {
    for(brightness=0;brightness<255;brightness++)
    {
      SetOutput(brightness);
      Wait();
    }
    for(brightness=255;brightness>0;brightness--)
    {
      SetOutput(brightness);
      Wait();
    }
  }
}
#endif

/*******************************************************************************
//calcul CRC16 pentru un sir stocat in ram, primit pe seriala cu terminat cu @
********************************************************************************/
#ifdef CRC16_RAM_String_Sir_primit_pe_seriala
#include "crc16.h"

//functie pentru calculul numarului de caractere 
unsigned int nrCar(unsigned char sir[])
{
  unsigned int nr = 0;
  while(sir[nr] != 0)
  {
    nr++;
  }
  return nr;
}


void main( void )
{
  unsigned char sir[40] = {0};//aloc un spatiu preventiv
  unsigned char nrCaractere = 0;//nr caractere primite 
  unsigned char caracter = 0;//caracterul ptimit acum
  
  USART_initialize(BAUD_RATE);//initializare seriala 
  
  caracter = USART_Receive();//astept sa primesc un caracter 
  while(caracter != '@')//nu am primit 0
  {
    if(caracter != 0)//se mai da la eroare la seriala 
    {
      sir[nrCaractere]  = caracter;//pun caracterul in vector 
      nrCaractere++;
    }
    caracter = USART_Receive();//astept altul 
  }
  
  unsigned long long crc16_lent = crc16__data(CRC16_CCITT,0,(unsigned int)sir,nrCaractere,MSBF);
  //unsigned int crc16_rapid = crc16wtable__data(0,(unsigned int)sir,nrCaractere);//sau asa
  
  unsigned char sirRez[] = "text transmis pe seriala de mine CRC16 ";
  unsigned int nr = nrCar(sirRez);
  
  myprint(3,nr,sirRez);//transmit pe seriala
  myprint(1,4,&crc16_lent);
  
  while(1);
}


#endif

/*******************************************************************************
generare semnal 2 KHz precizie 2% initial 5% creste scade cu 3% de la 5% la 95% 
in 2 secunde 
********************************************************************************/
#ifdef timer1_2Khz_FU_5_95_pas_3_in_2_secunde
#define FOSC 4000000
unsigned int nrOvf=0; //variabila pentru întrerupere numar ne ovf
unsigned long long  fu = 5;  //factorul de umplere

/*
in 2s am pentru o frecventa de 2kHz
1/(2khz).....1 perioada
2s.........x perioade
x/(2khz) = 2s => x = 2s*2khz = 2*2000 = 4000 de perioade in 2 secunde 
4000 de perioade in care fu trebuie sa creasca de (95-5)/3 = 30 de cresteri ale fu,
implementand cresterea cu nr ovf am 
deci 4000/30 = 133.3 la fiecare ~133 de ovf se schimba fu ;)
*/
unsigned char flag = 0;//dicrectie crestere/scadere

#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  nrOvf++;//incrementez nr de ovf
  if(nrOvf % 133 == 0)//trebuie sa cresc FU
  {
    if(flag == 1)
      fu += 3;
    else
      fu -= 3;
    if(fu >= 95)
    {
      fu = 95;
      flag = 0; //incepe decrementarea
    }
    else if(fu <= 5)
    {
      fu = 5;
      flag = 1;//incepe incrementarea
    }
    
    OCR1A = fu * ICR1 / 100;
    USART_transmit('F');
    USART_transmit('U');
    USART_transmit('=');
    USART_transmit_ulonglong((unsigned long)(fu));
    USART_transmit('%');
    USART_transmit('\r');
    USART_transmit('\n');
  }
  
}

//configurarea timer 1
void timer1_INIT()
{
  /*
  Modul Fast PWM: WGM13:10=1110;
  Fãrã prescaler: CS12:10=001;
  Compare Output Mode: 10
  */
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);
  TIMSK|=(1<<TOIE1);//intrerupere pt iverflow
}

void main()
{
  USART_initialize(BAUD_RATE);//initializare pentru seriala 
  //Alegerea pinului de iesire
  DDRD |= (1<<PD5);
  PORTD &= ~(1<<PD5);
  //Initializarea timer-ului
  timer1_INIT();
  //Valoarea care se încarca în registru în functie de
  //frecventa necesara
  ICR1=FOSC/2000;
  //Factor de umplere de 50%
  OCR1A=fu * ICR1 / 100 ;
  //pornirea întreruperii
  __enable_interrupt();
  while(1)
  {
  }
}
#endif

/*********************************************************************************************************
2.Implementati un program care sa genereze un semnal dreptunghiular cu frecventa 20kHz cu eroare 2%. 
Factorul de umplere FU se modifica automat(creste scade cu 1%) de la 5% la 95% într-o secunda. 
FU obiinut se transmite pe seriala sub forma <FU-XX%>.
**********************************************************************************************************/
#ifdef timer120Khz_FU_5_95_pas_1_in_1_secunde
/*
1/20kHz..........1 parioade
1s...............x perioade 
x/(20000) = 1 -> x = 20000*1 = 20000
In 20000 de perioade fac (95-5)/1 = 90 de cresteri/scaderi pt FU 
20000/90 = 222.22, la 222 ovf fac modificare ovf 
*/
#define FOSC 4000000 
unsigned char flag = 0;
unsigned int nrOvf=0; //variabila pentru întrerupere
unsigned long long fu = 5;

//asociere intrerupere ovf cu functie 
#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  nrOvf++;
  if(nrOvf % 222 == 0)
  {
    if(flag == 1)
      fu += 1;
    else
      fu -= 1;
    if(fu >= 95)
    {
      fu = 95;
      flag = 0; //incepe decrementarea
    }
    else if(fu <= 5)
    {
      fu = 5;
      flag = 1;//incepe incrementarea
    }
    //print FU  
    OCR1A = ICR1 * fu / 100;
    nrOvf = 0;
  }
  
}

//configurarea timer 1
void timer1_INIT()
{
  /*
  Modul Fast PWM: WGM13:10=1110;
  Fãrã prescaler: CS12:10=001;
  Compare Output Mode: 10
  */
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);
  TIMSK|=(1<<TOIE1);//intrerupere ovf 
}

void main()
{
  //Alegerea pinului de iesire
  DDRD |= (1<<PD5);
  PORTD &= ~(1<<PD5);
  //Initializarea timer-ului
  timer1_INIT();
  //Valoarea care se încarca în registru în functie de
  //frecventa necesara
  ICR1=FOSC/20000;
  //Factor de umplere de 50%
  OCR1A= fu * ICR1 /100 ;
  //pornirea întreruperii
  __enable_interrupt();
  while(1)
  {
    ;
  }
}
#endif

/*******************************************************************************************************************************************
Sa se calculeze si sa se transmitã pe seriala 
CRC16-ul zonei de memorie de tip flash(program) 
de la adresa 0xXXXX-0xYYYY sub forma: <CRC16 
ADR[0xXXXX-0xYYYY] 0xCCCC>. 
Adresele de început(0xXXXX) si de sfârsit 
(0xYYYY) se vor introduce de la tastatura(prin seriala PC).

*******************************************************************************************************************************************/
#ifdef CRC16_flash_0xXXXX_0xYYYY
#include "crc16.h"

//functie ce converteste un nr hexazecimal gen 12AB in long long 
unsigned long long  hexStrToInt(unsigned char *hex) {
  unsigned long long val = 0;
  while (*hex) {
    // extrag cifrele din memorie, sir terminat cu '\0'
    unsigned char byte = *hex++; 
    // transformare caracter in echivalentul lui 
    if (byte >= '0' && byte <= '9') byte = byte - '0';
    else if (byte >= 'a' && byte <='f') byte = byte - 'a' + 10;
    else if (byte >= 'A' && byte <='F') byte = byte - 'A' + 10;    
    // calculul il fac in hexa inmultesc cu 16 si adaug numarul meu(ultimi 4 biti - cifra hexa) 
    val = (val << 4) | (byte & 0xF);
  }
  return val;
}


int main()
{
  USART_initialize(BAUD_RATE);//initializare pentru seriala 
  unsigned char xxxx[7] = {0},yyyy[7] = {0},caracter ;//vectori pt adresele primite pe seriala
  //dimensiune: nr cifre + '\0'
  long long nrXXXX,nrYYYY;
  long long nrOcteti;//nr de octeti din flash pt care fac crc-ul 
  int cnt = 0;//nr valori citite
  
  //citire adrese
  do
  {
    caracter = USART_Receive();//astept un caracter
    if(caracter != 0)
    {
      xxxx[cnt]  = caracter;
      cnt++;
    }
  }while(cnt != 6);//citesc 6 caractere
  
  cnt = 0;//citesc al doilea nr 
  
  do
  {
    caracter = USART_Receive();//astept caracter pt al doilea nr
    if(caracter != 0)
    {
      yyyy[cnt]  = caracter;
      cnt++;
    }
  }while(cnt != 6);
  
  
  //conversie la int
  nrXXXX = hexStrToInt(xxxx + 2);
  nrYYYY = hexStrToInt(yyyy + 2);
  
  nrOcteti = nrYYYY - nrXXXX;
  nrOcteti++;       //inca un octet pentru ca am interval inchis 
  
  unsigned long long crc16_lent = crc16__flash(CRC16_CCITT,0,(unsigned int)nrXXXX,nrOcteti,MSBF);
  unsigned long long crc16_rapid = crc16wtable__flash(0,(unsigned int)nrXXXX,nrOcteti);
  
  //trimitere pe seriala
  //<CRC16 ADR[0xXXXX-0xYYYY] 0xCCCC>
  unsigned char sir[] = "CRC16 ADR[";
  myprint(3,10,(void*)sir);
  myprint(3,6,(void*)xxxx);
  USART_transmit('-');
  myprint(3,6,(void*)yyyy);
  USART_transmit(']');
  USART_transmit(' ');
  unsigned long long * ptr =& crc16_lent;
  myprint(1,4,(void*)ptr);
  
  return 0;
}

#endif

/******************************************************************************************************************
10.Sa se calculeze si sa se transmitã pe seriala in format XXXX KHz frecventa oscilatorului WATCHDOG in cazul 
configurãri prescalerului acestuia cu valoarea WDP2-0 = 001.
*******************************************************************************************************************/
#ifdef frecventaOscilatorWDWDP2_0_001
__no_init unsigned int overflowsNo;
__no_init unsigned int timerVal;

#pragma vector = TIMER1_OVF_vect
__interrupt void isr_TIMER1_OVF_vect()
{
  overflowsNo++; 
}

void USART_transmit_ulonglong(unsigned long long n)
{
  unsigned char aux[20];
  int index = 0;
  while(n)
  {
    aux[index++] = n % 10 + '0';
    n /= 10; 
  }
  index--;
  while(index >= 0)
  {
    USART_transmit(aux[index--]);
  }
}

void main( void )
{
  USART_initialize(BAUD_RATE);
  DDRD |= (1 << PD1);
  
  unsigned long long timePass = overflowsNo;
  timePass *= 65536;
  timePass += timerVal;
  timePass = timePass * 249 / 1000; //us
  
  unsigned long long freq = 32768;
  freq = freq * 1000000 / timePass;
  
  USART_transmit_ulonglong(freq / 1000);//frecventa in Hz
  USART_transmit('.');
  USART_transmit_ulonglong(freq % 1000);//cifrele dupa zecimala
  
  //USART_transmit_ulonglong((unsigned long long)timePass );//perioada timeout us 
  
  USART_transmit('u');
  USART_transmit('s');
  USART_transmit('\r');
  USART_transmit('\n');
  
  overflowsNo = 0;
  timerVal = 0;
  
  WDTCR |=   (1 << WDP0);
  TIMSK |= (1 << TOIE1);
  __enable_interrupt();
  TCCR1B |= (1 << CS10);
  WDTCR |= (1 << WDE);
  
  while(1)
    timerVal = TCNT1;
}

#endif

/***********************************
Sa se implementeze un generator de semnal dreptunghiular utilizând timer-ul de
WD existent pe ATmega16. 
Semnalul va avea o frecventa in jurul valorii de 5Hz si factor de umplere 66%.
************************************/
#ifdef WDfrecventa5Hz_FU_66 
//5Hz => 200 ms nu se poate exact cu watch-dog nu are perioade de time-out exacte
//66% =>132 ms (011)
//34% =>68 ms (010)


__no_init unsigned int state;//schimbare front la reset pt duty

//initializare timer pt corectie 
void timer0_init()
{
  // iniþializare TCCR0
  TCCR1B|=(1<<CS11)|(1<<CS10); //64 prescaler se poate si fara doar ca ies perioade mai mari   
  
}
void main( void )
{    
  DDRD = (1<<PD5); //setam pinul de iesire
  
  timer0_init();
  TCNT1=0;
  
  if(state==0x1)//front pozitiv
  {
    PORTD |= (1<<PD5);//pin de iesire pe 1
    while(TCNT1<1748);//se regleaza in functie de frecventa reala corectie fata de timeout teoretic
    
    //WD2:0 011
    WDTCR|=(1<<WDP0);
    WDTCR|=(1<<WDP1);
    WDTCR &= ~(1<<WDP2);
    state=0x0;//front negativ 
  }else
  {
    PORTD &= ~(1<<PD5);//pin pe 0
    while(TCNT1<988);//corectie
    
    //WD2:0 010
    WDTCR &= ~(1<<WDP2);
    WDTCR |= (1<<WDP1);
    WDTCR &= ~(1<<WDP0);
    state=0x1;//trec la front pozitiv 
  }
  
  WDTCR |= (1<<WDE);//activare watch-dog dupa configurarea in functie de front 
  
  while(1)
  {  
  }
}
#endif

/*******************************************************************************
Sa se calculeze si sa se transmit? pe seriala in format XXXX KHz frecventa oscilatorului
WATCHDOG in cazul configur?ri prescalerului acestuia cu valoarea WDP2-0 = 001.
se modifica dupa caz valorile pt WDP2:0 si cel de la impartire
pentru claritate a se reseta mai lent
********************************************************************************/
#ifdef WDfrecventaWDP2_0_001
__no_init unsigned int overflowsNo;
__no_init unsigned int timerVal;

#pragma vector = TIMER1_OVF_vect
__interrupt void isr_TIMER1_OVF_vect()
{
  overflowsNo++; 
}

void USART_transmit_ulonglong(unsigned long long n)
{
  unsigned char aux[20];
  int index = 0;
  while(n)
  {
    aux[index++] = n % 10 + '0';
    n /= 10; 
  }
  index--;
  while(index >= 0)
  {
    USART_transmit(aux[index--]);
  }
}

void main( void )
{
  USART_initialize(BAUD_RATE);
  DDRD |= (1 << PD1);
  
  unsigned long long timePass = overflowsNo;
  timePass *= 65536;
  timePass += timerVal;
  timePass = timePass * 249 / 1000; //us
  
  unsigned long long freq = 32768; // nr de cicli pt reset
  freq = freq * 1000000 / timePass;
  
  USART_transmit_ulonglong(freq / 1000);//frecventa in Hz
  
  //USART_transmit_ulonglong((unsigned long long)timePass );//perioada timeout us 
  
  USART_transmit('H');
  USART_transmit('z');
  USART_transmit('\r');
  USART_transmit('\n');
  
  overflowsNo = 0;
  timerVal = 0;
  
  WDTCR |=  (1 << WDP0); //configurare mod 
  TIMSK |= (1 << TOIE1);
  __enable_interrupt();
  TCCR1B |= (1 << CS10);
  WDTCR |= (1 << WDE);
  
  while(1)
    timerVal = TCNT1;
}
#endif


/*******************************************************************************
Calcula?i si transmite?i pe seriala in formatul XXX.X ms valoarea 
tipica a WD-timer-ului in cazul proces?rii prescalerului WDP2-0 = 101.
Adica 1/frecventa
********************************************************************************/
#ifdef WDvaloareTipicaWDtimer_101
__no_init unsigned int overflowsNo;
__no_init unsigned int timerVal;

#pragma vector = TIMER1_OVF_vect
__interrupt void isr_TIMER1_OVF_vect()
{
  overflowsNo++; 
}

void USART_transmit_ulonglong(unsigned long long n)
{
  unsigned char aux[20];
  int index = 0;
  while(n)
  {
    aux[index++] = n % 10 + '0';
    n /= 10; 
  }
  index--;
  while(index >= 0)
  {
    USART_transmit(aux[index--]);
  }
}

void main( void )
{
  USART_initialize(BAUD_RATE);
  DDRD |= (1 << PD1);
  
  unsigned long long timePass = overflowsNo;
  timePass *= 65536;
  timePass += timerVal; 
  timePass = timePass * 249 / 1000; // nr cicli * perioada ciclu -> e in us 
  
  unsigned long long freq = 32768; // nr de cicli pt reset
  //USART_transmit_ulonglong(timePass ); // e in ms
  freq = freq * 1000000 / timePass; //freq in Hz inmultita cu 1000
  USART_transmit_ulonglong(freq /1000 );
  USART_transmit('.');
  USART_transmit_ulonglong(freq  % 1000 );
  USART_transmit('H');
  USART_transmit('z');
  USART_transmit('\r');
  USART_transmit('\n');
  
  overflowsNo = 0;
  timerVal = 0;
  
  WDTCR |=  (1 << WDP0); //configurare mod 
  TIMSK |= (1 << TOIE1);
  __enable_interrupt();
  TCCR1B |= (1 << CS10);
  WDTCR |= (1 << WDE);
  
  while(1)
    timerVal = TCNT1;
}
#endif

#ifdef crestere1_O_SECUNDA
//Generati un semnal de 20kHz cu factor de umplere care variaza crescator si
//descrescator, cu pasi de 5% intre 5 si 95% la fiecare 1 secunda si
//transmiterea factorului de umplere pe serial
//65,535 * 1/4000000 -> 0.016384 s pt un ovf 
// 1/0.016384 = 61.03515625 ovf in o secunda

#define FOSC 4000000
unsigned int flag;
unsigned int cnt;

#pragma vector=TIMER1_OVF_vect
__interrupt void T1_INT(void){
  cnt++;
  if(cnt > 61){//asta ar trebui sa masoare o secnda
    flag = 1;
    cnt = 0;
  }
}

unsigned int cifre[2];//vrem un numar de 2 cifre pt procent

void print(unsigned int fu){
  
  USART_transmit('<');
  USART_transmit('F');
  USART_transmit('U');
  USART_transmit('-');
  unsigned int n = 0;
  
  while(fu > 0){
    cifre[n] = fu%10;
    fu /= 10;
    n++;
  }//transcriem cifrele
  
  for(int i = n-1; i>=0; i--){
    USART_transmit(cifre[i]+'0');//conversie la char
  }
  USART_transmit('%');
  USART_transmit('>');
 // USART_transmit(0x0a); //??
  USART_transmit('\n');
  USART_transmit('\r');
}
void main( void)
{
  unsigned int fu = 5;
  unsigned int pas = 1;
  
  TCCR1B = (1<<WGM13) | (1<<WGM12) | (1<<CS10);//setez timer fara prescale
  TCCR1A = (1<<WGM11) | (1<<COM1B1);//COM1B1 seteaza pinul PD4(OC1B)

  TIMSK = (1<<TOIE1);//enable intrerupere
  DDRD = (1<<PD4);//pe iesire
  PORTD = (1<<PD4);//pe iesire
  
  USART_initialize (BAUD_RATE );
  __enable_interrupt();
  
  ICR1 = FOSC/20000;
  
  while(1){
    if(flag == 1){
      flag = 0;
      if(fu < 95 && pas == 1){
        print(fu);
        OCR1B = ICR1 * fu / 100;
        fu += 5;
        if(fu == 95){
          pas = 0;//numratoare descrescatoare
        }
      }else if(fu > 5 && pas == 0){
        print(fu);
        OCR1B = fu *ICR1 / 100;
        fu -= 5;
        if(fu == 5){//a ajuns la minim
          pas = 1;//numaratoare crescatoare
        }
      }
    }
  }
}

#endif

/*************************************************
3.Sa se implementeze un generator de semnal dreptunghiular
utilizând timer-ul de WD existent pe ATmega16. Semnalul 
va avea o frecventa in jurul valorii de 20Hz si factor de umplere 33%. 
**************************************************/

#ifdef WDfrecventa_20Hz_FU_33
//20Hz => 50 ms nu se poate obtine exact :)
//33% =>16.3 ms (000) 
//66% =>32.5 ms (001)

__no_init unsigned int state;//front 


//timer pt corectie 
void timer0_init()
{
  // iniþializare TCCR0
  TCCR1B|=(1<<CS11)|(1<<CS10); //64 prescaler   
}

void main( void )
{    
  DDRD |= (1<<PD5); //setam pinul de iesire
  
  timer0_init();
  TCNT1=0;
  
  if(state==0x1)//sunt pe front pozitiv 
  {
    PORTD |= (1<<PD5);
    while(TCNT1<192);//se regleaza in functie de frecventa reala corectie fata de timeout teoretic    
    WDTCR &=~(1<<WDP2);
    WDTCR &=~(1<<WDP1);
    WDTCR &=~(1<<WDP0);
    state=0x0;//urmeaza front negativ
  }else
  {
    PORTD &= ~(1<<PD5);//pin pe 0
    while(TCNT1<455);//corectie 
    
    WDTCR &= ~(1<<WDP2);
    WDTCR &= ~(1<<WDP1);
    WDTCR |= (1<<WDP0);
    state=0x1;//urmeaza front pozitiv
  }
  
  WDTCR |= (1<<WDE);//activarea wd cu configuratiile facut la front 
  
  while(1)
  {  
  }
}
#endif

/*******************************************************************************
7.Calculati si transmiteti pe seriala in formatul XXX.X ms valoarea tipica
a WD-timer-ului in cazul procestrii prescalerului WDP2-0 = 101.
*******************************************************************************/
#ifdef WD_timer_ms_101
__no_init unsigned long long int overflowsNo;
__no_init unsigned int timerVal;//valoare timer la reset 

#pragma vector = TIMER1_OVF_vect
__interrupt void isr_TIMER1_OVF_vect()
{
  overflowsNo++; 
}


void main( void )
{
  USART_initialize(BAUD_RATE);
  //DDRD |= (1 << PD1);
  
  unsigned long long timePass = overflowsNo;
  timePass *= 65536;
  timePass += timerVal;
  timePass = timePass * 249689 / 1000000; //us
  
  unsigned long long freq = 524288;
  freq = freq * 1000000000 / timePass;//in khz 
  
  //frecventa
  
  /*
  USART_transmit_ulonglong(freq / 1000);//frecventa in Hz
  USART_transmit('.');
  USART_transmit_ulonglong(freq % 1000);//cifrele dupa zecimala
  USART_transmit('H');
  USART_transmit('z');
  USART_transmit('\r');
  USART_transmit('\n');
  */
  
  //perioada
  
  unsigned long long t =   1000000 / freq ;//freq in KHz -> t in ms  
  USART_transmit_ulonglong(t  );//ms 
  USART_transmit('.');
  USART_transmit_ulonglong((t  % 10000) / 100);//cele doua zecimale
  USART_transmit('m');
  USART_transmit('s');
  USART_transmit('\r');
  USART_transmit('\n');
  
  
  overflowsNo = 0;
  timerVal = 0;
  //101
  WDTCR |= (1 << WDP2) |  (1 << WDP0);
  TIMSK |= (1 << TOIE1);
  TCCR1B |= (1 << CS10);//fara prescaler 
  WDTCR |= (1 << WDE);
   __enable_interrupt();
   
  while(1)
    timerVal = TCNT1;
}
#endif

/**********************************************************************
10.Sa se calculeze si sa se transmita pe seriala in format XXXX Hz ?? pai e de 
oedinul mega HZ ?
frecventa oscilatorului WATCHDOG in cazul configuriri prescalerului 
acestuia cu valoarea WDP2-0 = 001.
**********************************************************************/
#ifdef WD_timer_hz_001
__no_init unsigned long long int overflowsNo;
__no_init unsigned int timerVal;
__no_init unsigned int print;//spam cu printari ;)

#pragma vector = TIMER1_OVF_vect
__interrupt void isr_TIMER1_OVF_vect()
{
  overflowsNo++; 
}

void main( void )
{
  USART_initialize(BAUD_RATE);
  DDRD |= (1 << PD1);
  
  unsigned long long timePass = overflowsNo;
  timePass *= 65536;
  timePass += timerVal;
  timePass = timePass * 249689 / 1000000; //us
  
  unsigned long long freq = 32768;
  freq = freq * 1000000000 / timePass;//in KHz
  
  //frecventa
  if(print >= 10)
  {  
    print = 0;
    USART_transmit_ulonglong(freq / 1000);//frecventa in Hz
    //USART_transmit('.');
    //USART_transmit_ulonglong(freq % 1000000);//cifrele dupa zecimala
    USART_transmit('H');
    USART_transmit('z');
    USART_transmit('\r');
    USART_transmit('\n');
  }
  else
    print++;
  
  //perioada
  /*
  unsigned long long t =   1000000/ freq ;//ms 
  USART_transmit_ulonglong(t);
  USART_transmit('.');
  USART_transmit_ulonglong(t  % 10000 / 100);
  USART_transmit('m');
  USART_transmit('s');
  USART_transmit('\r');
  USART_transmit('\n');
  */
  
  overflowsNo = 0;
  timerVal = 0;
  //001
  WDTCR |= (1 << WDP0);
  TIMSK |= (1 << TOIE1);
  TCCR1B |= (1 << CS10);
  WDTCR |= (1 << WDE);
  __enable_interrupt();
  
  while(1)
    timerVal = TCNT1;
}
#endif


/********************************************************************************
11.Sa se implementeze un generator de semnal dreptunghiular 
utilizând timer-ul de WD existent pe ATmega16. 
Semnalul va avea o frecventa in jurul valori de 10Hz
si factorul de umplere 33%. 
********************************************************************************/
#ifdef WDfrecventa10Hz_FU_33
//10Hz => 100 ms
//33% =>32.5 ms (001)
//64% =>65 ms (010)

__no_init unsigned int state;//front 

void timer0_init()
{
  // iniþializare TCCR0
  TCCR1B|=(1<<CS11)|(1<<CS10); //64 prescaler 
}

void main( void )
{    
  DDRD = (1<<PD5); //setam pinul de iesire
  
  timer0_init();
  TCNT1=0;
  
  if(state==0x1)//front pozitiv 
  {
    PORTD |= (1<<PD5);
    while(TCNT1<1248);//se regleaza in functie de frecventa reala corectie fata de timeout teoretic
    //WD2:0 001
    WDTCR |= (1<<WDP0);
    WDTCR &= ~(1<<WDP1);
    WDTCR &= ~(1<<WDP2);
    state=0x0;
  }
  else
  {
    PORTD &= ~(1<<PD5);
    while(TCNT1<905);//corectie 
    //WD2:0 010
    WDTCR &= ~(1<<WDP2);
    WDTCR|=(1<<WDP1);
    WDTCR &= ~(1<<WDP0);
    state=0x1;
  }
  
  WDTCR |= (1<<WDE);//pornire wd configuratia de mai sus 
  
  while(1)
  {  
    ;
  }
}
#endif

/***************************************************************************
22.Sa se calculeze si sa se transmit? pe seriala CRC16-ul zonei de memorie
ram(data) de la adresa 0xXXXX-0xYYYY sub forma: <CRC16 ADR[0xXXXX-0xYYYY]
0xCCCC>. Adresele de început(0xXXXX) si de sfârsit (0xYYYY) se vor 
introduce de la tastatura(prin seriala PC).
****************************************************************************/
#ifdef CRC16_RAM_0xXXXX_0xYYYY
#include "crc16.h"

unsigned long long  hexStrToInt(unsigned char *hex) {
  unsigned long long val = 0;
  while (*hex) {
    // extrag cifrele din memorie, sir terminat cu '\0'
    unsigned char byte = *hex++; 
    // transformare caracter in echivalentul lui 
    if (byte >= '0' && byte <= '9') byte = byte - '0';
    else if (byte >= 'a' && byte <='f') byte = byte - 'a' + 10;
    else if (byte >= 'A' && byte <='F') byte = byte - 'A' + 10;    
    // calculul il fac in hexa inmultesc cu 16 si adaug numarul meu(ultimi 4 biti - cifra hexa) 
    val = (val << 4) | (byte & 0xF);
  }
  return val;
}

int main()
{
  USART_initialize(BAUD_RATE);
  unsigned char xxxx[7] = {0},yyyy[7] = {0},caracter ;
  long long nrXXXX,nrYYYY;
  long long nrOcteti;
  int cnt = 0;
  
  //citire adrese
  do
  {
    caracter = USART_Receive();
    if(caracter != 0)
    {
      xxxx[cnt]  = caracter;
      cnt++;
    }
  }while(cnt != 6);
  
  cnt = 0;
  
  do
  {
    caracter = USART_Receive();
    if(caracter != 0)
    {
      yyyy[cnt]  = caracter;
      cnt++;
    }
  }while(cnt != 6);
  
  
  //conversie la int
  nrXXXX = hexStrToInt(xxxx + 2);
  nrYYYY = hexStrToInt(yyyy + 2);
  
  nrOcteti = nrYYYY - nrXXXX;
  nrOcteti++;       //inca un octet pentru ca am interval inchis 
  
  unsigned long long crc16_lent = crc16__data(CRC16_CCITT,0,(unsigned int)nrXXXX,nrOcteti,MSBF);
  unsigned long long crc16_rapid = crc16wtable__data(0,(unsigned int)nrXXXX,nrOcteti);
  
  //trimitere pe seriala
  //<CRC16 ADR[0xXXXX-0xYYYY] 0xCCCC>
  unsigned char sir[] = "CRC16 ADR[";
  myprint(3,10,(void*)sir);
  myprint(3,6,(void*)xxxx);
  USART_transmit('-');
  myprint(3,6,(void*)yyyy);
  USART_transmit(']');
  USART_transmit(' ');
  unsigned long long * ptr =& crc16_lent;
  myprint(1,4,(void*)ptr);
  
  return 0;
}

#endif

/*******************************************************************************
2.Implementa?i un program care sa genereze un semnal dreptunghiular cu frecventa
20kHz cu eroare 2%. Factorul de umplere FU se modifica automat(creste scade cu 1%)
de la 5% la 95% într-o secunda. FU ob?inut se transmite pe seriala sub forma 
<FU-XX%>.
********************************************************************************/
#ifdef timer1_20kHZ_FU_creste_scade_1_in_o_secunda

unsigned int nrOvf=0; //variabila pentru întrerupere
unsigned long long fu = 5;
unsigned long long FOSC = 4000000;

//întreruperea de overflow la timer1
/*
in 1s am pentru o frecventa de 20kHz 20000 de perioade in care fu trebuie sa creasca 
(95-5)/1 = 90 de cresteri ale fu, deci 20000/90 = 222.2 la fiecare ~222 de ovf se schimba fu 
*/
unsigned int flag = 0;


#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  nrOvf++;
  if(nrOvf % 222 == 0)
  {
    if(flag == 1)
      fu += 1;
    else
      fu -= 1;
    if(fu >= 95)
    {
      fu = 95;
      flag = 0; //incepe decrementarea
    }
    else if(fu <= 5)
    {
      fu = 5;
      flag = 1;//incepe incrementarea
    }
    //print FU 
    USART_transmit('F');
    USART_transmit('U');
    USART_transmit('=');
    USART_transmit_ulonglong((unsigned long)(fu));
    USART_transmit('%');
    USART_transmit('\r');
    USART_transmit('\n');
    OCR1A = ICR1 * fu / 100;
    nrOvf = 0;
  }
  
}




//configurarea timer 1
void timer1_INIT()
{
  /*
  Modul Fast PWM: WGM13:10=1110;
  Fãrã prescaler: CS12:10=001;
  Compare Output Mode: 10
  */
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);
  TIMSK|=(1<<TOIE1);//intrerupere ovf 
}


void main()
{
  USART_initialize(BAUD_RATE);
  //Alegerea pinului de iesire
  DDRD |= (1<<PD5);
  PORTD &= (1<<PD5);
  //Initializarea timer-ului
  timer1_INIT();
  //Valoarea care se încarca în registru în functie de
  //frecventa necesara
  ICR1=FOSC/20000;
  //Factor de umplere de 50%
  OCR1A=ICR1 * fu /100 ;
  //pornirea întreruperii
  __enable_interrupt();
  while(1)
  {
  }
}

#endif

/*******************************************************************************
2.5.Implementati un program care sa genereze un semnal dreptunghiular cu 
frecventa de 6kHz cu eroarea 2%. Factorul de umplere(FU) se modifica 
automat(creste/scade cu 3%) de la 5% la 95% într-o secunda. FU obtinut se 
transmite pe seriala sub forma <FU=XX%>.
********************************************************************************/
#ifdef timer1_6kHZ_FU_creste_scade_1_in_o_secunda

unsigned int nrOvf=0; //variabila pentru întrerupere
unsigned long long fu = 5;
unsigned long FOSC = 4000000;
//întreruperea de overflow la timer1
/*
in 1s am pentru o frecventa de 6kHz 6000 de perioade in care fu trebuie sa creasca 
(95-5)/1 = 90 de cresteri ale fu, deci 6000/90 = 67 la fiecare ~67 de ovf se schimba fu 
*/
unsigned int flag = 0;

#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  nrOvf++;
  if(nrOvf % 67 == 0)
  {
    if(flag == 1)
      fu += 1;
    else
      fu -= 1;
    if(fu >= 95)
    {
      fu = 95;
      flag = 0; //incepe decrementarea
    }
    else if(fu <= 5)
    {
      fu = 5;
      flag = 1;//incepe incrementarea
    }
    //print FU 
    USART_transmit('F');
    USART_transmit('U');
    USART_transmit('=');
    USART_transmit_ulonglong((unsigned long)(fu));
    USART_transmit('%');
    USART_transmit('\r');
    USART_transmit('\n');
    OCR1A = ICR1 * fu / 100;
    nrOvf = 0;
  }
}

//configurarea timer 1
void timer1_INIT()
{
  /*
  Modul Fast PWM: WGM13:10=1110;
  Fãrã prescaler: CS12:10=001;
  Compare Output Mode: 10 OC0 (PD5)
  */
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);
  TIMSK|=(1<<TOIE1);
}


void main()
{
  USART_initialize(BAUD_RATE);
  //Alegerea pinului de iesire
  DDRD|=(1<<PD5);
  PORTD&=(1<<PD5);
  
  //Initializarea timer-ului
  timer1_INIT();
  //Valoarea care se încarca în registru în functie de
  //frecventa necesara
  ICR1=FOSC/6000;
  //Factor de umplere de 50%
  OCR1A=ICR1 * fu /100 ;
  //pornirea întreruperii
  __enable_interrupt();
  while(1)
  {
  }
}

#endif

/******************************************************************************
23.Sa se implementeze un generator de semnal dreptunghiular cu frecventa 5.1
kHz cu factor de umplere in gama 1%-99%. Factorul de umplere se va prelua de 
pe USART in formatul XX%. Indica?ie : timer-ul este in mod PWM, octetul ascii
difer? de cifra zecimala.
*******************************************************************************/
#ifdef timer1_5_1KHz_FU_seriala

int nrOvf=0; //variabila pentru intrerupere
unsigned long long FOSC = 4000000;

//intreruperea de overflow la timer1
#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  nrOvf++;
}

//configurarea timer 1
void timer1_INIT()
{
  /*
  Modul Fast PWM: WGM13:10=1110;
  F?r? prescaler: CS12:10=001;
  Compare Output Mode: 10 PD5
  */
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);
  TIMSK|=(1<<TOIE1);
}

void main()
{
  unsigned long long fu = 10;
  unsigned char cifra;
  unsigned char sir[3] = {0};//factor de pe seriala 
  int cnt = 0;

  USART_initialize(BAUD_RATE);
  DDRD|=(1<<PD5);
  PORTD&=(1<<PD5);
  
  
  //Initializarea timer-ului
  timer1_INIT();
  ICR1=FOSC/5100; 
  
  //Factor de umplere de 50%
  OCR1A=fu * ICR1 / 100;
  while(1)
  {
    do{
      cifra = USART_Receive();
      sir[cnt] = cifra;
      cnt ++;
    }while(cnt != 3);
    cnt = 0;//citire mereu FU
    
    fu = (sir[0] - '0') * 10 + sir[1] - '0';
    
    ICR1=FOSC/5100; 
    
    //Factor de umplere de 50%
    OCR1A=fu * ICR1 / 100;
    
    //pornirea intreruperii
    __enable_interrupt();
    
  }
}

#endif

/*******************************************************************************
27.calculati si transmiteti pe seriala in formatul X.XX secunde timpul dintre 
2 apasari succesive ale tastei reset
*******************************************************************************/
#ifdef timpDouaApasariReset
char cifre[10];
int n = 0;


__no_init int stare;
__no_init int timp;
__no_init unsigned long long cnt;
unsigned long long  perioada;

#pragma vector = TIMER1_OVF_vect
__interrupt void T1int(void){
  cnt++;
}

void main(){
  
  USART_initialize(BAUD_RATE);
  TCCR1B = (1<<CS10);//fara prescaler
  TIMSK = (1<<TOIE1);
  
  __enable_interrupt();
  if(stare == 1)//am apasat a doua oara
  {
    TCNT1 = 0; //nr de la 0 la 65535 
    perioada = cnt * 65536;//echivalent cu inmultirea cu 65536
    perioada += timp;//ia de cate ori o ajuns la 65...+rest...       
    unsigned long long  aux1=perioada*251538/1000000;//us 
    
    USART_transmit_ulonglong(aux1/1000000);//secunda
    USART_transmit('.');
    USART_transmit_ulonglong((aux1 % 1000000)/100000);//0.... secunde
    USART_transmit('\r');
    USART_transmit('\n');
    
    cnt = 0; //numara de cate ori sa ajuns la valoare 65536
    stare=0;
  }
  else//am apasat prima 
  {
    TCNT1 = 0;
    cnt = 0; 
    stare = 1;
  }
  while(1){
    timp = TCNT1;//ia val intre 0 si 65535
  }
}

#endif

#ifdef nrApasariReset
__no_init unsigned long long cnt;

void main(){
  
  USART_initialize(BAUD_RATE);
  cnt++;
  USART_transmit_ulonglong(cnt);
  USART_transmit('\r');
  USART_transmit('\n');
  while(1);
}

#endif

/*******************************************************************************
26.Calculati si transmiteti pe seriala in formatul XX.XX secunde timpul 
de la ultima apasare a butonului reset de pe placa.Sau intre doua reset-uri se 
poate afisa gen cand primesc pe seriala ceva valoarea de la ultimul reset 
********************************************************************************/
#ifdef timpDeLaUltimulReset
char cifre[10];
int n = 0;

__no_init int state;
__no_init int time;
__no_init unsigned long long cnt;
unsigned long long  perioada;

#pragma vector = TIMER1_OVF_vect
__interrupt void T1int(void){
  cnt++;
}
void main(){
  
  USART_initialize(BAUD_RATE);
  TCCR1B = (1<<CS10);
  TIMSK = (1<<TOIE1);
  
  __enable_interrupt();
  
  perioada = cnt * 65536;
  perioada += time;//ia de cate ori o ajuns la 65...+rest    
  unsigned long long  aux1=perioada*251538/1000000;//us 
  
  USART_transmit_ulonglong(aux1/1000000);
  USART_transmit('.');
  USART_transmit_ulonglong((aux1 % 1000000)/100000);
  USART_transmit('\r');
  USART_transmit('\n');
  
  TCNT1 = 0;
  cnt = 0;
  
  while(1){
    time = TCNT1;//ia val intre 0 si 65535
  }
}
#endif

/*******************************************************************************
//generare frecventa de 1KHz folosind TCNT1 factor 50%
********************************************************************************/

#ifdef TCNT1_1kHz_v2
unsigned long long FOSC = 4000000;
unsigned long long fCeruta = 1000;
int cnt=0; //variabila pentru ?ntrerupere

//asociere vector de intrerupere cu functie
#pragma vector=TIMER1_OVF_vect
__interrupt void T1int(void)
{
  cnt++;
}

//configurarea timer 1
void timer1_INIT()
{
  /*
  Modul Fast PWM: WGM13:10=1110;
  Fara prescaler: CS12:10=001; precizie mare
  Compare Output Mode: 10
  */
  
  TCCR1B|=(1<<WGM13)|(1<<WGM12)|(1<<CS10);
  TCCR1A|=(1<<COM1A1)|(1<<WGM11);       //Seteazã OC1A/OC1B la potrivire, reseteazã OC1A/OC1B la BOTTOM, (mod inversabil)
  TIMSK|=(1<<TOIE1);
}

void main()
{
  //Alegerea pinului de iesire
  DDRD |= (1<<PD5);
  PORTD &= ~(1<<PD5);                   //initial 0 la potrivire il setez
  
  //Initializarea timer-ului
  timer1_INIT();
  
  //Valoarea care se incarca in registru in functie de frecventa necesara
  
  //ICR1 = 4000 teoretic se numara de la 0 la ICR1 in FAST PWM
  
  ICR1=FOSC/fCeruta; // 4021 = valoarea calculata
  
  //Factor de umplere de 50% se seteaza OC1A la potrivire 
  OCR1A=ICR1>>1;
  
  //pornirea intreruperii nu se folosesc 
  __enable_interrupt();
  
  while(1)
  {
    
  }
}
#endif

