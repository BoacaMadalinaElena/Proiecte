package org.example.execution_node;

import org.example.execution_node.conexion_tcp.ConnectionTCPManager;
import org.example.execution_node.conexion_udp.ConnectionUDPManager;
import org.example.execution_node.other.CustomPrinter;
import org.example.execution_node.sendLogs.SendLogs;
import org.example.execution_node.security.CustomSecurityManager;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ExecutionNode {

    public static void main(String[] args) throws IOException {
        CustomPrinter.printSuccess("Start");
        System.setProperty("java.security.policy", "all_permissions.policy");
        System.setSecurityManager(new CustomSecurityManager());

        CustomPrinter.printSuccess("Start ConnectionTCPManager");
        ConnectionTCPManager connectionTCPManager = ConnectionTCPManager.getCodeExecutorManager();
        Thread threadTCP = new Thread(connectionTCPManager);
        threadTCP.start();

        CustomPrinter.printSuccess("Start ConnectionUDPManager");
        ConnectionUDPManager connectionUDPManager = ConnectionUDPManager.getConnectionUDPManager();
        Thread threadUDP = new Thread(connectionUDPManager);
        threadUDP.start();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        SendLogs sendLogs = new SendLogs();
        executor.scheduleAtFixedRate(sendLogs, 0, 2, TimeUnit.MINUTES);
    }
}