package org.example.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class LogsMessageHttpNodeComparator implements Comparator<LogsMessageHttpNode> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public int compare(LogsMessageHttpNode o1, LogsMessageHttpNode o2) {
        LocalDateTime dateTime1 = LocalDateTime.parse(o1.getLocalDateTime(), formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(o2.getLocalDateTime(), formatter);

        return dateTime1.compareTo(dateTime2);
    }
}