package org.example.web_socket_management.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CodeMessage {
    private String fileName;
    private String contentString;
    private String isStartUp;
    private String isCodeClass;
    private byte[] contentBytes;
    private int type;
}
