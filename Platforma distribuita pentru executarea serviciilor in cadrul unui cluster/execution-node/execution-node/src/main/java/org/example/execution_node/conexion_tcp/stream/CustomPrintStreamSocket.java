package org.example.execution_node.conexion_tcp.stream;

import com.google.gson.Gson;
import lombok.Getter;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.other.CustomPrinter;
import org.example.execution_node.conexion_tcp.stream.exceptions.ThreadGroupException;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;

public class CustomPrintStreamSocket extends PrintStream {
    private final Gson gson;
    @Getter
    private final String prefix;
    @Getter
    private static final ConcurrentHashMap<String, CustomPrintStreamSocket> listOfNodes = new ConcurrentHashMap<>();

    public CustomPrintStreamSocket(OutputStream out, String prefix,boolean flush) {
        super(out,flush);
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        listOfNodes.put(threadGroup,this);
        this.prefix = prefix;
        this.gson = new Gson();
    }

    @Override
    public void print(String x){
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        if(listOfNodes.containsKey(threadGroup) && !x.contains("org.example.execution_node") ) {
            CustomPrintStreamSocket customPrintStreamSocket = listOfNodes.get(threadGroup);
            customPrintStreamSocket.printSuper(this.gson.toJson(new MessageToMaster(prefix, x, null,null)));
        }else{
            // throw new ThreadGroupException("Parallel computation is permitted, but the creation of thread groups is not allowed in the Java Virtual Machine injection version.");
        }
    }

    @Override
    public void print(int x){
        print(x + "");
    }

    @Override
    public void print(boolean x){
        print(x + "");
    }

    @Override
    public void print(long x){
        print(x + "");
    }

    public void printSuper(String s){
            super.print(s);
    }

    public void addDirectMessage(String s){
        this.printSuper(s);
    }

    /*public void addCommand(String s){
        CustomPrinter.printNormal( "Send command: " + s);
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        if(listOfNodes.containsKey(threadGroup)) {
            CustomPrintStreamSocket customPrintStreamSocket = listOfNodes.get(threadGroup);
            customPrintStreamSocket.printSuper(this.gson.toJson(new MessageToMaster(prefix, null, null,s)));
        }else{
            //throw new ThreadGroupException("Parallel computation is permitted, but the creation of thread groups is not allowed in the Java Virtual Machine injection version.");
        }
    }*/

    public static void removeStream(){
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        listOfNodes.remove(threadGroup);
    }
}
