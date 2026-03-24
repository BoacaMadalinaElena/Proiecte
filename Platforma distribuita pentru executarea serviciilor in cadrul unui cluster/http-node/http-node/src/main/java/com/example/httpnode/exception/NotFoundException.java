package com.example.httpnode.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends Exception{
    private final String errorRo;
    private final String errorEng;

    public NotFoundException(String ro,String eng){
        this.errorEng = eng;
        this.errorRo = ro;
    }
}
