package com.example.consultation_component.example.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
public class ParameterValidationsService {
    public  boolean isValidDiagnostic(String input) {
        if(input == null)
            return false;
        return Pattern.matches("^[a-zA-Z0-9\\.,\\-]+$", input.replace(" ",""));
    }
    public  boolean isValidName(String input) {
        if(input == null)
            return false;
        return Pattern.matches("^[a-zA-Z0-9\\.,\\-]+$", input.replace(" ",""));
    }
    public  boolean isValidResult(String input) {
        if(input == null)
            return true;
        return Pattern.matches("^[a-zA-Z0-9\\.,\\-]+$", input.replace(" ",""));
    }
    public LocalDateTime isValidDate(String date){
        String expectedFormat = "yyyy-MM-dd HH:mm";
        LocalDateTime birthDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
            LocalDateTime parsedDate = LocalDateTime.parse(date, formatter);
            return parsedDate;
        } catch (Exception ex) {
            throw new IllegalArgumentException();
        }

    }
}
