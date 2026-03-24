package org.example.execution_node.code_executor;

import org.example.execution_node.code_executor.exceptions.StopWithException;
import org.example.execution_node.code_executor.implementations.CodeExecutorByteCode;
import org.example.execution_node.code_executor.implementations.CodeExecutorJar;
import org.example.execution_node.code_executor.implementations.CodeExecutorSource;
import org.example.execution_node.code_executor.model.ByteCode;

public class CodeExecutorManager {
    private final CodeExecutorByteCode codeExecutorByteCode;
    private final CodeExecutorSource codeExecutorSource;
    private final CodeExecutorJar codeExecutorJar;

    public CodeExecutorManager(){
        this.codeExecutorByteCode = new CodeExecutorByteCode();
        this.codeExecutorSource = new CodeExecutorSource();
        this.codeExecutorJar = new CodeExecutorJar();
    }

    public void executeByteCode(ByteCode code) throws StopWithException {
            this.codeExecutorByteCode.execute(code);
    }

    public void executeSourceCode(String filePath, String fileName) throws StopWithException {
        this.codeExecutorSource.executeCode(filePath, fileName);
    }

    public void loadByteCode(ByteCode code) { this.codeExecutorByteCode.loadByteCode(code);}

    public void executeJarCode(String filePath,String runClass,String  address,int port,String userId) throws StopWithException {
        this.codeExecutorJar.executeCode(filePath,runClass,address,port,userId);
    }
}
