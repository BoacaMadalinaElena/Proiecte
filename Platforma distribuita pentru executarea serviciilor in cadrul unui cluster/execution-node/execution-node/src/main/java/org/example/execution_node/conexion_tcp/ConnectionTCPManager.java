package org.example.execution_node.conexion_tcp;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.other.CustomPrintStreamError;
import org.example.execution_node.other.CustomPrinter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.file.Files;
import java.util.Enumeration;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.example.execution_node.security.CustomSecurityManager;

public class ConnectionTCPManager implements  Runnable{
    private static ConnectionTCPManager connectionTCPManager;
    private final ServerSocket serverSocket;

    private ConnectionTCPManager(boolean cloud) throws IOException {
        InetAddress localAddress = null;
        int port = 0;

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            if ("eth0".equals(networkInterface.getName())) {
                Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();

                while (interfaceAddresses.hasMoreElements()) {
                    InetAddress address = interfaceAddresses.nextElement();

                    if (address instanceof Inet4Address) {
                        localAddress = address;
                        break;
                    }
                }
                break;
            }
        }

        if (localAddress != null) {
            serverSocket = new ServerSocket(port, 0, localAddress);
        } else {
            serverSocket = new ServerSocket(port);
        }

        String dirName = "serverJavaResourcesRemote";
        Path dirPath = Paths.get(dirName);
        if (Files.exists(dirPath)) {
            FileUtils.deleteDirectory(dirPath.toFile());
        }
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        }
    }

    private ConnectionTCPManager() throws IOException {
        InetAddress localAddress = null;
        int port = 0;

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();

            while (interfaceAddresses.hasMoreElements()) {
                InetAddress address = interfaceAddresses.nextElement();

                // 127.0.0.1
                // 192.168
                if (address instanceof Inet4Address && address.getHostAddress().startsWith("192.168.")) {
                    localAddress = address;
                    break;
                }
            }
        }

        if(localAddress != null){
            serverSocket = new ServerSocket(port, 0, localAddress);
        }else {
            serverSocket = new ServerSocket(port);
        }

        String dirName = "serverJavaResourcesRemote";
        Path dirPath = Paths.get(dirName);
        if (Files.exists(dirPath)) {
            FileUtils.deleteDirectory(dirPath.toFile());
        }
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        }
    }

    // Singleton
    public static ConnectionTCPManager getCodeExecutorManager() throws IOException {
        if(connectionTCPManager == null){
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
            if(out.toString().contains("Debian") || out.toString().contains("Ubuntu") ) {
                connectionTCPManager = new ConnectionTCPManager();
            }else{
                connectionTCPManager = new ConnectionTCPManager(true);
            }
        }
        return  connectionTCPManager;
    }

    @Override
    public void run(){
        while (true){
            try {
                Socket socket = serverSocket.accept();
                CustomPrinter.printInfo("New client: " + socket.getInetAddress() + "_" + socket.getPort());

                ThreadGroup threadGroup = new ThreadGroup(socket.getInetAddress().getHostName() + "_" + socket.getPort());
                CustomSecurityManager.addThreadGroup(socket.getInetAddress().getHostName() + "_" + socket.getPort());
                Thread clientThread = new Thread(threadGroup, new ClientSocket(socket));

                clientThread.start();
            }catch (IOException ioException){
                String stacktrace = ExceptionUtils.getStackTrace(ioException);
                CustomPrinter.printErr(stacktrace);
            }
        }
    }

    public  String getIpAddress(){
        return serverSocket.getInetAddress().toString().replace("/","");
    }

    public int getPort(){
        return serverSocket.getLocalPort();
    }
}
