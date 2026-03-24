package com.example.httpnode.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends Exception {
    private final String errorRo;
    private final String errorEng;

    public UnauthorizedException(String ro,String eng){
        this.errorEng = eng;
        this.errorRo = ro;
    }
}
