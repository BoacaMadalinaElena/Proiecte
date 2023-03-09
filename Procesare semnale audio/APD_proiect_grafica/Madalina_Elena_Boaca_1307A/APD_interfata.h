/**************************************************************************/
/* LabWindows/CVI User Interface Resource (UIR) Include File              */
/*                                                                        */
/* WARNING: Do not add to, delete from, or otherwise modify the contents  */
/*          of this include file.                                         */
/**************************************************************************/

#include <userint.h>

#ifdef __cplusplus
    extern "C" {
#endif

     /* Panels and Controls: */

#define  PANEL                            1       /* callback function: OnMainPanel */
#define  PANEL_BINARYSWITCH               2       /* control type: binary, callback function: OnTime_Freq */
#define  PANEL_GRAPH_RAW                  3       /* control type: graph, callback function: (none) */
#define  PANEL_B_NEXT                     4       /* control type: command, callback function: OnNext */
#define  PANEL_B_PREV                     5       /* control type: command, callback function: OnPrev */
#define  PANEL_TEXTMSG                    6       /* control type: textMsg, callback function: (none) */
#define  PANEL_RING_FILTRU                7       /* control type: ring, callback function: (none) */
#define  PANEL_B_APLICA                   8       /* control type: command, callback function: OnAplica */
#define  PANEL_B_LOAD                     9       /* control type: command, callback function: OnLoadButtonCB */
#define  PANEL_GRAPH_HISTOGRAMA           10      /* control type: graph, callback function: (none) */
#define  PANEL_SPLITTER                   11      /* control type: splitter, callback function: (none) */
#define  PANEL_NUMERIC                    12      /* control type: numeric, callback function: AlphaSchimbat */
#define  PANEL_GRAPH_FILTRED              13      /* control type: graph, callback function: (none) */
#define  PANEL_COMMANDBUTTON              14      /* control type: command, callback function: OnAfisareAmvelopa */
#define  PANEL_RING_DIM                   15      /* control type: ring, callback function: (none) */
#define  PANEL_COMMANDBUTTON_2            16      /* control type: command, callback function: OnSave */
#define  PANEL_COMMANDBUTTON_3            17      /* control type: command, callback function: OnDerivata */
#define  PANEL_SPLITTER_2                 18      /* control type: splitter, callback function: (none) */
#define  PANEL_NUMERIC_STOP               19      /* control type: numeric, callback function: StopValue */
#define  PANEL_NUMERIC_START              20      /* control type: numeric, callback function: StartValue */
#define  PANEL_NUMERIC_TRECERI_0          21      /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_DISPERSIE          22      /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_MEDIANA            23      /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_MEAN               24      /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_INDEX_MAXIM        25      /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_MAX                26      /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_INDEX_MINIM        27      /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_MIN                28      /* control type: numeric, callback function: (none) */
#define  PANEL_OUTPUT_WAVE                29      /* control type: command, callback function: OnWriteOutput */

#define  PANEL_2                          2       /* callback function: OnPanel2 */
#define  PANEL_2_BINARYSWITCH             2       /* control type: binary, callback function: OnTime_Freq */
#define  PANEL_2_GRAPH_SPECTRUM           3       /* control type: graph, callback function: (none) */
#define  PANEL_2_RING                     4       /* control type: ring, callback function: OnNrEsantioane */
#define  PANEL_2_NR_FEREASTRA             5       /* control type: numeric, callback function: (none) */
#define  PANEL_2_NUMERIC_F_PEAK           6       /* control type: numeric, callback function: (none) */
#define  PANEL_2_NUMERIC_POWER_PEAK       7       /* control type: numeric, callback function: (none) */
#define  PANEL_2_GRAPH_ROW_FILTRED        8       /* control type: graph, callback function: (none) */
#define  PANEL_2_GRAPH_SPECTRUM_FILTRE    9       /* control type: graph, callback function: (none) */
#define  PANEL_2_GRAPH_ROW_WIN            10      /* control type: graph, callback function: (none) */
#define  PANEL_2_GRAPH_WINDOWING          11      /* control type: graph, callback function: (none) */
#define  PANEL_2_GRAPH_ROW                12      /* control type: graph, callback function: (none) */
#define  PANEL_2_GRAPH_WINDOW             13      /* control type: ring, callback function: OnTipFereastra */
#define  PANEL_2_FILTER_TYPE              14      /* control type: ring, callback function: OnTipFiltru */
#define  PANEL_2_NUMERIC_FPASS            15      /* control type: numeric, callback function: ChangeFpasAndFStop */
#define  PANEL_2_NUMERIC_FSTOP            16      /* control type: numeric, callback function: ChangeFpasAndFStop */
#define  PANEL_2_TEXTMSG                  17      /* control type: textMsg, callback function: (none) */
#define  PANEL_2_DECORATION               18      /* control type: deco, callback function: (none) */
#define  PANEL_2_TOGGLEBUTTON             19      /* control type: textButton, callback function: OnStartStop */
#define  PANEL_2_TIMER                    20      /* control type: timer, callback function: OnTimer */
#define  PANEL_2_RING_SECOND              21      /* control type: ring, callback function: ChangeSecond */
#define  PANEL_2_COMMANDBUTTON            22      /* control type: command, callback function: SaveFreq */
#define  PANEL_2_RING_TIP_AFISARE         23      /* control type: ring, callback function: tipAfisareSchimbat */
#define  PANEL_2_COMMANDBUTTON_2          24      /* control type: command, callback function: SaveWaveFrequency */
#define  PANEL_2_NUMERIC_ORDER            25      /* control type: numeric, callback function: OnOrder */


     /* Control Arrays: */

          /* (no control arrays in the resource file) */


     /* Menu Bars, Menus, and Menu Items: */

          /* (no menu bars in the resource file) */


     /* Callback Prototypes: */

int  CVICALLBACK AlphaSchimbat(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK ChangeFpasAndFStop(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK ChangeSecond(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnAfisareAmvelopa(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnAplica(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnDerivata(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnLoadButtonCB(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnMainPanel(int panel, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnNext(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnNrEsantioane(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnOrder(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnPanel2(int panel, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnPrev(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnSave(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnStartStop(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnTime_Freq(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnTimer(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnTipFereastra(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnTipFiltru(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnWriteOutput(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK SaveFreq(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK SaveWaveFrequency(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK StartValue(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK StopValue(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK tipAfisareSchimbat(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);


#ifdef __cplusplus
    }
#endif