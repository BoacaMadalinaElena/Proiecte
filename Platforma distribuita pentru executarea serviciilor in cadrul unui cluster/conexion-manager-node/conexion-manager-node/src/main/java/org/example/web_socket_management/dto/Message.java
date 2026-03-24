package org.example.web_socket_management.dto;

import lombok.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    private String from;
    private String content;
    private boolean isRequest;
    private List<CodeMessage> listOfCodes;
    private boolean isReadRequest;
    private int statusCode;
    private boolean isFileResponse;
    private String fileName;
    private byte[] contentFile;
    private MessageFrame messageFrame;
    private String idCode;
    private int type;

    public Message(String from, String content, boolean isRequest, List<CodeMessage> listOfCodes, boolean isReadRequest, int statusCode,int type) {
        this.from = from;
        this.content = content;
        this.isRequest = isRequest;
        this.listOfCodes = listOfCodes;
        this.isReadRequest = isReadRequest;
        this.statusCode = statusCode;
        this.isFileResponse = false;
        this.fileName = null;
        this.contentFile = null;
        this.type = type;
    }

    public Message(MessageFrame messageFrame) {
        this.from = null;
        this.content = null;
        this.isRequest = false;
        this.listOfCodes = null;
        this.isReadRequest = false;
        this.statusCode = 200;
        this.isFileResponse = true;
        this.fileName = null;
        this.contentFile = null;
        this.messageFrame = messageFrame;

    }
}