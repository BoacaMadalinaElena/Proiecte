package org.example.dto;

import org.springframework.data.annotation.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class LogsMessageMasterNode {
    @Id
    private String id;

    String localDateTime;

    String address;

    String type;

    String message;
    String typeNode;
}
