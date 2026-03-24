package org.example.sendLogs.model;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogsMessage {
    private String id;

    String localDateTime;

    String address;

    String type;

    String message;
    String typeNode = "Feedback";
}
