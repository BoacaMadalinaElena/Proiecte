package org.example.dto;

import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class LogsMessageExecutionNode {
    @Id
    private String id;

    String localDateTime;

    String address;

    String type;

    String userId;

    String message;
    String typeNode;
}
