package com.example.httpnode.exception;

import lombok.Getter;

@Getter
public class ConflictException extends Exception{
    private final String errorRo;
    private final String errorEng;

    public ConflictException(String ro,String eng){
        this.errorEng = eng;
        this.errorRo = ro;
    }
}
