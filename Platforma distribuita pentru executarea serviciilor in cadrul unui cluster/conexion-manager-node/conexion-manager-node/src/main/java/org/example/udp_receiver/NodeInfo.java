package org.example.udp_receiver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NodeInfo {
    private String ipAddress;
    private int port;
    private double ramTotal;
    private double ramFree;
    int processors;
    double loadAverage;
    // campul nu se deserializeaza
    private transient LocalDateTime localDateTime;

    public NodeInfo(String ipAddress, int port, double ramTotal, double ramFree,int processors,double loadAverage) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.ramTotal = ramTotal;
        this.ramFree = ramFree;
        this.processors = processors;
        this.loadAverage = loadAverage;
    }
}
