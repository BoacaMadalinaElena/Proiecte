package org.example.tcp_management.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageToMaster {
    private String userId;
    private String message;
    private byte[] bytes;
    private String command;
}
