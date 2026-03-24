package org.example.execution_node.code_executor.implementations;

import com.google.gson.Gson;
import org.example.execution_node.code_executor.exceptions.StopWithException;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;

public class CodeExecutorJar {
    private final Gson gson = new Gson();


    public void executeCode(String filePath,String runClassName,String  address,int port,String userId) throws StopWithException {
        try{
            String dirName = "serverJavaResourcesRemote/" + address + "_" + port + "_" + userId;
            File replacementFile = new File(dirName + "/GetPathFile.class");
            URL jarUrl = new File(filePath).toURI().toURL();
            CustomClassLoaderJar classLoader = new CustomClassLoaderJar(new URL[]{jarUrl}, this.getClass().getClassLoader(), "GetPathFile.class", replacementFile,address,port,userId);
            Class<?> loadedClass = Class.forName(runClassName, true, classLoader);
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
