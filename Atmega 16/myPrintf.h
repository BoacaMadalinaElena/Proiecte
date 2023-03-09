#ifndef __MYPRINTF__
#define __MYPRINTF__

#include "usart.h"

void myprint(unsigned int tip, unsigned int nr_car, void * val);
void integerTransmit (unsigned int p1, unsigned int p2, void * p3);
void characterTransmit (unsigned int p1, unsigned int p2, void * p3);
void hexadecimalTransmit (unsigned int p1, unsigned int p2, void * p3);
void USART_transmit_ulonglong(unsigned long long n);

#endif