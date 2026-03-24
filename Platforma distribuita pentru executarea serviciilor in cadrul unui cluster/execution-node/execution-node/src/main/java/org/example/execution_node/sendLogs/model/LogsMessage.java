package org.example.execution_node.sendLogs.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LogsMessage {
    private String id;

    String localDateTime;

    String address;

    String type;

    String userId;

    String message;
}
