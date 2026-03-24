package org.example.execution_node.code_executor.management.jvmLoad;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import org.example.execution_node.other.CustomPrinter;
import java.io.*;

public class Compile {
    private static final Gson gson = new Gson();
    public static boolean compile(String fileName, String userId,String ip,int port) {
        boolean returnVal = true;
        try {
            String currentWorkingDirectory = System.getProperty("user.dir");
            Runtime runtime = Runtime.getRuntime();

            String sourceDirectory = "serverJavaResourcesRemote/" + ip  + "_" + port+ "_" + userId;
            String fullPath = currentWorkingDirectory + File.separator + sourceDirectory;

            File sourceDir = new File(fullPath);

            Process process = runtime.exec("javac " + fileName, null, sourceDir);

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine;
            boolean ok = false;

            StringBuilder result = new StringBuilder();
            int number = 0;
            while ((errorLine = errorReader.readLine()) != null) {
                if (number == 0) {
                    ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                    String threadGroup = parentGroup.getName();
                    CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                    customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
                    number++;
                }
                System.err.println(errorLine);
                result.append(errorLine);
                returnVal = false;
                if (errorLine.contains("cannot find symbol")) {
                    ok = true;
                }
            }
            if (number != 0) {
                ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                String threadGroup = parentGroup.getName();
                CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");
            }
            if (ok) {
                CustomPrinter.printNormal(result.toString());
            }

            int exitValue = process.waitFor();
            CustomPrinter.printInfo("Compile: Process exit value: " + exitValue);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            System.err.println(stacktrace);
        }
        return returnVal;
    }
}
