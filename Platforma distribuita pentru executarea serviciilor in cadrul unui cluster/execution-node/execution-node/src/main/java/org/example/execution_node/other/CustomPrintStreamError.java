package org.example.execution_node.other;

import lombok.Getter;
import java.io.OutputStream;
import java.io.PrintStream;

public class CustomPrintStreamError extends PrintStream {
    public CustomPrintStreamError(OutputStream out) {
      super(out);
    }
    @Getter
    private static CustomPrintStreamError customPrintStreamError;

    static {
        customPrintStreamError = new CustomPrintStreamError(System.out);
    }

    @Override
    public void print(String x){
        CustomPrinter.printErr(x);
    }

    @Override
    public void print(int x){
        print(x + "");
    }

    @Override
    public void print(boolean x){
        print(x + "");
    }

    @Override
    public void print(long x){
        print(x + "");
    }

    public void printSuper(String s){
            super.print(s);
    }

    public void addDirectMessage(String s){
        this.printSuper(s);
    }
}
