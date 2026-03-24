package org.example.tcp_management.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageToFront {
    private String idUser;
    private boolean read;
    private String message;
    private byte[] bytes;
    private boolean isFile;
    private int statusCode;

    public MessageToFront(String idUser, boolean read, String message) {
        this.idUser = idUser;
        this.read = read;
        this.message = message;
        this.bytes = null;
        this.isFile = false;
    }
}
