package org.example.execution_node.code_executor.implementations;

import org.example.execution_node.code_executor.management.jvmLoad.Compile;
import org.example.execution_node.other.CustomPrintStreamError;
import org.example.execution_node.other.CustomPrinter;
import java.io.*;
import java.net.*;

public class CustomClassLoaderJar extends URLClassLoader {
    private final String fileNameToReplace;
    private final File replacementFile;
    private final String address;
    private final int port;
    private final String userId;

    public CustomClassLoaderJar(URL[] urls, ClassLoader parent, String fileNameToReplace, File replacementFile, String address, int port, String userId) {
        super(urls, parent);
        this.fileNameToReplace = fileNameToReplace;
        this.replacementFile = replacementFile;
        this.address = address;
        this.port = port;
        this.userId = userId;
    }

    @Override
    public URL findResource(String name) {
        if (name.endsWith(fileNameToReplace)) {
            try {
                CustomPrinter.printSuccess(replacementFile.getPath());
                return replacementFile.toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            }
        }
        return super.findResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        if (name.endsWith(fileNameToReplace)) {
            try {
                CustomPrinter.printInfo(name);
                CustomPrinter.printSuccess(replacementFile.getPath());

                writeFileGetPath(address, port, userId, "GetPathFile.java", name.replace("/GetPathFile.class", ""));
                Compile.compile("GetPathFile.java", userId, address, port);

                return new FileInputStream(replacementFile);
            } catch (IOException e) {
                e.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            }
        }
        return super.getResourceAsStream(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            String path = name.replace('.', '/').replace(".class", "").concat(".class");
            InputStream stream = getResourceAsStream(path);
            if (stream == null) {
                throw new ClassNotFoundException(name);
            }
            try {
                byte[] buffer = new byte[stream.available()];
                stream.read(buffer);

                return defineClass(name, buffer, 0, buffer.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        } catch (ClassNotFoundException e) {
            return super.findClass(name);
        }
    }

    public void writeFileGetPath(String address, int port, String userId, String fileName, String packageName) throws IOException {
        FileWriter fileWriter = new FileWriter("serverJavaResourcesRemote/" + address + "_" + port + "_" + userId + "/" + fileName);
        fileWriter.write("package " + packageName.replace("/", ".") + ";\n" +
                "import java.nio.file.*;\n" +
                "\n" +
                "public class GetPathFile {\n" +
                "    public static String getPath() {\n" +
                "        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();\n" +
                "        String threadGroup = parentGroup.getName();\n" +
                "        Path directory = Paths.get( \"serverJavaResourcesRemote/\" + threadGroup);\n" +
                "        if (!Files.exists(directory)) {\n" +
                "            try {\n" +
                "                Files.createDirectory(directory);\n" +
                "            } catch (Exception e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "        }\n" +
                "        return \"./\" + directory.toString() + \"/\";\n" +
                "    }\n" +
                "}\n");
        fileWriter.close();
    }
}
