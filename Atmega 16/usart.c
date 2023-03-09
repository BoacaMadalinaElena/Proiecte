#include "usart.h"
void USART_initialize(unsigned short int baud_rate)//16 biti
{
  /* seteaza baud rate */ // = numarul maxim de biti care se pot transmite intr-o secunda
  UBRRH = (unsigned char)(baud_rate >> 8);
  UBRRL = (unsigned char)(baud_rate & 0xFF);//este efectuat un si pe biti -> se copiaza
  UCSRB = (1 << RXEN) | (1 << TXEN);// pt a activa receptia si transmisia
  
  DDRD |= (1 << PD1);//pd1 = TXD = Usart output pin
  
  DDRD &= ~(1 << PD0);//pd0 = RXD = Usart input pin
  
  UCSRB |= (1 << RXCIE);//intrerupere pe flagul RXC este activa
  
}
void USART_transmit(unsigned char data)
{
  while (!(UCSRA & (1 << UDRE)))//UDRE = bit al registrului UCSRA, UDRE = usart data register empty
  {
    ;
  }
  //daca UDRE = 1 -> UDR este gata sa gol si gata sa primeasca noi date
  UDR = data;//registru de date pe 16 biti  (8 RXB - receive, 8 TXB - transmit)
}
unsigned char USART_Receive(void)
{
  /* A?teapta recep?ionarea datelor */
  while (!(UCSRA & (1 << RXC)))//RCX setat cand am primit date
  {
    ;
  }
  /* Preia ?i returneaza datele recep?ionate din buffer */
  //RXC = receive comlete = 1 daca UDR are date necitite in el
  return UDR;
}

