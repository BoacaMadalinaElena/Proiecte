package org.example.dto;

import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class LogsMessageHttpNode {
    @Id
    private String id;

    String localDateTime;

    String address;

    String type;

    String message;
    String typeNode;
}
