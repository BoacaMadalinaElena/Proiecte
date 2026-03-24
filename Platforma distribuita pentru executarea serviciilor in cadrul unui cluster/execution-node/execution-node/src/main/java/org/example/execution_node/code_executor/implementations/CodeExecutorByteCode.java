package org.example.execution_node.code_executor.implementations;

import com.google.gson.Gson;
import org.example.execution_node.code_executor.exceptions.StopWithException;
import org.example.execution_node.code_executor.model.ByteCode;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import org.example.execution_node.other.CustomPrinter;
import java.lang.reflect.Constructor;
import java.util.HashMap;

public class CodeExecutorByteCode extends ClassLoader {
    private ByteCode code;
    private final Gson gson = new Gson();
    private final HashMap<String, Class<?>> listOfClass;

    public CodeExecutorByteCode() {
        listOfClass = new HashMap<>();
    }

    @Override
    public Class<?> findClass(String name) {
        byte[] classBytes = code.getContent();
        try {
            Class<?> loadedClass = this.listOfClass.get(name);
            if (loadedClass != null) {
                return loadedClass;
            } else {
                CustomPrinter.printInfo("Defined new class: " + name);
                return defineClass(name, classBytes, 0, classBytes.length);
            }
        } catch (Throwable exception) {
            ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
            String threadGroup = parentGroup.getName();
            CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
            customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
            exception.printStackTrace(System.err);
            customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");

        }
        return null;
    }

    public void execute(ByteCode code) throws StopWithException {
        try {
            CustomPrinter.printNormal("Run class : " + code.getFileName());
            this.code = code;
            Class<?> myClass;
            if (!listOfClass.containsKey(code.getFileName())) {
                myClass = this.findClass(code.getFileName());
                this.listOfClass.put(code.getFileName(), myClass);
            } else {
                myClass = this.listOfClass.get(code.getFileName());
            }
            Constructor<?> constructor = myClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            myClass.getMethod("run").invoke(instance);
        } catch (Throwable exception) {
            ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
            String threadGroup = parentGroup.getName();
            CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
            customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
            exception.printStackTrace(System.err);
            customPrintStreamSocket.addDirectMessage(gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");

            throw new StopWithException();
        }
    }

    public void loadByteCode(ByteCode code) {
        this.code = code;
        if (!listOfClass.containsKey(code.getFileName())) {
            Class<?> myClass = this.findClass(code.getFileName());
            this.listOfClass.put(code.getFileName(), myClass);
        }
    }
}
