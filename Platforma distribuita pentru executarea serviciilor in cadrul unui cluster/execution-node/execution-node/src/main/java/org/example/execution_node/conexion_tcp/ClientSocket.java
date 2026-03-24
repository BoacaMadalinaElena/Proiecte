package org.example.execution_node.conexion_tcp;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.code_executor.management.docker.PipelineDockerRun;
import org.example.execution_node.code_executor.management.externalDirectory.PipelineExternalDirectory;
import org.example.execution_node.code_executor.management.jvmLoad.PipelineClassCodeJVM;
import org.example.execution_node.code_executor.management.jvmLoad.PipelineJarCodeJVM;
import org.example.execution_node.code_executor.management.jvmLoad.PipelineJavaCodeJVM;
import org.example.execution_node.conexion_tcp.model.MessageToExecutionNode;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import org.example.execution_node.conexion_tcp.stream.GetPathFile;
import org.example.execution_node.conexion_tcp.stream.InputStreamSocket;
import org.example.execution_node.other.CustomPrintStreamError;
import org.example.execution_node.other.CustomPrinter;
import org.example.execution_node.security.CustomSecurityManager;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import java.util.HashSet;
import java.util.concurrent.*;
import java.io.*;
import java.net.Socket;

public class ClientSocket implements Runnable {
    private final Socket socket;
    private DataInputStream dataInputStream;
    private final Gson gson;
    private final PipelineClassCodeJVM pipelineClassCodeJVM;
    private final PipelineJavaCodeJVM pipelineJavaCodeJVM;
    private final PipelineJarCodeJVM pipelineJarCodeJVM;

    public ClientSocket(Socket socket) {
        this.socket = socket;
        this.gson = new Gson();
        pipelineClassCodeJVM = new PipelineClassCodeJVM();
        pipelineJavaCodeJVM = new PipelineJavaCodeJVM();
        pipelineJarCodeJVM = new PipelineJarCodeJVM();
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        try {
            while (true) {
                MessageToExecutionNode message = this.readMessage();
                CustomPrinter.printNormal("Message from: " + message.getUserId() + " with file: " + message.getFileName());

                long startTime = System.nanoTime();

                CustomSecurityManager.addThreadGroup(message.getMethodExecution(), threadGroup,message.getUserId());
                CustomSecurityManager.addThreadGroup(threadGroup);
                CustomPrinter.addUser(threadGroup, message.getUserId());
                this.redirectStream(message.getUserId());

                switch (message.getMethodExecution()) {
                    case 0:
                        runInJVM(message);
                        break;
                    case 1:
                        runInExternalDirectory(message);
                        break;
                    case 2:
                        runInDocker(message);
                        break;
                    default:
                        CustomPrinter.printErr("Invalid method!");
                        System.out.println("Invalid method!");
                }

                long endTime = System.nanoTime();
                long duration = endTime - startTime;
            //    FileWriter fileWriter = new FileWriter("outTime.txt",true);
             //   fileWriter.write(duration + "\n");
              //  fileWriter.close();

            }
        } catch (Exception ex) {
            CustomPrinter.printErr(ex.getMessage());
        }
    }

    public void runInDocker(MessageToExecutionNode message) {
       PipelineDockerRun pipelineDockerRun = new PipelineDockerRun() ;
        try {
            Future<?> future = null;
            try {
                Runnable myRunnable = () -> {
                    if (message.getFileName().endsWith(".class") || message.getFileName().endsWith(".java") || message.getFileName().endsWith(".jar") || message.getFileName().endsWith(".py")) {
                        pipelineDockerRun.runInDocker(message, this.dataInputStream);
                    } else {
                        this.writeOtherFile(message);
                    }
                };

                ExecutorService executor = Executors.newSingleThreadExecutor();
                future = executor.submit(myRunnable);

                future.get(5 * 60 * 1000, TimeUnit.MILLISECONDS);
                CustomPrinter.printSuccess("Completed successfully for: " + message.getUserId());
            } catch (TimeoutException timeoutException) {
                PrintStream fileStream = new CustomPrintStreamSocket(this.socket.getOutputStream(), message.getUserId(), true);
                System.setOut(fileStream);
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
                System.out.println("The execution has been interrupted because it exceeded the server's runtime limit set at 20 minutes.");
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");

                try {
                    future.cancel(true);
                    pipelineDockerRun.clear(message.getUserId());

                } catch (Exception ignored) {
                }
            } catch (InterruptedException ignored) {
            }
        } catch (Exception ioException) {
            try {
                this.socket.close();
            } catch (IOException ioException1) {
                ioException1.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            }
            ioException.printStackTrace();
            CustomPrinter.printErr("The user with: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " disconnected!");
        }
    }

    public void runInExternalDirectory(MessageToExecutionNode message) {
        try {
            Future<?> future = null;
            try {
                Runnable myRunnable = () -> {
                    if (message.getFileName().endsWith(".class") || message.getFileName().endsWith(".java") || message.getFileName().endsWith(".jar")) {
                        new PipelineExternalDirectory().runInExternalDirectory(message, this.dataInputStream);
                    } else {
                        this.writeOtherFile(message);
                    }
                };

                ExecutorService executor = Executors.newSingleThreadExecutor();
                future = executor.submit(myRunnable);

                future.get(20 * 60 * 1000, TimeUnit.MILLISECONDS);
                CustomPrinter.printSuccess("Completed successfully for: " + message.getUserId());

            } catch (TimeoutException timeoutException) {
                PrintStream fileStream = new CustomPrintStreamSocket(this.socket.getOutputStream(), message.getUserId(), true);
                System.setOut(fileStream);
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
                System.out.println("The execution has been interrupted because it exceeded the server's runtime limit set at 20 minutes.");
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");

                try {
                    future.cancel(true);
                } catch (Exception ignored) {
                }
            } catch (InterruptedException ignored) {
            }
        } catch (Exception ioException) {
            ioException.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            try {
                this.socket.close();
            } catch (IOException ioException1) {
                ioException1.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            }
            CustomPrinter.printErr("The user with: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " disconnected!");
        }
    }

    public void runInJVM(MessageToExecutionNode message) {
        try {
            Future<?> future = null;

            try {
                Runnable myRunnable = () -> {
                    if (message.getFileName().endsWith(".class")) {
                        pipelineClassCodeJVM.manageByteCodeExecute(message, this.socket);
                    } else if (message.getFileName().endsWith(".java")) {
                        pipelineJavaCodeJVM.manageSourceCode(message, this.socket);
                    } else if (message.getFileName().endsWith(".jar")) {
                        pipelineJarCodeJVM.manageJarCode(message, this.socket);
                    } else {
                        this.writeOtherFile(message);
                    }
                };

                ExecutorService executor = Executors.newSingleThreadExecutor();
                future = executor.submit(myRunnable);
                future.get(20 * 60 * 1000, TimeUnit.MILLISECONDS);
            } catch (ExecutionException | SecurityException securityException) {
                PrintStream fileStream = new CustomPrintStreamSocket(this.socket.getOutputStream(), message.getUserId(), true);
                System.setOut(fileStream);
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
                System.out.println(securityException.getMessage());
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
            } catch (TimeoutException timeoutException) {
                PrintStream fileStream = new CustomPrintStreamSocket(this.socket.getOutputStream(), message.getUserId(), true);
                System.setOut(fileStream);
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
                System.out.println("The execution has been interrupted because it exceeded the server's runtime limit set at 20 minutes.");
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");

                try {
                    future.cancel(true);
                } catch (Exception ignored) {

                }
            } catch (InterruptedException ignored) {
            }
        } catch (Exception ioException) {
            ioException.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            CustomPrinter.printErr(ioException.getMessage());
            try {
                this.socket.close();
            } catch (IOException ioException1) {
                ioException1.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            }
            CustomPrinter.printErr("The user with: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " disconnected!");
        }
    }

    public MessageToExecutionNode readMessage() throws IOException {
        CustomPrinter.printSuccess("Wait request....");
        int numberOfFrames = this.dataInputStream.readInt();
        StringBuilder messageString = new StringBuilder();
        for (int i = 0; i < numberOfFrames; i++) {
            messageString.append(dataInputStream.readUTF());
        }
        return this.gson.fromJson(messageString.toString(), MessageToExecutionNode.class);
    }

    public void redirectStream(String userId) {
        try {
            PrintStream fileStream = new CustomPrintStreamSocket(this.socket.getOutputStream(), userId, true);
            System.setIn(new InputStreamSocket(this.socket));
            System.setOut(fileStream);
            System.setErr(fileStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeOtherFile(MessageToExecutionNode message) {
        try {
            // write other file
            CustomPrinter.printFile(message.getFileName());
            // png, jpg, txt, mp3, mp4, pdf, doc, docx , jpeg
               String path = GetPathFile.getPath();
            FileOutputStream fos = new FileOutputStream(path + message.getFileName());
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(message.getContentByte());
            bos.close();

            Detector detector = new AutoDetectParser().getDetector();
            FileInputStream input = new FileInputStream(path + message.getFileName());
            BufferedInputStream bufferedInput = new BufferedInputStream(input);
            Metadata metadata = new Metadata();
            MediaType mediaType = detector.detect(bufferedInput, metadata);
            CustomPrinter.printFile("Tipul de continut al fisierului: " + mediaType.toString());
            // _ png image/*
            // _ txt text/plain
            // _ mp3 application/x-matroska
            // _ mp4 video/*
            // _ pdf application/pdf
            // odt  application/vnd.oasis.opendocument.text
            // docx application/zip
            HashSet<String> arrayFormat = new HashSet<>();
            arrayFormat.add("application/x-matroska");
            arrayFormat.add("text/plain");
            arrayFormat.add("application/pdf");
            arrayFormat.add("application/vnd.oasis.opendocument.text");
            arrayFormat.add("application/zip");
            arrayFormat.add("application/x-tika-msoffice");
            arrayFormat.add("audio/");
            arrayFormat.add("application/octet-stream");
            if(!(mediaType.toString().startsWith("image/") || arrayFormat.contains(mediaType.toString()) || mediaType.toString().startsWith("video/")|| mediaType.toString().startsWith("audio/"))){
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");
                System.out.println("In version 1 of execution, only images and .txt, .pdf, mp3, mp4, .odt, .doc, docx files are accepted.");
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
                throw new SecurityException("Only image, video, .txt, .pdf, .mp3, .odt,.doc, .docx files are accepted for execution.");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
        }
    }
}