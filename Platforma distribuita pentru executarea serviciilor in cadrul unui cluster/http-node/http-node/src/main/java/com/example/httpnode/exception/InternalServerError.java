package com.example.httpnode.exception;

import lombok.Getter;

@Getter
public class InternalServerError extends Exception{
    private final String errorRo;
    private final String errorEng;

    public InternalServerError(String ro,String eng){
        this.errorEng = eng;
        this.errorRo = ro;
    }
}
