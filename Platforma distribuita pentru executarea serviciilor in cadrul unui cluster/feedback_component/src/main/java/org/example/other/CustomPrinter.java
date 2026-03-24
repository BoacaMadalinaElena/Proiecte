package org.example.other;

import com.opencsv.CSVWriter;
import lombok.Getter;
import lombok.Setter;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomPrinter {
    private static final List<String[]> listMessages = new ArrayList<>();
    @Setter
    @Getter
    private static int currentIdFile;
    @Getter
    private static AtomicBoolean lock = new AtomicBoolean(false);


    public static void printInfo(String input) {
        insertRecordInCSV(new String[]{LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        "INFO",
                        input
                }
        );
        synchronizedPrint(
                ColorPrint.BLUE
                        + "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t"
                        + String.format("%12s", "INFO") + "\t"
                        + input + "\n" +
                        ColorPrint.RESET
        );
    }

    public static void printWarning(String input) {
        insertRecordInCSV(new String[]
                {
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        "WARNING",
                        input
                }
        );
        synchronizedPrint(
                ColorPrint.YELLOW +
                        "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" +
                        String.format("%12s", "WARNING") + "\t"
                        + input + "\n" +
                        ColorPrint.RESET
        );
    }

    public static void printErr(String input) {
        insertRecordInCSV(new String[]{
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        "ERROR",
                        input
                }
        );
        synchronizedPrint(
                ColorPrint.RED +
                        "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" +
                        String.format("%12s", "ERROR") + "\t" +
                        input + "\n" +
                        ColorPrint.RESET
        );
    }

    public static void printSuccess(String input) {
        insertRecordInCSV(new String[]{
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        "SUCCESS",
                        input
                }
        );
        synchronizedPrint(
                ColorPrint.GREEN +
                        "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" +
                        String.format("%12s", "SUCCESS") + "\t" +
                        input + "\n" +
                        ColorPrint.RESET
        );
    }

    public static void printNormal(String input) {
        insertRecordInCSV(new String[]{
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        "MESSAGE",
                        input
                }
        );
        synchronizedPrint(
                ColorPrint.WHITE +
                        "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]\t" +
                        String.format("%12s", "MESSAGE") + "\t" +
                        input + "\n" +
                        ColorPrint.RESET
        );
    }

    private static void synchronizedPrint(String s) {
        synchronized (System.out) {
            System.out.print(s);
        }
    }


    private static void insertRecordInCSV(String[] logs) {
        synchronized (listMessages) {
            listMessages.add(logs);
            if (listMessages.size() == 10) {
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
        String csvFile = "logs_http_" + modifiedDateTime + ".csv";
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

    // el are un fisier pe fiecare ora, in ora urmatoare el il citeste si trimite
}
