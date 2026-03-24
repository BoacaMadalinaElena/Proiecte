package org.example.execution_node.code_executor.management.externalDirectory;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.code_executor.management.files.FilesSend;
import org.example.execution_node.code_executor.management.files.model.FileModel;
import org.example.execution_node.conexion_tcp.model.MessageToExecutionNode;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import org.example.execution_node.conexion_tcp.stream.GetPathFile;
import org.example.execution_node.other.CustomPrinter;
import java.io.*;
import java.util.List;


public class PipelineExternalDirectory {
    private final Gson gson = new Gson();

    public void runInExternalDirectory(MessageToExecutionNode message, DataInputStream dataInputStream) {
        // write + compile
        try {
            if(message.getFileName().endsWith(".java")) {
                this.writeFiles(message.getFileName(),message.getContent());
                if(this.compile(message) != 0){
                    ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                    String threadGroup = parentGroup.getName();
                    CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                    customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
                    return;
                }
            }else{
                this.writeFiles(message.getFileName(),message.getContentByte());
            }
        } catch (IOException | InterruptedException ioException) {
            String stacktrace = ExceptionUtils.getStackTrace(ioException);
            CustomPrinter.printErr(stacktrace);
            System.out.println("Internal server error");
            return;
        }

        // run code
        if (message.getIsStartUp() == 1) {
            try {
                FilesSend filesSend = new FilesSend();

                List<FileModel> listBefore = filesSend.getFileNames();
                Process process;
                if(message.getFileName().endsWith(".java") || message.getFileName().endsWith(".class")) {
                    process = Runtime.getRuntime().exec("java " + message.getFileName().replace(".java", "").replace(".class", ""), null, new File(GetPathFile.getPath()));
                }else{
                    process = Runtime.getRuntime().exec("java "  + " -Xmx1m -Xss1k -XX:MaxMetaspaceSize=1m " + " -jar " + message.getFileName(), null, new File(GetPathFile.getPath()));
                }

                // read normal stream
                Thread outputThread = this.getThreadReadNormalOutputStream(process.getInputStream());
                // read error stream
                Thread outputErrorThread = this.getThreadErrorOutputStream(process.getErrorStream());

                // input stream
                Process finalProcess = process;
                Thread inputThread = new Thread(() -> {
                    try {
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(finalProcess.getOutputStream()));
                        String line;

                        boolean ok = true;
                        while (finalProcess.isAlive() && ok) {
                            line = dataInputStream.readUTF();
                            writer.write(line);
                            writer.newLine();
                            writer.flush();
                            if (line.contains("STOP"))
                                ok = false;
                        }
                    } catch (Exception exception) {
                        //exception.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
                        //System.out.println("Internal server error");
                    }
                });

                outputThread.start();
                outputErrorThread.start();
                inputThread.start();

                int exitCode = process.waitFor();

                // stop
                outputThread.join();
                outputErrorThread.join();
                inputThread.interrupt();


                filesSend.compressFileInDirectory("");
                List<FileModel> listAfter = filesSend.getFileNames();
                CustomPrinter.printInfo("Process exited with code " + exitCode);
                filesSend.sendFiles(listBefore,listAfter);

                // out
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");

                filesSend.deleteDirectory(GetPathFile.getPath());
                filesSend.deleteDirectory( GetPathFile.getPath().substring(0, GetPathFile.getPath().length() - 1)+ "_" + message.getUserId());   } catch (IOException | InterruptedException e) {
                String stacktrace = ExceptionUtils.getStackTrace(e);
                CustomPrinter.printErr(stacktrace);
                System.out.println("Internal server error");
            }
        }
    }

    private void writeFiles(String fileName,String content) throws IOException {
            FileWriter fos = new FileWriter(GetPathFile.getPath() + fileName);
            fos.write(content);
            fos.close();
    }

    private void writeFiles(String fileName,byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(GetPathFile.getPath() + "/" + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(content);
        bos.close();
    }

    public int compile(MessageToExecutionNode message) throws IOException, InterruptedException {
        // compile
        Runtime runtime = Runtime.getRuntime();
        String currentWorkingDirectory = System.getProperty("user.dir");
        String fullPath = currentWorkingDirectory + File.separator + GetPathFile.getPath();
        Process process = runtime.exec("javac " + message.getFileName(), null, new File(fullPath));

        // compile error
        BufferedReader is =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = is.readLine()) != null)
            System.out.println(line);

        return process.waitFor();
    }

    public Thread getThreadReadNormalOutputStream(InputStream inputStream){
        return new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                CustomPrinter.printErr(stacktrace);
                System.out.println("Internal server error");
            }
        });
    }

    public Thread getThreadErrorOutputStream(InputStream inputStream){
        return new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                int number = 0;
                while ((line = reader.readLine()) != null) {
                    if (number == 0) {
                        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                        String threadGroup = parentGroup.getName();
                        CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                        customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
                    }
                    number++;
                    System.out.println(line);
                }
                if (number > 0) {
                    ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                    String threadGroup = parentGroup.getName();
                    CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                    customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");
                }
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                CustomPrinter.printErr(stacktrace);
            }
        });
    }
}
