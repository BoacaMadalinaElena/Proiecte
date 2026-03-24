package org.example.other;

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

    // send json
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
}
