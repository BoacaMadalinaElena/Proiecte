package org.example.tcp_management.socket;

import com.google.gson.Gson;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.other.CustomPrinter;
import org.example.tcp_management.TCPManagement;
import org.example.tcp_management.model.MessageToFront;
import org.example.tcp_management.model.MessageToExecutionNode;
import org.example.tcp_management.model.MessageToMaster;
import org.example.web_socket_management.CommunicationTCPEndpoint;
import org.example.web_socket_management.dto.CodeMessage;
import org.example.web_socket_management.dto.Message;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketWithExecutionNode extends Thread {
    private final ConcurrentLinkedQueue<Message> messageQueue;
    private final ConcurrentLinkedQueue<Message> messageReadValue;
    private Socket socket;
    private DataOutputStream dataOutputSteam;
    private BufferedReader bufferedReader;
    @Getter
    private String idUser;
    private final Gson gson;
    private final String address;
    private final int port;
    private boolean running;

    public SocketWithExecutionNode(String address, int port) {
        this.address = address;
        this.port = port;
        this.messageQueue = new ConcurrentLinkedQueue<>();
        try {
            this.socket = new Socket(address, port);
            this.dataOutputSteam = new DataOutputStream(socket.getOutputStream());
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ioException) {
            String stacktrace = ExceptionUtils.getStackTrace(ioException);
            CustomPrinter.printErr(stacktrace);
        }
        this.messageReadValue = new ConcurrentLinkedQueue<>();
        this.gson = new Gson();


        this.running = true;
    }

    public void stopMyThread() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            if (!this.messageQueue.isEmpty()) {
                CustomPrinter.printInfo("Message in queue: " + this.messageQueue.size());
                // un thread trimite doar un cod pe conexiune
                Runnable myTask = () -> {
                    Message message = this.messageQueue.peek();
                    try {
                        if (message != null && message.getListOfCodes() != null) {
                            CustomPrinter.printInfo(message.getListOfCodes().size() + "");
                            this.messageQueue.remove(message);
                            AtomicBoolean okInExecuteCodThread = new AtomicBoolean(false);
                            this.idUser = message.getFrom();
                            List<CodeMessage> codeMessageList = message.getListOfCodes();
                            codeMessageList.sort((o1, o2) -> {
                                int compareByIsCodeClass = o1.getIsCodeClass().compareTo(o2.getIsCodeClass());
                                if (compareByIsCodeClass != 0) {
                                    return compareByIsCodeClass;
                                } else {
                                    return o1.getIsStartUp().compareTo(o2.getIsStartUp());
                                }
                            });
                            for (CodeMessage codeMessage : codeMessageList) {
                                CustomPrinter.printWarning(codeMessage.getFileName() + " " + codeMessage.getContentBytes());
                                MessageToExecutionNode messageToSend = new MessageToExecutionNode(message.getFrom(), codeMessage.getFileName(), codeMessage.getContentString(), Objects.equals(codeMessage.getIsStartUp(), "true") ? 1 : 0, Objects.equals(codeMessage.getIsCodeClass(), "true") ? 1 : 0, codeMessage.getContentBytes(),codeMessage.getType());

                                String msg = this.gson.toJson(messageToSend);
                                int chunkSize = 32768;
                                int numMessages = (int) Math.ceil((double) msg.length() / chunkSize);

                                // nr cadre
                                this.dataOutputSteam.writeInt(numMessages);
                                this.dataOutputSteam.flush();

                                for (int i = 0; i < msg.length(); i += chunkSize) {
                                    int endIndex = Math.min(i + chunkSize, msg.length());
                                    String chunk = msg.substring(i, endIndex);
                                    this.dataOutputSteam.writeUTF(chunk);
                                }
                                this.dataOutputSteam.flush();
                                CustomPrinter.printWarning("Request send ok...");
                                if (Objects.equals(codeMessage.getIsStartUp(), "true")) {
                                    Thread readerThread = getThreadClass(okInExecuteCodThread, message);
                                    readerThread.join();
                                }
                            }
                        }
                    } catch (InterruptedException | IOException | NullPointerException ex) {
                        String stacktrace = ExceptionUtils.getStackTrace(ex);
                        CustomPrinter.printErr(stacktrace);
                        TCPManagement.removeSocket(this.address + ":" + this.port);
                        TCPManagement.addMessageInQueue(message);
                    }
                };
                Thread th = new Thread(myTask);
                th.start();
                try {
                    th.join();
                    CustomPrinter.printInfo("Finish run code!");
                } catch (InterruptedException e) {
                    String stacktrace = ExceptionUtils.getStackTrace(e);
                    CustomPrinter.printErr(stacktrace);
                }
            }
        }
        CustomPrinter.printErr("Thread run code for: " + this.idUser + " is down!");
    }

    private Thread getThreadClass(AtomicBoolean okInExecuteCodThread, Message message) {
        CustomPrinter.printSuccess("New message processing...");
        Thread readerThread = null;
        Thread finalReaderThread = readerThread;
        readerThread = new Thread(() -> {
            MessageToMaster messageToMaster = null;

            if (message.getType() == 1 || message.getType() == 2) {
                // read from front
                Thread threadFromFront = new Thread(() -> {
                    while (this.running) {
                        if (!messageReadValue.isEmpty()) {
                            try {
                                Message message1 = this.messageReadValue.peek();
                                if (message1 != null && Objects.equals(message1.getFrom(), message.getFrom()) && message1.isRequest() && message1.isReadRequest()) {
                                    messageReadValue.remove();
                                    CustomPrinter.printWarning(message1.getContent());
                                    this.dataOutputSteam.writeUTF(message1.getContent());
                                    this.dataOutputSteam.flush();
                                }
                            } catch (Exception ex) {
                                String stacktrace = ExceptionUtils.getStackTrace(ex);
                                CustomPrinter.printErr(stacktrace);
                            }
                        }
                    }
                });
                threadFromFront.start();
            }
            try {
                String line;
                label:
                while (this.running && (line = bufferedReader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        CustomPrinter.printSuccess(line);
                        messageToMaster = this.gson.fromJson(line, MessageToMaster.class);
                        CustomPrinter.printInfo(messageToMaster.getUserId() + " " + messageToMaster.getMessage());
                        String type = messageToMaster.getCommand();
                        if(type == null)
                            type = "";
                        else
                            type = type.replace("\n", "");
                        switch (type) {
                            case "STOP":
                                CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(messageToMaster.getUserId(), false, "Process finish!"));

                                okInExecuteCodThread.set(true);
                                this.idUser = null;
                                if(message.getType()  == 1 ||message.getType()  == 2) {
                                    this.dataOutputSteam.writeUTF("STOP");
                                    this.dataOutputSteam.flush();
                                }
                                break label;
                            case "START_EXCEPTION":
                                CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(messageToMaster.getUserId(), false, "START_EXCEPTION"));
                                break;
                            case "STOP_EXCEPTION":
                                CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(messageToMaster.getUserId(), false, "STOP_EXCEPTION"));
                                break;
                            case "NEW_FILE":
                                String fileName = bufferedReader.readLine();
                                while (fileName.isEmpty()) {
                                    fileName = bufferedReader.readLine();
                                }
                                CustomPrinter.printWarning("filename: " + fileName);
                                MessageToMaster fileNameMsg = this.gson.fromJson(fileName, MessageToMaster.class);
                                String contentFile = bufferedReader.readLine();
                                while (contentFile.isEmpty())
                                    contentFile = bufferedReader.readLine();
                                CustomPrinter.printWarning(contentFile.length() + "");
                                MessageToMaster contentFileMsg = this.gson.fromJson(contentFile, MessageToMaster.class);

                                byte[] bytes = contentFileMsg.getBytes();

                                CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(messageToMaster.getUserId(), false, fileNameMsg.getCommand(), bytes, true, 200));
                                okInExecuteCodThread.set(true);
                                break;
                            case "READ":
                                if(message.getType()  == 0) {
                                    CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(messageToMaster.getUserId(), true, messageToMaster.getMessage()));
                                    boolean stop = false;
                                    while (this.running && !stop) {
                                        if (!messageReadValue.isEmpty()) {
                                            Message message1 = this.messageReadValue.peek();
                                            if (message1 != null && Objects.equals(message1.getFrom(), message.getFrom()) && message1.isRequest() && message1.isReadRequest()) {
                                                messageReadValue.remove(message1);
                                                this.dataOutputSteam.writeBytes(message1.getContent());
                                                this.dataOutputSteam.writeByte(10);
                                                this.dataOutputSteam.flush();
                                                stop = true;
                                            }
                                        }
                                    }
                                    break;
                                }
                            default:
                                CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(messageToMaster.getUserId(), false, messageToMaster.getMessage()));
                                break;
                        }
                    }
                }
                if (finalReaderThread != null)
                    finalReaderThread.stop();
            } catch (IOException e) {
                CustomPrinter.printErr("Node: " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort() + " is down!");
                TCPManagement.removeSocket(this.address + ":" + this.port);
                if (messageToMaster != null)
                    CommunicationTCPEndpoint.addMessageToSendFront(new MessageToFront(messageToMaster.getUserId(), false, "The server encountered an error! Please try to rerun the code. The problem has been reported to an administrator.", null, false, 500));
            }
        });

        readerThread.start();
        return readerThread;
    }

    public void addMessage(Message message) {
        //CustomPrinter.printSuccess("New message in socket with execution node" + message);
        CustomPrinter.printInfo("Send to: " + this.socket.getInetAddress().getHostName() + ":" + this.socket.getPort());
        this.messageQueue.add(message);
    }

    public void addMessageRead(Message message) {
        //CustomPrinter.printSuccess("New message in socket with execution node" + message);
        if (this.idUser != null && Objects.equals(this.idUser, message.getFrom())) {
            this.messageReadValue.add(message);
        }
    }
}
