package org.example.execution_node.conexion_udp.udp_multicast;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.conexion_udp.udp_multicast.system_info.InfoToSend;
import org.example.execution_node.conexion_udp.udp_multicast.system_info.SystemInfoManager;
import org.example.execution_node.other.CustomPrintStreamError;
import org.example.execution_node.other.CustomPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class MulticastUDPTransmitter {
    private final SystemInfoManager systemInfoManager;
    private final Gson gson;
    private MulticastSocket multicastSocket;
    private InetAddress inetAddress;

    public MulticastUDPTransmitter() {
        systemInfoManager = new SystemInfoManager();
        gson = new Gson();

        try {
            inetAddress = InetAddress.getByName("230.1.1.1");
            InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, 1234);
            multicastSocket = new MulticastSocket(5775);

            StringBuilder out = new StringBuilder();
            try {
                Process process = Runtime.getRuntime().exec("uname -a");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
                CustomPrinter.printErr("Cerere esuata localAddress is null!");
                System.exit(-1);
            }
            if (out.toString().contains("Debian") || out.toString().contains("Ubuntu")) {
                multicastSocket.joinGroup(inetSocketAddress, NetworkInterface.getByName("wlo1"));
            } else {
                multicastSocket.joinGroup(inetSocketAddress, NetworkInterface.getByName("eth0"));
            }
        } catch (IOException ioException) {
            String stacktrace = ExceptionUtils.getStackTrace(ioException);
            CustomPrinter.printErr(stacktrace);
            System.exit(-1);
        }
    }

    public void send() throws IOException {
        InfoToSend infoToSend;
        String stringToSend;
        DatagramPacket datagramPacket;

        infoToSend = systemInfoManager.getSystemInfo();
        stringToSend = gson.toJson(infoToSend);
        datagramPacket = new DatagramPacket(stringToSend.getBytes(), stringToSend.length(), inetAddress, 5775);

        multicastSocket.send(datagramPacket);
        // CustomPrinter.printNormal("UDP send -> " + stringToSend + ColorPrint.RESET);
    }
}
