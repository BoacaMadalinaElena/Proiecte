package org.example.execution_node.security;

import org.example.execution_node.other.CustomPrintStreamError;
import org.example.execution_node.other.CustomPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class CustomSecurityManager extends SecurityManager {
    private static final HashMap<String, Integer> hashTypeThreadGroup = new HashMap<>();
    private static final HashMap<String, String> hashUserIdThreadGroup = new HashMap<>();
    private static final Set<String> listOfThreads = new HashSet<>();

    public static void addThreadGroup(int type, String threadGroup,String userId) {
        hashTypeThreadGroup.put(threadGroup, type);
        hashUserIdThreadGroup.put(threadGroup,userId);
    }

    public static void addThreadGroup(String group) {
        listOfThreads.add(group);
    }

    static {
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        CustomPrinter.printSuccess(threadGroup);
        addThreadGroup("main");
        addThreadGroup("system");
        addThreadGroup(threadGroup);
    }

    @Override
    public void checkExec(String cmd) {
        HashMap<Integer, List<String>> accept = new HashMap<Integer, List<String>>();
        accept.put(0, new ArrayList<>());
        accept.put(2, new ArrayList<>());

        // nu e nimic periculos
        accept.get(0).add("javac");
        accept.get(0).add("zip");

        // oricum el executa in docker
        accept.get(2).add("javac");
        accept.get(2).add("zip");
        accept.get(2).add("java");
        accept.get(2).add("jar");
        accept.get(2).add("docker");
        accept.get(2).add("sh");
        accept.get(2).add("cp");
        accept.get(2).add("rm");
        accept.get(2).add("bash");

        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        Integer val = hashTypeThreadGroup.get(threadGroup);

        if (Objects.equals(cmd, "free") || Objects.equals(cmd, "uname")) {
            return;
        }
        boolean ok = false;
        for (String s : accept.get(val)) {
            if (Objects.equals(s, cmd) && !cmd.contains("||") && !cmd.contains("&&") && !cmd.contains(";")) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            CustomPrinter.printCheck("The commands cannot be executed at the host system level: " + cmd);
            ThreadGroup parentGroupTest = Thread.currentThread().getThreadGroup();
            String threadGroupTest = parentGroupTest.getName();
            sendAlert(hashUserIdThreadGroup.get(threadGroupTest));
            CustomPrinter.printFile(hashUserIdThreadGroup.get(threadGroupTest));
            throw new SecurityException("The commands cannot be executed at the host system level!");
        }
    }

    @Override
    public void checkRead(String file) {
        if (!((file.contains("serverJavaResourcesRemote") || file.contains("javac") ||
                file.startsWith("/usr/lib/jvm/") ||  file.startsWith("/proc/cgroups") || file.startsWith("/proc/self/mountinfo") || file.startsWith("/proc/self/cgroup") || file.startsWith("/sys/fs/cgroup/") ||
                file.startsWith("/etc/java-17-openjdk/") ||
                file.startsWith("logs_execution") || file.startsWith("/etc/java/")
                || file.startsWith("/tmp/")  || file.equals("/home/madab/.accessibility.properties") || file.equals("/dev/urandom")|| file.equals("/dev/random") ) && !file.contains("../")
                || file.endsWith("execution-node-1.0-SNAPSHOT-jar-with-dependencies.jar") )) {
            CustomPrinter.printCheck("Access denied: Unable to read from inaccessible path: " + file);
            ThreadGroup parentGroupTest = Thread.currentThread().getThreadGroup();
            String threadGroupTest = parentGroupTest.getName();
            sendAlert(hashUserIdThreadGroup.get(threadGroupTest));
            CustomPrinter.printFile(hashUserIdThreadGroup.get(threadGroupTest));
            throw new SecurityException("Access denied: Unable to read from inaccessible path.");
        }
    }

    @Override
    public void checkDelete(String file) {
        if (!((file.contains("serverJavaResourcesRemote") ||  file.startsWith("/tmp/")||
                file.startsWith("logs_execution")   ) && !file.contains("../")
        )) {
            CustomPrinter.printCheck("Access denied: Unable to read from inaccessible path: " + file);
            ThreadGroup parentGroupTest = Thread.currentThread().getThreadGroup();
            String threadGroupTest = parentGroupTest.getName();
            sendAlert(hashUserIdThreadGroup.get(threadGroupTest));
            CustomPrinter.printFile(hashUserIdThreadGroup.get(threadGroupTest));
            throw new SecurityException("Access denied: Unable to read from inaccessible path.");
        }
    }

    @Override
    public void checkWrite(String file) {
        if (!((file.contains("serverJavaResourcesRemote") ||
            file.startsWith("/tmp/") ||
                file.startsWith("logs_execution")   ) && !file.contains("../")
        )) {
            CustomPrinter.printCheck("Access denied: Unable to read from inaccessible path: " + file);
            ThreadGroup parentGroupTest = Thread.currentThread().getThreadGroup();
            String threadGroupTest = parentGroupTest.getName();
            sendAlert(hashUserIdThreadGroup.get(threadGroupTest));
            CustomPrinter.printFile(hashUserIdThreadGroup.get(threadGroupTest));
            throw new SecurityException("Access denied: Unable to read from inaccessible path.");
        }
    }

    @Override
    public void checkConnect(String host, int port) {
            if (!(host.startsWith("172") || host.startsWith("192")  ||host.endsWith("ec2.internal") || Objects.equals(host, "localhost") || Objects.equals(host, "127.0.0.1")  || Objects.equals(host, System.getProperty("user.name"))|| host.equals("100.24.146.38") || host.equals("0.0.0.0") || host.equals("madab")||
            host.equals("fe80:0:0:0:5cc0:2dff:fe02:8dfc%veth4bd6e00")
            ) ) {
                CustomPrinter.printCheck("You are not allowed to perform network operations: " + host);
                ThreadGroup parentGroupTest = Thread.currentThread().getThreadGroup();
                String threadGroupTest = parentGroupTest.getName();
                sendAlert(hashUserIdThreadGroup.get(threadGroupTest));
                CustomPrinter.printFile(hashUserIdThreadGroup.get(threadGroupTest));
                throw new SecurityException("You are not allowed to perform network operations!");
        }
    }

    @Override
    public void checkAccess(ThreadGroup g) {
        if (!listOfThreads.contains(g.getName() ) && !g.getName().equals("InnocuousThreadGroup")) {
            ThreadGroup parentGroupTest = Thread.currentThread().getThreadGroup();
            String threadGroupTest = parentGroupTest.getName();
            sendAlert(hashUserIdThreadGroup.get(threadGroupTest));
            CustomPrinter.printFile(hashUserIdThreadGroup.get(threadGroupTest));
            throw new SecurityException("Thread group objects cannot be created for use.");
        }
    }

    public void sendAlert(String userId){
        try {

            URL url = new URL("http://100.24.146.38:8081/api/cluster/user/securityAttack");

            int responseCode = getResponseCode(userId, url);
            CustomPrinter.printInfo("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
               CustomPrinter.printSuccess("Request was successful.");
            } else {
                CustomPrinter.printErr("Request failed." + responseCode);
            }
        }catch (Exception ex){
            ex.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
        }
    }

    private static int getResponseCode(String userId, URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "GrtIw3=Gl+iSE!nlc2KYu=Aksaau!97hw1nHh#X!=4Ilsk%Y*Kg8Gax$#1T$953fvfG--jdv9Z4RR*D7i+HDRoc#-oujScS0UoId^A5kQE2fmv74xBl&Oka-E6cY_R7#MA_P*nk+4m_wPsT@xdo@9B%sWq8i8JEpC52NVlk7jhhGX3zik3zylJMF67e4Vv_C1uWR%9wz");

        connection.setDoOutput(true);

        String jsonInputString = "{\"userId\": \"" + userId + "\"}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return connection.getResponseCode();
    }
}