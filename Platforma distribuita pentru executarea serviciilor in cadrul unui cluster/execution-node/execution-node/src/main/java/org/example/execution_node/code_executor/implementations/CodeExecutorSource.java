package org.example.execution_node.code_executor.implementations;

import com.google.gson.Gson;
import org.example.execution_node.code_executor.exceptions.StopWithException;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

public class CodeExecutorSource  {
    private final Gson gson = new Gson();
    public void executeCode(String filePath,String fileName) throws StopWithException {
        try{
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(filePath).toURI().toURL()});
            Class<?> loadedClass = classLoader.loadClass(fileName);

            Constructor<?> constructor = loadedClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();

            loadedClass.getMethod("run").invoke(instance);

            classLoader.close();
        } catch (Throwable exception){
            ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
            String threadGroup = parentGroup.getName();
            CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
            customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
            exception.printStackTrace(System.err);
            customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");
            throw  new StopWithException();
        }
    }
}
