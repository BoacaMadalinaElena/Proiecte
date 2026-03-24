package org.example.execution_node.code_executor.management.jvmLoad;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.code_executor.CodeExecutorManager;
import org.example.execution_node.code_executor.exceptions.StopWithException;
import org.example.execution_node.code_executor.management.files.model.FileModel;
import org.example.execution_node.code_executor.model.ByteCode;
import org.example.execution_node.code_executor.management.files.FilesSend;
import org.example.execution_node.conexion_tcp.model.MessageToExecutionNode;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import org.example.execution_node.conexion_tcp.stream.GetPathFile;
import org.example.execution_node.conexion_tcp.stream.InputStreamSocket;
import org.example.execution_node.other.CustomPrinter;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

public class PipelineClassCodeJVM {
    private final CodeExecutorManager codeExecutorManager;
    private final Gson gson = new Gson();

    public PipelineClassCodeJVM() {
        this.codeExecutorManager = new CodeExecutorManager();
    }

    public void manageByteCodeExecute(MessageToExecutionNode message, Socket socket) {
        if (message.getIsStartUp() == 1) {
            FilesSend filesSend = null;
            try {
                this.changeStreams(socket, message.getUserId());

                ByteCode code = new ByteCode(message.getFileName().replace(".class", ""), message.getContentByte());

                filesSend = new FilesSend();
                List<FileModel> listBefore = filesSend.getFileNames();

                codeExecutorManager.executeByteCode(code);

                filesSend.compressFileInDirectory("");
                List<FileModel> listAfter = filesSend.getFileNames();

                try {
                    filesSend.sendFiles(listBefore, listAfter);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }

                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");


                CustomPrinter.printSuccess(socket.getInetAddress() + ":" + socket.getPort() + " I have sent the result!");

            } catch (StopWithException stopWithException) {
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                CustomPrinter.printErr(stacktrace);
                System.out.println("Internal server error!");
            } finally {
                try {
                    if (filesSend != null) {
                        filesSend.deleteDirectory(GetPathFile.getPath());
                        filesSend.deleteDirectory( GetPathFile.getPath().substring(0, GetPathFile.getPath().length() - 1)+ "_" + message.getUserId());
                    }
                } catch (Exception ex) {
                    String stacktrace = ExceptionUtils.getStackTrace(ex);
                    CustomPrinter.printErr(stacktrace);
                }
            }
        } else {
            ByteCode code = new ByteCode(message.getFileName().replace(".class", ""), message.getContentByte());
            codeExecutorManager.loadByteCode(code);
        }
        CustomPrintStreamSocket.removeStream();
        InputStreamSocket.removeSocket();
    }

    private void changeStreams(Socket socket, String userId) throws IOException {
        PrintStream fileStream = new CustomPrintStreamSocket(socket.getOutputStream(), userId, true);
        System.setIn(new InputStreamSocket(socket));
        System.setOut(fileStream);
        System.setErr(fileStream);
    }
}
