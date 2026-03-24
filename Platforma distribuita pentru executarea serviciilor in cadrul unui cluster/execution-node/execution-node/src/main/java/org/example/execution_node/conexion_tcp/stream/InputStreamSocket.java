package org.example.execution_node.conexion_tcp.stream;

import com.google.gson.Gson;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

// read with socket
public class InputStreamSocket extends InputStream {
    private static final ConcurrentHashMap<String,Socket> listOfSockets = new ConcurrentHashMap<>();

    public InputStreamSocket(Socket socket) {
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        listOfSockets.put(threadGroup,socket) ;
    }

    private static int c = 0;

    @Override
    public int read() throws IOException {
        if (c == 0)
        {
            ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
            String threadGroup = parentGroup.getName();
            CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
            customPrintStreamSocket.addDirectMessage(new Gson().toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "READ")) + "\n");
        }
        if (c == 10) {
            c = 0;
            return -1;
        }
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        if(listOfSockets.containsKey(threadGroup)) {
            Socket socket = listOfSockets.get(threadGroup);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            c = dataInputStream.readByte();

            return c;
        }else{
            //throw new ThreadGroupException("Parallel computation is permitted, but the creation of thread groups is not allowed in the Java Virtual Machine injection version.");
        }
        return -1;
    }

    public static void removeSocket(){
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        listOfSockets.remove(threadGroup);
    }
}
