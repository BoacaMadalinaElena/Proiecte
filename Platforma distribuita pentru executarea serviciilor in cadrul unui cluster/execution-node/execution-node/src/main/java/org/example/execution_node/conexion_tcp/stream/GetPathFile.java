package org.example.execution_node.conexion_tcp.stream;

import org.example.execution_node.other.CustomPrintStreamError;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetPathFile {
    public static String getPath() {
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        Path directory = Paths.get( "serverJavaResourcesRemote/" + threadGroup);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectory(directory);
            } catch (Exception e) {
                e.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            }
        }
        return directory + "/";
    }
}

