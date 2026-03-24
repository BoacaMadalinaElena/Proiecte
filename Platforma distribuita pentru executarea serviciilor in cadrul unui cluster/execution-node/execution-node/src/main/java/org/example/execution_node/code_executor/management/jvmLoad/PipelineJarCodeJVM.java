package org.example.execution_node.code_executor.management.jvmLoad;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.code_executor.CodeExecutorManager;
import org.example.execution_node.code_executor.exceptions.StopWithException;
import org.example.execution_node.code_executor.management.files.FilesSend;
import org.example.execution_node.code_executor.management.files.model.FileModel;
import org.example.execution_node.conexion_tcp.model.MessageToExecutionNode;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import org.example.execution_node.conexion_tcp.stream.GetPathFile;
import org.example.execution_node.conexion_tcp.stream.InputStreamSocket;
import org.example.execution_node.other.CustomPrinter;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PipelineJarCodeJVM {
    private final CodeExecutorManager codeExecutorManager;
    private final Gson gson = new Gson();

    public PipelineJarCodeJVM(){
        this.codeExecutorManager = new CodeExecutorManager();
    }

    public void manageJarCode(MessageToExecutionNode message, Socket socket) {
        FilesSend filesSend = new FilesSend();
        try {
            String address = socket.getInetAddress().getHostAddress();
            int port = socket.getPort();
            String userId = message.getUserId();

            this.createDirectoryForUser(address,port,userId);

            this.writeFile(message,address,port,userId);

            List<FileModel> listBefore = filesSend.getFileNames();

            String start = "";
            try {
                start = this.getRunClassFromJar( "serverJavaResourcesRemote/" + address + "_" + port + "_" + userId + "/" + message.getFileName());
            } catch (Exception ex) {
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
                ex.printStackTrace(System.err);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");
            }


            codeExecutorManager.executeJarCode( "serverJavaResourcesRemote/" + address + "_" + port + "_" + userId + "/" + message.getFileName(), start,address,port,userId);

            filesSend.compressFileInDirectory("");
            List<FileModel> listAfter = filesSend.getFileNames();

            try {
                filesSend.sendFiles(listBefore, listAfter);
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                CustomPrinter.printErr(stacktrace);

                System.out.println("Internal server error!");
            }

            ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
            String threadGroup = parentGroup.getName();
            CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
            customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");

            CustomPrinter.printSuccess(socket.getInetAddress() + ":" + socket.getPort() + " I have sent the result!");
        } catch (StopWithException stopWithException) {
            CustomPrinter.printNormal("Final executeSourceCode");
            ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
            String threadGroup = parentGroup.getName();
            CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
            customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
        } catch (Exception ex) {
            String stacktrace = ExceptionUtils.getStackTrace(ex);
            CustomPrinter.printErr(stacktrace);
        } finally {
            CustomPrintStreamSocket.removeStream();
            InputStreamSocket.removeSocket();
            try {
                filesSend.deleteDirectory(GetPathFile.getPath());
                filesSend.deleteDirectory( GetPathFile.getPath().substring(0, GetPathFile.getPath().length() - 1)+ "_" + message.getUserId());
            }catch (Exception ignored){

            }
        }
    }

    private String getRunClassFromJar(String jarPath) throws IOException {
        JarFile jarFile = new JarFile(jarPath);

        Manifest manifest = jarFile.getManifest();
        Attributes attributes = manifest.getMainAttributes();
        String mainClassName = attributes.getValue("Main-Class");

        jarFile.close();

        return mainClassName;
    }

    public void createDirectoryForUser(String  address,int port,String userId) throws IOException {
        String dirName = "serverJavaResourcesRemote/" + address + "_" + port + "_" + userId;
        Path dirPath = Paths.get(dirName);
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        }
    }

    public void writeFile(MessageToExecutionNode message, String address, int port, String userId) throws IOException {
        String dirName = "serverJavaResourcesRemote/" + address + "_" + port + "_" + userId;
        FileOutputStream fos = new FileOutputStream(dirName + "/" + message.getFileName());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(message.getContentByte());
        bos.close();
      }

}
