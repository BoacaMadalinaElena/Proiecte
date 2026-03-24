package org.example.execution_node.conexion_tcp.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageToExecutionNode {
    private String userId;
    private String fileName;
    private String content;
    private int isStartUp;
    private byte[] contentByte;
    private int methodExecution;
}
