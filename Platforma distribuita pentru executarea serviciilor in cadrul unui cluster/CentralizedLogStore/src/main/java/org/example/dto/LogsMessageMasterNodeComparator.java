package org.example.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class LogsMessageMasterNodeComparator implements Comparator<LogsMessageMasterNode> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public int compare(LogsMessageMasterNode o1, LogsMessageMasterNode o2) {
        LocalDateTime dateTime1 = LocalDateTime.parse(o1.getLocalDateTime(), formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(o2.getLocalDateTime(), formatter);

        return dateTime1.compareTo(dateTime2);
    }
}