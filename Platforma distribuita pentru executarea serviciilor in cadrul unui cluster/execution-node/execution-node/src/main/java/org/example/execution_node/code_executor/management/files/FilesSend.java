package org.example.execution_node.code_executor.management.files;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.code_executor.management.files.model.FileModel;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import org.example.execution_node.conexion_tcp.stream.GetPathFile;
import org.example.execution_node.other.CustomPrinter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class FilesSend {
    private final Gson gson;

    public FilesSend() {
        this.gson = new Gson();
    }

    public void sendFiles(List<FileModel> listBefore, List<FileModel> listAfter) {
        try {
            for (FileModel element : listAfter) {
                boolean ok = true;
                for(FileModel fileModel : listBefore){
                    if (element.getFileName().equals(fileModel.getFileName()) && element.getFileTime().equals(fileModel.getFileTime())) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    CustomPrinter.printSuccess(element.toString());
                    File file = new File(element.getFileName());

                    if (file.exists() && !file.isDirectory()) {
                        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                        String threadGroup = parentGroup.getName();
                        CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                        customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "NEW_FILE")) + "\n");
                        customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, file.getName())) + "\n");

                        byte[] contentFile = Files.readAllBytes(Path.of(file.getPath()));

                        customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);

                        customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", contentFile, null)) + "\n");

                         if (file.exists()) {
                            if (!file.delete()) {
                                CustomPrinter.printErr("Error delete file: " + file.getName());
                            }
                        } else {
                            CustomPrinter.printErr("Error delete file, file not found: " + file.getName());
                        }
                    }
                }
            }

        } catch (Exception ex) {
            String stacktrace = ExceptionUtils.getStackTrace(ex);
            CustomPrinter.printErr(stacktrace);
            System.out.println("Internal server error!");
        }
    }

    public List<FileModel> getFileNames() {
        List<FileModel> list = new ArrayList<>();
        File directory = new File("./" + GetPathFile.getPath());
        File[] files = directory.listFiles();
        assert files != null;

        for (File file : files) {
            try {
                BasicFileAttributes attrs = Files.readAttributes(Path.of("./" + GetPathFile.getPath() + file.getName()), BasicFileAttributes.class);
                FileTime creationTime = attrs.lastModifiedTime();
                list.add(new FileModel("./" + GetPathFile.getPath() + file.getName(),creationTime));
            }catch (Exception ex){
                ex.printStackTrace(System.err);
            }
        }
        return list;
    }

    public void compressFileInDirectory(String subdirectory) {
        File directory = new File("./" + GetPathFile.getPath() + subdirectory);
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                CustomPrinter.printWarning(file.getName());
                String bash = "zip -jrq " + GetPathFile.getPath() + file.getName() + " " + GetPathFile.getPath() + file.getName();
                CustomPrinter.printWarning(bash);
                try {
                    Process process = Runtime.getRuntime().exec(bash);
                    int result = process.waitFor();
                    if (result == 0) {
                        CustomPrinter.printSuccess("Status compress 0");
                    } else {
                        CustomPrinter.printSuccess("Status compress 1");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        String linie;
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((linie = reader.readLine()) != null) {
                            stringBuilder.append(linie).append("\n");
                        }
                        CustomPrinter.printErr(stringBuilder.toString());

                        reader.close();
                    }
                } catch (IOException | InterruptedException e) {
                    String stacktrace = ExceptionUtils.getStackTrace(e);
                    CustomPrinter.printErr(stacktrace);
                    System.out.println("Error create zip files.");
                }
            }
        }
    }

    public void deleteDirectory(String dirName) throws IOException {
        CustomPrinter.printInfo(dirName);
        try {
            Files.walkFileTree(Path.of(dirName), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch (Exception ex){
            CustomPrinter.printErr(ex.getMessage());
        }
    }
}
