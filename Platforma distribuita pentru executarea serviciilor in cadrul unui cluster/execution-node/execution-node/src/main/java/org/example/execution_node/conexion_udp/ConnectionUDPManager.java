package org.example.execution_node.conexion_udp;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.other.CustomPrinter;
import org.example.execution_node.conexion_udp.udp_multicast.MulticastUDPTransmitter;
import java.io.IOException;
import static java.lang.Thread.sleep;

public class ConnectionUDPManager implements  Runnable{
    // singleton
    private static ConnectionUDPManager connectionUDPManager;
    private final MulticastUDPTransmitter multicastUDPTransmitter;

    private ConnectionUDPManager() {
        multicastUDPTransmitter = new MulticastUDPTransmitter();
    }

    public static ConnectionUDPManager getConnectionUDPManager() {
        if (connectionUDPManager == null) {
            connectionUDPManager = new ConnectionUDPManager();
        }
        return connectionUDPManager;
    }

    @Override
    public void run() {
        try {
            while (true) {
                multicastUDPTransmitter.send();
                try {
                    sleep((int)(0.5*60*1000)); // 30 seconds
                } catch (InterruptedException interruptedException) {
                    String stacktrace = ExceptionUtils.getStackTrace(interruptedException);
                    CustomPrinter.printErr(stacktrace);
                }
            }
        } catch (IOException e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            CustomPrinter.printErr(stacktrace);
        }
    }
}
