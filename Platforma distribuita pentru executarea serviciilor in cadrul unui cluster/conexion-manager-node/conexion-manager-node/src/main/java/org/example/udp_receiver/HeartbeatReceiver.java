package org.example.udp_receiver;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.notification.EmailSend;
import org.example.other.CustomPrintStreamError;
import org.example.other.CustomPrinter;
import org.example.tcp_management.TCPManagement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;import java.net.*;


public class HeartbeatReceiver implements Runnable {
    private static HeartbeatReceiver heartbeatReceiver;
    private MulticastSocket multicastSocket;
    private static Map<String, NodeInfo> nodes;

    private final Gson gson;

    private HeartbeatReceiver() {
        this.gson = new Gson();
        try {
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

            NetworkInterface networkInterface;
            if(out.toString().contains("Debian") || out.toString().contains("Ubuntu") ) {
                 networkInterface = NetworkInterface.getByName("wlo1");
            }else{
                networkInterface = NetworkInterface.getByName("eth0");
            }

            multicastSocket.setNetworkInterface(networkInterface);

            InetAddress inetAddress = InetAddress.getByName("230.1.1.1");
            multicastSocket.joinGroup(new InetSocketAddress(inetAddress, 5775), networkInterface);

            Map<String, NodeInfo> hMap = new HashMap<>();
            nodes = Collections.synchronizedMap(hMap);
        } catch (IOException ioException) {
            String stacktrace = ExceptionUtils.getStackTrace(ioException);
            CustomPrinter.printErr(stacktrace);
        }
    }

    public static HeartbeatReceiver getHeartbeatReceiver() {
        if (heartbeatReceiver == null) {
            heartbeatReceiver = new HeartbeatReceiver();
        }
        return heartbeatReceiver;
    }


    @Override
    public void run() {
        Thread shutdownThread = new Thread(() -> {
            while (true) {
                try {
                    synchronized (nodes) {
                        Iterator<String> iterator = nodes.keySet().iterator();
                        while (iterator.hasNext()) {
                            String s = iterator.next();
                            NodeInfo nodeInfo = nodes.get(s);
                            Duration duration = Duration.between(nodeInfo.getLocalDateTime(), LocalDateTime.now());

                            if (Math.abs(duration.toSeconds()) >= 60) {
                                CustomPrinter.printErr("Node: " + s + " is down!");
                                EmailSend.send("spammadalinaboaca@gmail.com","Problema...","Va imformam cu regret ca nodul : " + s + " nu a mai trimis mesaje despre starea sa. Va rugam sa verificati!");
                                iterator.remove();
                                CustomPrinter.printInfo("Number of nodes :  " + nodes.size());
                                TCPManagement.removeSocket(s);
                            }
                        }
                    }
                } catch (Exception ex){
                    String stacktrace = ExceptionUtils.getStackTrace(ex);
                    CustomPrinter.printErr(stacktrace);
                }
            }
        });
        shutdownThread.start();
        while (true) {
            try {
                byte[] buff = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                multicastSocket.receive(packet);
                String received = new String(
                        packet.getData(), 0, packet.getLength());

                NodeInfo nodeInfo = gson.fromJson(received, NodeInfo.class);
                String key = nodeInfo.getIpAddress() + ":" + nodeInfo.getPort();
                nodeInfo.setLocalDateTime(LocalDateTime.now());
                //CustomPrinter.printNormal("UDP message: " + gson.toJson(nodeInfo));
                if(!nodes.containsKey(key)){
                    CustomPrinter.printInfo("New node execution: " + key);
                }
                nodes.put(key, nodeInfo);
            } catch (IOException ioException) {
                String stacktrace = ExceptionUtils.getStackTrace(ioException);
                CustomPrinter.printErr(stacktrace);
            }
        }
    }

    public void remove(String key){
        synchronized (nodes){
            nodes.remove(key);
        }
    }

    public static Map<String,NodeInfo> getAllNodes()
    {
        return  nodes;
    }
}
