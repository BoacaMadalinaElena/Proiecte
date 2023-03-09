#include "usart.h"
void USART_initialize(unsigned short int baud_rate)
{
  /* seteaza baud rate */
  UBRRH = (unsigned char)(baud_rate >> 8);
  UBRRL = (unsigned char)(baud_rate & 0xFF);
  
  UCSRB = (1 << RXEN) | (1 << TXEN); 

  DDRD |= (1 << PD1);

  DDRD &= ~(1 << PD0);
  
  //UCSRB |= (1 << RXCIE);

}
void USART_transmit(unsigned char data)
{
  while (!( UCSRA & (1 << UDRE)))
  {
    ;
  }
  UDR = data;
}
unsigned char USART_Receive( void )
 {
 /* Asteapta receptionarea datelor */
  while ( !(UCSRA & (1<<RXC)) )
  {
    ;
  }
 /* Preia si returneaza datele receptionate din buffer */
  return UDR;
}
