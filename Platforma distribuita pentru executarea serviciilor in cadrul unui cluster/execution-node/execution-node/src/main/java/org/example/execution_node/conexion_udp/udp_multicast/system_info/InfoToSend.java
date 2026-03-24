package org.example.execution_node.conexion_udp.udp_multicast.system_info;

public record InfoToSend(double ramTotal, double ramFree, String ipAddress, int port, int processors, double loadAverage) {
}
