#include "myPrintf.h"

void myprint(unsigned int tip, unsigned int nr_car, void * val)
{
  //0 - int
  //1 - long,long cu 0x
  //3 - sir de caractere (unsigned char)

  switch(tip)
  {
  case 0:
    integerTransmit(tip, nr_car, val);
    break;
  case 1:
    hexadecimalTransmit(tip, nr_car, val);
    break;
  case 3:
    characterTransmit(tip, nr_car, val);
    break;
  }
}

//transmiterea unui intreg pe seriala
void integerTransmit (unsigned int p1, unsigned int p2, void * p3)
{
  int index=0;
  char aux[5];
  int x=*((int *)(p3));
  if(x<0)
  {
    USART_transmit('-');
    x*=(-1);
    p2--;
  }
  while(x!=0)
  {
    aux[index]=x%10+'0';
    index++;
    x=x/10;
  }
  while(p2>0 )
  {
    USART_transmit(aux[index-1]);
    index--;
    p2--;
  }
}




//transmiterea unui sir de caractere pe seriala
void characterTransmit (unsigned int p1, unsigned int p2, void * p3)
{
  unsigned char *x=(unsigned char *)(p3);
  int index=0;
  while(index!=p2)
  {
    USART_transmit(x[index]);
    index++;
  }
}

//transmiterea unui numar hexazecimal pe seriala
void hexadecimalTransmit (unsigned int p1, unsigned int p2, void * p3)
{
  long long x=*((long long *)(p3));
  int index=0;
  char aux[5];
  USART_transmit('0');
  USART_transmit('x');
  while(x!=0)
  {
    aux[index]=x&0x0F;
    if(aux[index]<=9)
    {
      aux[index]+='0';
    }
    else
    {
      aux[index]=aux[index]+'A'-10;
    }
    index++;
    x>>=4;
  }
  while(p2>0 && index>0)
  {
    USART_transmit(aux[index-1]);
    index--;
    p2--;
  }
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
