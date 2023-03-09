#ifndef __CRC32__
#define __CRC32__

enum BitOrder { LSBF, MSBF };
#define CRC32P 0x4c11db7

unsigned long crc32__flash(unsigned long polinom32, unsigned long init_val_32, 
                           unsigned int adr_start,unsigned int len, enum BitOrder ord);
unsigned long crc32wtable__flash(unsigned int init_val_32, unsigned int adr_start, 
                          unsigned int len);
#endif