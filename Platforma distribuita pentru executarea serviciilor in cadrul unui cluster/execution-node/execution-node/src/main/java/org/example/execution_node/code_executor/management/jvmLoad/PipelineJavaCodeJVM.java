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

public class PipelineJavaCodeJVM {
    private final CodeExecutorManager codeExecutorManager;
    private final Gson gson = new Gson();

    public PipelineJavaCodeJVM() {
        this.codeExecutorManager = new CodeExecutorManager();
    }

    public void manageSourceCode(MessageToExecutionNode message, Socket socket) {
        FilesSend filesSend = new FilesSend();
        try {
            String address = socket.getInetAddress().getHostAddress();
            int port = socket.getPort();
            String userId = message.getUserId();
            String fileName = message.getFileName();
            String content = message.getContent();

            createDirectoryForUser(address, port, userId);

            writeFileGetPath(address, port, userId, "GetPathFile.java");
            Compile.compile("GetPathFile.java", userId, address, port);

            writeFile(address, port, userId, fileName, content);

            if (Compile.compile(fileName, userId, address, port)) {
                String currentWorkingDirectory = System.getProperty("user.dir");
                String sourceDirectory = "serverJavaResourcesRemote/" + address + "_" + port + "_" + message.getUserId();
                String fullPath = currentWorkingDirectory + File.separator + sourceDirectory;

                if (message.getIsStartUp() == 1) {
                    try {
                        List<FileModel> listBefore = filesSend.getFileNames();

                        this.codeExecutorManager.executeSourceCode(fullPath, message.getFileName().replace(".java", ""));

                        filesSend.compressFileInDirectory("");
                        List<FileModel> listAfter = filesSend.getFileNames();

                        try {
                            filesSend.sendFiles(listBefore, listAfter);
                        } catch (Exception ex) {
                            String stacktrace = ExceptionUtils.getStackTrace(ex);
                            CustomPrinter.printErr(stacktrace);
                        }

                        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                        String threadGroup = parentGroup.getName();
                        CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                        customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
                        CustomPrinter.printSuccess(address + ":" + port + " I have sent the result!");
                    } finally {
                        CustomPrintStreamSocket.removeStream();
                        InputStreamSocket.removeSocket();
                        try {

                            filesSend.deleteDirectory(GetPathFile.getPath());
                            filesSend.deleteDirectory( GetPathFile.getPath().substring(0, GetPathFile.getPath().length() - 1)+ "_" + message.getUserId());
                        } catch (Exception ignored) {

                        }
                    }
                }
            } else {
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
            }
        } catch (StopWithException stopWithException) {
            ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
            String threadGroup = parentGroup.getName();
            CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
            customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
        } catch (SecurityException securityException) {
            throw securityException;
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            CustomPrinter.printErr(stacktrace);
        }
    }

    public void writeFile(String address, int port, String userId, String fileName, String content) throws IOException {
        FileWriter fileWriter = new FileWriter("serverJavaResourcesRemote/" + address + "_" + port + "_" + userId + "/" + fileName);
        if (!fileName.startsWith("GetPathFile")) {
            fileWriter.write(content);
            fileWriter.close();
        }else{
            fileWriter.write("import java.nio.file.*;\n" +
                    "\n" +
                    "public class GetPathFile {\n" +
                    "    public static String getPath() {\n" +
                    "        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();\n" +
                    "        String threadGroup = parentGroup.getName();\n" +
                    "        Path directory = Paths.get( \"serverJavaResourcesRemote/\" + threadGroup);\n" +
                    "        if (!Files.exists(directory)) {\n" +
                    "            try {\n" +
                    "                Files.createDirectory(directory);\n" +
                    "            } catch (Exception e) {\n" +
                    "                e.printStackTrace();\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return \"./\" + directory.toString() + \"/\";\n" +
                    "    }\n" +
                    "}\n");
            fileWriter.close();
        }
    }

    public void writeFileGetPath(String address, int port, String userId, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter("serverJavaResourcesRemote/" + address + "_" + port + "_" + userId + "/" + fileName);
        fileWriter.write("import java.nio.file.*;\n" +
                "\n" +
                "public class GetPathFile {\n" +
                "    public static String getPath() {\n" +
                "        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();\n" +
                "        String threadGroup = parentGroup.getName();\n" +
                "        Path directory = Paths.get( \"serverJavaResourcesRemote/\" + threadGroup);\n" +
                "        if (!Files.exists(directory)) {\n" +
                "            try {\n" +
                "                Files.createDirectory(directory);\n" +
                "            } catch (Exception e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "        }\n" +
                "        return \"./\" + directory.toString() + \"/\";\n" +
                "    }\n" +
                "}\n");
        fileWriter.close();
    }

    public void createDirectoryForUser(String address, int port, String userId) throws IOException {
        String dirName = "serverJavaResourcesRemote/" + address + "_" + port + "_" + userId;
        Path dirPath = Paths.get(dirName);
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        }
    }
}
