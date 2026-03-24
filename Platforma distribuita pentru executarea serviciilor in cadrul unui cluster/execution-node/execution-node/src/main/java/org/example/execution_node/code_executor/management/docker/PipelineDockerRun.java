package org.example.execution_node.code_executor.management.docker;

import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.execution_node.code_executor.management.files.FilesSend;
import org.example.execution_node.code_executor.management.files.model.FileModel;
import org.example.execution_node.conexion_tcp.model.MessageToExecutionNode;
import org.example.execution_node.conexion_tcp.model.MessageToMaster;
import org.example.execution_node.conexion_tcp.stream.CustomPrintStreamSocket;
import org.example.execution_node.conexion_tcp.stream.GetPathFile;
import org.example.execution_node.other.CustomPrinter;

import java.io.*;
import java.util.List;

public class PipelineDockerRun {
    private final Gson gson = new Gson();

    public void runInDocker(MessageToExecutionNode message, DataInputStream dataInputStream) {
        CustomPrinter.printInfo(message.toString());
        // write + compile
        try {
            if (message.getFileName().endsWith(".java")) {
                this.writeFiles(message.getFileName(), message.getContent());
                if (this.compile(message) != 0) {
                    System.out.println("Error compile file: " + message.getFileName());
                }
            } else {
                this.writeFiles(message.getFileName(), message.getContentByte());
            }
        } catch (IOException | InterruptedException ioException) {
            String stacktrace = ExceptionUtils.getStackTrace(ioException);
            CustomPrinter.printErr(stacktrace);
        }

        // run code
        if (message.getIsStartUp() == 1) {
            clearShort(message.getUserId());
            FilesSend filesSend = new FilesSend();
            try {
                if (message.getFileName().endsWith(".java") || message.getFileName().endsWith(".class")) {
                    int status = this.createJar(message);
                    if (status != 0) {
                        System.out.println("Error create jar file.");
                    }
                    CustomPrinter.printInfo("Status create jar: " + status + " for: " + message.getUserId());
                }

                // volume
                int status = this.createVolume(message.getUserId());
                CustomPrinter.printInfo("Status create volume: " + status);

                if (status == 0) {
                    // image
                    status = this.createImage(message);
                    CustomPrinter.printInfo("Docker build image: " + status);

                    if (status == 0) {
                        // run
                        status = this.runDockerContainer(message, dataInputStream);
                        CustomPrinter.printInfo("Status process run container: " + status);
                        if (status != 0) {
                            System.out.println("Error run process!");
                        }
                        // out

                        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                        String threadGroup = parentGroup.getName();
                        CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                        customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
                    } else {
                        System.out.println("Error create docker image!");
                        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                        String threadGroup = parentGroup.getName();
                        CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                        customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
                    }
                } else {
                    System.out.println("Error create docker volume for user!");
                    ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                    String threadGroup = parentGroup.getName();
                    CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                    customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP")) + "\n");
                }
            } catch (IOException | InterruptedException e) {
                String stacktrace = ExceptionUtils.getStackTrace(e);
                CustomPrinter.printErr(stacktrace);
            } finally {
                try {
                    filesSend.deleteDirectory(GetPathFile.getPath());
                    filesSend.deleteDirectory( GetPathFile.getPath().substring(0, GetPathFile.getPath().length() - 1)+ "_" + message.getUserId());
                    if (
                            this.deleteContainer(message.getUserId()) != 0) {
                        CustomPrinter.printErr("Error delete container!");
                    }else{
                        CustomPrinter.printSuccess("Success delete container!");
                    }
                    if (this.deleteVolume(message.getUserId()) != 0) {
                        CustomPrinter.printErr("Error delete volume!");
                    }else{
                        CustomPrinter.printSuccess("Success delete volume!");
                    }
                    if(this.deleteImage(message.getUserId()) != 0){
                        CustomPrinter.printErr("Error delete image!");
                    }else{
                        CustomPrinter.printSuccess("Success delete image!");
                    }
                } catch (Exception ex) {
                    String stacktrace = ExceptionUtils.getStackTrace(ex);
                    CustomPrinter.printErr(stacktrace);
                }
            }
        }
    }

    private void writeFiles(String fileName, String content) throws IOException {
        FileWriter fos = new FileWriter(GetPathFile.getPath() + fileName);
        fos.write(content);
        fos.close();
    }

    private void writeFiles(String fileName, byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(GetPathFile.getPath() + "/" + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(content);
        bos.close();
    }

    public int compile(MessageToExecutionNode message) throws IOException, InterruptedException {
        // compile
        Runtime runtime = Runtime.getRuntime();
        String currentWorkingDirectory = System.getProperty("user.dir");
        String fullPath = currentWorkingDirectory + File.separator + GetPathFile.getPath();
        Process process = runtime.exec("javac " + message.getFileName(), null, new File(fullPath));

        // compile error
        BufferedReader is =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = is.readLine()) != null)
            System.out.println(line);
        return process.waitFor();
    }

    public Thread getThreadReadNormalOutputStream(InputStream inputStream) {
        return new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    CustomPrinter.printInfo(line);
                }
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                CustomPrinter.printErr(stacktrace);
            }
        });
    }

    public Thread getThreadErrorOutputStream(InputStream inputStream) {
        return new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                int number = 0;
                while ((line = reader.readLine()) != null) {
                    if (number == 0) {
                        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                        String threadGroup = parentGroup.getName();
                        CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                        customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "START_EXCEPTION")) + "\n");
                    }
                    number++;
                    System.out.println(line);
                }
                if (number > 0) {
                    ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
                    String threadGroup = parentGroup.getName();
                    CustomPrintStreamSocket customPrintStreamSocket = CustomPrintStreamSocket.getListOfNodes().get(threadGroup);
                    customPrintStreamSocket.addDirectMessage(this.gson.toJson(new MessageToMaster(customPrintStreamSocket.getPrefix(), "", null, "STOP_EXCEPTION")) + "\n");

                }
            } catch (Exception ex) {
                String stacktrace = ExceptionUtils.getStackTrace(ex);
                CustomPrinter.printErr(stacktrace);
            }
        });
    }

    public int createVolume(String userid) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("docker volume create " + userid);

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        StringBuilder err = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            err.append(line).append("\n");
        }
        CustomPrinter.printErr(err.toString());

        return process.waitFor();
    }

    public int createImage(MessageToExecutionNode message) throws IOException, InterruptedException {
        if( message.getFileName().endsWith(".java") || message.getFileName().endsWith(".class") || message.getFileName().endsWith(".jar")) {
            FileWriter fileWriter = getFileWriter(message);
            fileWriter.close();
        }else{
            // .py
            FileWriter fileWriter = new FileWriter(GetPathFile.getPath() +"Dockerfile");
            fileWriter.write("FROM python:3.9-slim\n");
            fileWriter.write("WORKDIR /app\n");
            fileWriter.write("COPY . .\n");
            fileWriter.write("CMD [\"python3\", \"" + message.getFileName() + "\"]\n");

            fileWriter.close();
        }

        ProcessBuilder processBuilder = new ProcessBuilder("docker", "image", "build", "-t", message.getUserId() + ":latest", ".");
        processBuilder.directory(new File(GetPathFile.getPath()));

        Process process = processBuilder.start();
        if (process.info().commandLine().isPresent())
            CustomPrinter.printInfo(process.info().commandLine().get());

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        CustomPrinter.printErr(stringBuilder.toString());

        return process.waitFor();
    }

    private static FileWriter getFileWriter(MessageToExecutionNode message) throws IOException {
        FileWriter fileWriter = new FileWriter(GetPathFile.getPath() + "Dockerfile");
        fileWriter.write("FROM amazoncorretto:17-alpine-jdk\n");
        fileWriter.write("WORKDIR /app\n");
        fileWriter.write("ADD " + message.getFileName().replace(".class", ".jar").replace(".java", ".jar") + " " + message.getFileName().replace(".class", ".jar").replace(".java", ".jar") + "\n");
        fileWriter.write("COPY . .\n");
        fileWriter.write("CMD java -jar " + message.getFileName().replace(".class", ".jar").replace(".java", ".jar") + "\n");
        return fileWriter;
    }

    public int runDockerContainer(MessageToExecutionNode message, DataInputStream dataInputStream) throws IOException, InterruptedException {
        FilesSend filesSend = new FilesSend();
        List<FileModel> listBefore = filesSend.getFileNames();

        Process process = Runtime.getRuntime().exec("docker run --network none -m 400m  -v " + message.getUserId() + ":/app " + "--name " + message.getUserId() + " -i " + message.getUserId() + ":latest ", null, new File(GetPathFile.getPath()));

        if (process.info().commandLine().isPresent())
            CustomPrinter.printInfo(process.info().commandLine().get());

        // read normal stream
        Thread outputThread = this.getThreadReadNormalOutputStream(process.getInputStream());
        // read error stream
        Thread outputErrorThread = this.getThreadErrorOutputStream(process.getErrorStream());

        // input stream
        Thread inputThread = new Thread(() -> {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                String line;

                boolean ok = true;
                while (process.isAlive() && ok) {
                    line = dataInputStream.readUTF();
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                    if (line.contains("STOP"))
                        ok = false;
                }
            } catch (Exception ignored) {
                //System.out.println("Internal server error!");
            }
        });

        outputThread.start();
        outputErrorThread.start();
        inputThread.start();

        int status = process.waitFor();

        if (this.moveInLocalDirectory(message) != 0) {
            System.out.println("Internal server error!");
        }

        filesSend.compressFileInDirectory("");
        List<FileModel> listAfter = filesSend.getFileNames();
        filesSend.sendFiles(listBefore, listAfter);

        // stop
        outputThread.interrupt();
        outputErrorThread.interrupt();
        inputThread.interrupt();

        return status;
    }

    public int moveInLocalDirectory(MessageToExecutionNode message) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("docker cp " + message.getUserId().replace(".jar", "").toLowerCase() + ":/app" + " ./" + GetPathFile.getPath());
        if (process.info().commandLine().isPresent())
            CustomPrinter.printSuccess(process.info().commandLine().get());

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        CustomPrinter.printErr(stringBuilder.toString());

        int status = process.waitFor();
        CustomPrinter.printInfo("Status cp docker:local: " + status);
        if (status == 0) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.directory(new File(GetPathFile.getPath()));

                processBuilder.command("bash", "-c", "cp -n -r ./app/* ./");

                process = processBuilder.start();

                status = process.waitFor();

                processBuilder = new ProcessBuilder();
                processBuilder.directory(new File("./" + GetPathFile.getPath()));
                processBuilder.command("rm", "-rf", "./app");

                process = processBuilder.start();

                StringBuilder err = new StringBuilder();
                reader =
                        new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    err.append(line).append("\n");
                }
                CustomPrinter.printErr(err.toString());

                status = process.waitFor();
            } catch (IOException | InterruptedException e) {
                String stacktrace = ExceptionUtils.getStackTrace(e);
                CustomPrinter.printErr(stacktrace);
                //System.out.println("Internal server error!");
            }
        }

        return status;
    }

    public int deleteContainer(String name) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker", "rm", name);

        Process process = processBuilder.start();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder err = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            err.append(line).append("\n");
        }
        CustomPrinter.printErr(err.toString());

        return process.waitFor();
    }

    public int deleteImage(String imageName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker", "rmi", imageName);

        Process process = processBuilder.start();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder err = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            err.append(line).append("\n");
        }
        CustomPrinter.printErr(err.toString());

        return process.waitFor();
    }

    public int deleteVolume(String name) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker", "volume", "rm", name);

        Process process = processBuilder.start();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder err = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            err.append(line).append("\n");
        }
        CustomPrinter.printErr(err.toString());

        return process.waitFor();
    }

    public int createJar(MessageToExecutionNode message) throws IOException, InterruptedException {
        FileWriter fileWriter = new FileWriter(GetPathFile.getPath() + "manifest.txt");
        fileWriter.write("Manifest-Version: 1.0\n");
        fileWriter.write("Created-By: Madab\n");
        fileWriter.write("Main-Class: " + message.getFileName().replace(".class", "").replace(".java", "") + "\n");
        fileWriter.close();

        //    fileWriter = new FileWriter(GetPathFile.getPath() + "run.sh");
        //   fileWriter.write("jar cfm " + message.getFileName().replace(".class", "").replace(".java", "") + ".jar " + "manifest.txt " + "*.class\n");
        //     fileWriter.close();

        //     ProcessBuilder processBuilder = new ProcessBuilder("sh", "run.sh");
        //     processBuilder.directory(new File(GetPathFile.getPath()));

        //    Process process = processBuilder.start();
        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "jar cfm " + message.getFileName().replace(".class", "").replace(".java", "") + ".jar manifest.txt *.class");
        processBuilder.directory(new File(GetPathFile.getPath()));
        Process process = processBuilder.start();
        if (process.info().commandLine().isPresent())
            CustomPrinter.printInfo("Run command: " + process.info().commandLine().get());

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder err = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            err.append(line).append("\n");
        }
        CustomPrinter.printErr(err.toString());

        return process.waitFor();
    }

    public void clear(String user){
        try {
            FilesSend filesSend = new FilesSend();
            filesSend.deleteDirectory(GetPathFile.getPath());
            filesSend.deleteDirectory(GetPathFile.getPath().substring(0, GetPathFile.getPath().length() - 1) + "_" + user);
            if (this.stopContainer(user) != 0) {
                CustomPrinter.printErr("Error stop container!");
                return;
            } else {
                CustomPrinter.printSuccess("Success stop container!");
            }
            if (this.deleteContainer(user) != 0) {
                CustomPrinter.printErr("Error delete container!");
                return;
            } else {
                CustomPrinter.printSuccess("Success delete container!");
            }
            if (this.deleteVolume(user) != 0) {
                CustomPrinter.printErr("Error delete volume!");
                return;
            } else {
                CustomPrinter.printSuccess("Success delete volume!");
            }
            if (this.deleteImage(user) != 0) {
                CustomPrinter.printErr("Error delete image!");
            } else {
                CustomPrinter.printSuccess("Success delete image!");
            }
        }catch (Exception ex){
            CustomPrinter.printErr(ex.getMessage());
        }
    }
    public void clearShort(String user){
        try {

            if (this.stopContainer(user) != 0) {
                CustomPrinter.printErr("Error stop container!");
                return;
            } else {
                CustomPrinter.printSuccess("Success stop container!");
            }
            if (this.deleteContainer(user) != 0) {
                CustomPrinter.printErr("Error delete container!");
                return;
            } else {
                CustomPrinter.printSuccess("Success delete container!");
            }
            if (this.deleteVolume(user) != 0) {
                CustomPrinter.printErr("Error delete volume!");
                return;
            } else {
                CustomPrinter.printSuccess("Success delete volume!");
            }
            if (this.deleteImage(user) != 0) {
                CustomPrinter.printErr("Error delete image!");
            } else {
                CustomPrinter.printSuccess("Success delete image!");
            }
        }catch (Exception ex){
            CustomPrinter.printErr(ex.getMessage());
        }
    }

    private int stopContainer(String name) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker", "stop", name);

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder err = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            err.append(line).append("\n");
        }
        CustomPrinter.printErr(err.toString());

        return process.waitFor();
    }
}
