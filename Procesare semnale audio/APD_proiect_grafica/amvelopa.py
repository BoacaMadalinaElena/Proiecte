import numpy as np
from scipy.signal import hilbert;

def main():
    signal=[];
    file=open("waveData.txt","r");
    for line in file:
        signal.append(int(line));
    file.close()
    
    analytic_signal=hilbert(signal);
    amplitude_envelope=np.abs(analytic_signal);
    
    #np.savetxt("anvelopa.txt", amplitude_envelope, fmt="%2.0f")
    file=open("anvelopa.txt","w");
    for i in amplitude_envelope:
        file.write(str(i)+"\n");
    file.close()
        
main()
