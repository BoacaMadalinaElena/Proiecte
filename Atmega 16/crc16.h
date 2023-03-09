#ifndef __CRC16__
#define __CRC16__

#define CRC16_CCITT 0x1021 
enum BitOrder { LSBF, MSBF };

unsigned int crc16__flash(unsigned int polinom16, unsigned int init_val_16,
                   unsigned int adr_start,unsigned int len, enum BitOrder ord);
unsigned int crc16wtable__flash(unsigned int init_val_16,unsigned int adr_start,
                                unsigned int len) ;
unsigned int crc16__data(unsigned int polinom16, unsigned int init_val_16,
                   unsigned int adr_start,unsigned int len, enum BitOrder ord);
unsigned int crc16wtable__data(unsigned int init_val_16,unsigned int adr_start,
                                unsigned int len) ;
#endif