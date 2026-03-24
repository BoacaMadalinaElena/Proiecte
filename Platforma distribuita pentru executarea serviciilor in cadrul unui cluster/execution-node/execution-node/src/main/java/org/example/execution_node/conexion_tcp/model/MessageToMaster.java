package org.example.execution_node.conexion_tcp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
