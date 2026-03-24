package org.example.exception;

import lombok.Getter;

@Getter
public class InvalidFieldException extends Exception{
    private final String errorRo;
    private final String errorEng;

    public InvalidFieldException(String ro, String eng){
        this.errorEng = eng;
        this.errorRo = ro;
    }
}
