package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogsMessageExecutionNodeComparator implements Comparator<LogsMessageExecutionNode> {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public int compare(LogsMessageExecutionNode o1, LogsMessageExecutionNode o2) {
        LocalDateTime dateTime1 = LocalDateTime.parse(o1.getLocalDateTime(), formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(o2.getLocalDateTime(), formatter);

        return dateTime1.compareTo(dateTime2);
    }
}
