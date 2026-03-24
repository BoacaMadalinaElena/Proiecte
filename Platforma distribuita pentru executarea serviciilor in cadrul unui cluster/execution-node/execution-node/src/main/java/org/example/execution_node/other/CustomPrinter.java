package org.example.execution_node.other;

import com.opencsv.CSVWriter;
import lombok.Getter;
import lombok.Setter;
import org.example.execution_node.conexion_tcp.ConnectionTCPManager;
import java.io.FileWriter;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomPrinter {
    private static final PrintStream originalOut;
    private static final List<String[]> listMessages = new ArrayList<>();
    private static ConnectionTCPManager connectionTCPManager;
    @Setter
    @Getter
    private static int currentIdFile;
    @Getter
    private static AtomicBoolean lock = new AtomicBoolean(false);

    static {
        originalOut = System.out;
    }

    private static final Map<String, String> users = Collections.synchronizedMap(new HashMap<String, String>());




    public static void printInfo(String input) {
        if (input.isEmpty()) {
            return;
        }
        if (connectionTCPManager == null) {
            try {
                connectionTCPManager = ConnectionTCPManager.getCodeExecutorManager();
            } catch (Exception ex) {
                System.exit(-1);
            }
        }
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        insertRecordInCSV(new String[]{LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort(), "INFO", users.getOrDefault(threadGroup, "GENERIC"), input});
        synchronizedPrint(ColorPrint.BLUE + "EXECUTION\t" + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" + connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort() + "\t" + String.format("%12s", "INFO") + "\t" + users.getOrDefault(threadGroup, "GENERIC") + "\t" + input + "\n" + ColorPrint.RESET);
    }

    public static void printWarning(String input) {
        if (input.isEmpty()) {
            return;
        }
        if (connectionTCPManager == null) {
            try {
                connectionTCPManager = ConnectionTCPManager.getCodeExecutorManager();
            } catch (Exception ex) {
                System.exit(-1);
            }
        }
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        insertRecordInCSV(new String[]
                {
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort(),
                        "WARNING",
                        users.getOrDefault(threadGroup, "GENERIC"),
                        input
                }
        );
        synchronizedPrint(ColorPrint.YELLOW + "EXECUTION\t" + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" + connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort() + "\t" + String.format("%12s", "WARNING") + "\t" + users.getOrDefault(threadGroup, "GENERIC") + "\t" + input + "\n" + ColorPrint.RESET);
    }

    public static void printErr(String input) {
        if (input.isEmpty()) {
            return;
        }
        if (connectionTCPManager == null) {
            try {
                connectionTCPManager = ConnectionTCPManager.getCodeExecutorManager();
            } catch (Exception ex) {
                System.exit(-1);
            }
        }
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        insertRecordInCSV(new String[]{LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort(), "ERROR", users.getOrDefault(threadGroup, "GENERIC"), input});
        synchronizedPrint(ColorPrint.RED + "EXECUTION\t" + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" + connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort() + "\t" + String.format("%12s", "ERROR") + "\t" + users.getOrDefault(threadGroup, "GENERIC") + "\t" + input + "\n" + ColorPrint.RESET);
    }

    public static void printSuccess(String input) {
        if (input.isEmpty()) {
            return;
        }
        if (connectionTCPManager == null) {
            try {
                connectionTCPManager = ConnectionTCPManager.getCodeExecutorManager();
            } catch (Exception ex) {
                System.exit(-1);
            }
        }
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        insertRecordInCSV(new String[]{LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort(), "SUCCESS", users.getOrDefault(threadGroup, "GENERIC"), input});
        synchronizedPrint(ColorPrint.GREEN + "EXECUTION\t" + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" + connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort() + "\t" + String.format("%12s", "SUCCESS") + "\t" + users.getOrDefault(threadGroup, "GENERIC") + "\t" + input + "\n" + ColorPrint.RESET);
    }

    public static void printNormal(String input) {
        if (input.isEmpty()) {
            return;
        }
        if (connectionTCPManager == null) {
            try {
                connectionTCPManager = ConnectionTCPManager.getCodeExecutorManager();
            } catch (Exception ex) {
                System.exit(-1);
            }
        }
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        insertRecordInCSV(new String[]{LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort(), "MESSAGE", users.getOrDefault(threadGroup, "GENERIC"), input});
        synchronizedPrint(ColorPrint.WHITE + "EXECUTION\t" + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" + connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort() + "\t" + String.format("%12s", "CHECK") + "\t" + users.getOrDefault(threadGroup, "GENERIC") + "\t" + input + "\n" + ColorPrint.RESET);
    }

    public static void printCheck(String input) {
        if (input.isEmpty()) {
            return;
        }

        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        synchronizedPrint(ColorPrint.ORANGE + "EXECUTION\t" + "[" + "     undefined     " + "]\t" + "undefined" + ":" + "undefined" + "\t" + String.format("%12s", "CHECK") + "\t" + users.getOrDefault(threadGroup, "GENERIC") + "\t" + input + "\n" + ColorPrint.RESET);
    }

    private static void synchronizedPrint(String s) {
        synchronized (originalOut) {
            if (!s.isEmpty()) {
                originalOut.print(s);
            }
        }
    }

    public static void addUser(String threadGroup, String userId) {
        synchronized (users) {
            users.put(threadGroup, userId);
        }
    }

    private static void insertRecordInCSV(String[] logs) {
        synchronized (listMessages) {
            listMessages.add(logs);
            if (listMessages.size() == 100) {
                clearListMessages();
            }
        }
    }

    private static void clearListMessages() {
        lock.set(true);
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm"));
        String modifiedDateTime = formattedDateTime.substring(0, formattedDateTime.length() - 1);
        modifiedDateTime += "0";
        String csvFile = "logs_execution_" + modifiedDateTime + ".csv";
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile, true));
            csvWriter.writeAll(listMessages);
            listMessages.clear();
            csvWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
        }
        lock.set(false);
    }

    public static void printFile(String input) {
        if (input.isEmpty()) {
            return;
        }
        if (connectionTCPManager == null) {
            try {
                connectionTCPManager = ConnectionTCPManager.getCodeExecutorManager();
            } catch (Exception ex) {
                System.exit(-1);
            }
        }
        ThreadGroup parentGroup = Thread.currentThread().getThreadGroup();
        String threadGroup = parentGroup.getName();
        insertRecordInCSV(new String[]{LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort(), "FILE", users.getOrDefault(threadGroup, "GENERIC"), input});
        synchronizedPrint(ColorPrint.MAGENTA + "EXECUTION\t" + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" + connectionTCPManager.getIpAddress() + ":" + connectionTCPManager.getPort() + "\t" + String.format("%12s", "FILE") + "\t" + users.getOrDefault(threadGroup, "GENERIC") + "\t" + input + "\n" + ColorPrint.RESET);

    }
}
