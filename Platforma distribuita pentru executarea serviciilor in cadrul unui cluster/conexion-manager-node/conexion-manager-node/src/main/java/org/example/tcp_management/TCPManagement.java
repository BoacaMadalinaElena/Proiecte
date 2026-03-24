package org.example.tcp_management;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.httpClient.AutoDiscoveryHttp;
import org.example.notification.EmailSend;
import org.example.other.CustomPrinter;
import org.example.tcp_management.model.MessageToFront;
import org.example.tcp_management.socket.SocketWithExecutionNode;
import org.example.udp_receiver.HeartbeatReceiver;
import org.example.udp_receiver.NodeInfo;
import org.example.web_socket_management.CommunicationTCPEndpoint;
import org.example.web_socket_management.dto.Message;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class TCPManagement implements Runnable {
    private static final ConcurrentHashMap<String, SocketWithExecutionNode> listOfNodeExecutionSockets = new ConcurrentHashMap<>();
    private int currentNode = 0;
    private static final ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private static TCPManagement tcpManagementSingleton = null;
    private static final ConcurrentHashMap<String, String> ipUser = new ConcurrentHashMap<>();
    private final boolean type;

    private TCPManagement(boolean type) {
        this.type = type;
        CustomPrinter.printSuccess("Start server TCP!");
    }

    public static TCPManagement getInstance(boolean type) {
        if (tcpManagementSingleton == null) {
            tcpManagementSingleton = new TCPManagement(type);
        }
        return tcpManagementSingleton;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Map<String, NodeInfo> map = HeartbeatReceiver.getAllNodes();

                for (Map.Entry<String, NodeInfo> entry : map.entrySet()) {
                    String key = entry.getKey();
                    boolean ok = false;
                    for (Map.Entry<String, SocketWithExecutionNode> socketEntry : listOfNodeExecutionSockets.entrySet()) {
                        if (Objects.equals(socketEntry.getKey(), key)) {
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        // new connexion with new node
                        try {
                            SocketWithExecutionNode socketToAdd = new SocketWithExecutionNode(entry.getValue().getIpAddress(), entry.getValue().getPort());
                            listOfNodeExecutionSockets.put(entry.getValue().getIpAddress() + ":" + entry.getValue().getPort(), socketToAdd);
                            socketToAdd.start();

                            //listOfNodeExecutionSockets.remove(entry.getValue().getIpAddress() + ":" + entry.getValue().getPort());
                        } catch (Exception ex) {
                            String stacktrace = ExceptionUtils.getStackTrace(ex);
                            CustomPrinter.printErr(stacktrace);
                            listOfNodeExecutionSockets.remove(entry.getValue().getIpAddress() + ":" + entry.getValue().getPort());
                        }
                    }
                }
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                CustomPrinter.printErr(stacktrace);
            }

            if (!messageQueue.isEmpty()) {
                Message message = messageQueue.poll();
                List<String> keys = new ArrayList<>(listOfNodeExecutionSockets.keySet());
                try {
                    SocketWithExecutionNode socket = null;
                    if (this.type) {
                        // static
                        if (currentNode >= listOfNodeExecutionSockets.size() || currentNode < 0) {
                            currentNode = 0;
                        }
                        String keyNode = keys.get(currentNode);
                        currentNode++;
                        currentNode %= listOfNodeExecutionSockets.size();

                        socket = listOfNodeExecutionSockets.get(keyNode);
                        ipUser.put(keyNode, message.getFrom());
                        if (socket != null) {
                            socket.addMessage(message);
                        } else {
                            CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(message.getFrom(), false, "An internal error has occurred! An administrator has been notified! Please try again later.", null, false, 500));
                            EmailSend.send("spammadalinaboaca@gmail.com", "Problem", "There are no available execution nodes, and there are pending execution requests.");
                        }
                    } else {
                        // dynamic
                        Map<String, NodeInfo> map = HeartbeatReceiver.getAllNodes();

                        List<Map.Entry<String, NodeInfo>> nodesByRam = new ArrayList<>(map.entrySet());
                        nodesByRam.sort((a, b) -> Double.compare(b.getValue().getRamFree(), a.getValue().getRamFree()));

                        List<Map.Entry<String, NodeInfo>> nodesByCpu = new ArrayList<>(map.entrySet());
                        nodesByCpu.sort((a, b) -> Double.compare(b.getValue().getProcessors() - b.getValue().getLoadAverage(), a.getValue().getProcessors() - a.getValue().getLoadAverage()));

                        Map<String, Integer> scores = new HashMap<>();

                        for (int i = 0; i < nodesByRam.size(); i++) {
                            String nodeName = nodesByRam.get(i).getKey();
                            scores.put(nodeName, i + 1);
                            CustomPrinter.printNormal("RAM: " + nodeName + " : " + (i + 1));
                        }

                        for (int i = 0; i < nodesByCpu.size(); i++) {
                            String nodeName = nodesByCpu.get(i).getKey();
                            scores.put(nodeName, scores.getOrDefault(nodeName, 0) + (i + 1));
                            CustomPrinter.printNormal("CPU: " + nodeName + " : " + (i + 1));
                        }

                        String bestNode = null;
                        int bestScore = Integer.MAX_VALUE;

                        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                            CustomPrinter.printSuccess("Score for: " + entry.getKey() + " : " + entry.getValue());
                            if (entry.getValue() < bestScore) {
                                bestScore = entry.getValue();
                                bestNode = entry.getKey();
                            }
                        }

                        if (bestNode == null) {
                            CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(message.getFrom(), false, "An internal error has occurred! An administrator has been notified! Please try again later.", null, false, 500));
                            EmailSend.send("spammadalinaboaca@gmail.com", "Problem", "There are no available execution nodes, and there are pending execution requests.");
                            AutoDiscoveryHttp autoDiscoveryHttp = new AutoDiscoveryHttp();
                            try {
                                autoDiscoveryHttp.unRegister();
                            } catch (Exception ignored) {

                            }
                        } else {
                            // se alege acel nod
                            socket = listOfNodeExecutionSockets.get(bestNode);
                            socket.addMessage(message);
                        }
                    }
                } catch (java.lang.IndexOutOfBoundsException | java.lang.NullPointerException e) {
                    CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(message.getFrom(), false, "An internal error has occurred! An administrator has been notified! Please try again later.", null, false, 500));
                    EmailSend.send("spammadalinaboaca@gmail.com", "Problem", "There are no available execution nodes, and there are pending execution requests.");
                    AutoDiscoveryHttp autoDiscoveryHttp = new AutoDiscoveryHttp();
                    try {
                        autoDiscoveryHttp.unRegister();
                    } catch (Exception ignored) {

                    }
                }
            }
        }
    }


    private static void broadCastInSockets(Message message) {
        String keyToRemove = null;
        if (Objects.equals(message.getContent(), "CLOSE")) {
            for (Map.Entry<String, SocketWithExecutionNode> socketEntry : listOfNodeExecutionSockets.entrySet()) {
                if (Objects.equals(socketEntry.getValue().getIdUser(), message.getFrom())) {
                    keyToRemove = socketEntry.getKey();
                    break;
                }
            }
            assert keyToRemove != null;
            listOfNodeExecutionSockets.remove(keyToRemove);
        }
        for (Map.Entry<String, SocketWithExecutionNode> socketEntry : listOfNodeExecutionSockets.entrySet()) {
            if (Objects.equals(socketEntry.getValue().getIdUser(), message.getFrom())) {
                socketEntry.getValue().addMessageRead(message);
                break;
            }
        }
    }


    public static void addMessageInQueue(Message message) {
        // front value
        if (message.isReadRequest() && message.isRequest()) {
            broadCastInSockets(message);
        } else {
            // execution
            messageQueue.add(message);
        }
    }

    public static void interruptExecutionForClient(String clientId) {
        String keyToRemove;
        for (Map.Entry<String, SocketWithExecutionNode> socketEntry : listOfNodeExecutionSockets.entrySet()) {
            if (Objects.equals(socketEntry.getValue().getIdUser(), clientId)) {
                keyToRemove = socketEntry.getKey();
                SocketWithExecutionNode socket = listOfNodeExecutionSockets.get(keyToRemove);
                socket.stopMyThread();
                listOfNodeExecutionSockets.remove(keyToRemove);
                break;
            }
        }
    }

    public static void removeSocket(String socket) {
        CustomPrinter.printInfo("Remove socket: " + socket);
        listOfNodeExecutionSockets.remove(socket);
        HeartbeatReceiver.getHeartbeatReceiver().remove(socket);
        if (ipUser.containsKey(socket)) {
            // TODO remove
            CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(ipUser.get(socket), false, "The server encountered an error! Please try to rerun the code. The problem has been reported to an administrator.", null, false, 500));
            ipUser.remove(socket);
        }
    }
}
