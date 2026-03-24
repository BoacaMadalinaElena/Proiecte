package org.example.other;

import com.opencsv.CSVWriter;
import lombok.Getter;
import lombok.Setter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomPrinter {
    private static final PrintStream originalOut;
    private static final List<String[]> listMessages = new ArrayList<>();
    @Setter
    @Getter
    private static int currentIdFile;
    @Getter
    private static AtomicBoolean lock = new AtomicBoolean(false);
    @Getter
    private static String basePath =  null;
    private static String ipAddress;

    static {
        originalOut = System.out;

        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();

            while (interfaceAddresses.hasMoreElements()) {
                InetAddress address = interfaceAddresses.nextElement();

                // 127.0.0.1
                // 192.168
                if (address instanceof Inet4Address && (address.getHostAddress().startsWith("192.168") || address.getHostAddress().startsWith("172"))) {
                    ipAddress = address.getHostAddress();
                    break;
                }
            }
        }

    }

    public static void printInfo(String input) {
        insertRecordInCSV(new String[]
                {
                        //localDateTime,address,type,message
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        ipAddress,
                        "INFO",
                        input
                }
        );
        synchronizedPrint(ColorPrint.BLUE + "MASTER\t" + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" + String.format("%12s", "INFO") + "\t" + input + "\n" + ColorPrint.RESET);
    }

    public static void printWarning(String input) {
        insertRecordInCSV(new String[]
                {
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        ipAddress,
                        "WARNING",
                        input
                }
        );
        synchronizedPrint(ColorPrint.YELLOW + "MASTER\t" +"[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" + String.format("%12s", "WARNING") + "\t" + input + "\n" + ColorPrint.RESET);
    }

    public static void printErr(String input) {
        insertRecordInCSV(new String[]
                {
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        ipAddress,
                        "ERROR",
                        input
                }
        );
        synchronizedPrint(ColorPrint.RED +"MASTER\t" + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" + String.format("%12s", "ERROR") + "\t" + input + "\n" + ColorPrint.RESET);
    }

    public static void printSuccess(String input) {
        insertRecordInCSV(new String[]
                {
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        ipAddress,
                        "SUCCESS",
                        input
                }
        );
        synchronizedPrint(ColorPrint.GREEN + "MASTER\t" +"[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t"  + String.format("%12s", "SUCCESS") + "\t" +   input + "\n" + ColorPrint.RESET);
    }

    public static void printNormal(String input) {
        insertRecordInCSV(new String[]
                {
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        ipAddress,
                        "MESSAGE",
                        input});
        synchronizedPrint(ColorPrint.WHITE+ "MASTER\t" + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t"  + String.format("%12s", "MESSAGE") + "\t"+ input + "\n" + ColorPrint.RESET);
    }

    private static void synchronizedPrint(String s) {
        synchronized (originalOut) {
            originalOut.print(s);
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
        synchronized (listMessages) {
            lock.set(true);
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm"));
            String modifiedDateTime = formattedDateTime.substring(0, formattedDateTime.length() - 1);
            modifiedDateTime += "0";
            String csvFile = "logs_master_" + modifiedDateTime + ".csv";
            try {
                FileWriter fileWriter =new FileWriter(csvFile, true);
                CSVWriter csvWriter = new CSVWriter(fileWriter);
                csvWriter.writeAll(listMessages);
                listMessages.clear();
                csvWriter.close();
                if(basePath == null) {
                    System.out.println("File path: " + new File(csvFile).getAbsolutePath());
                    basePath = new File(csvFile).getAbsolutePath().replace(csvFile,"");
                }
            } catch (Exception ex) {
                ex.printStackTrace(CustomPrintStreamError.getCustomPrintStreamError());
            }
            lock.set(false);
        }
    }
}


