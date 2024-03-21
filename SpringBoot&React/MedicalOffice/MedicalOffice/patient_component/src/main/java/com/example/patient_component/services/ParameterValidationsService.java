package com.example.patient_component.services;

import com.example.patient_component.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
public class ParameterValidationsService {
    public boolean isValidCNP(String cnp){
        return  Pattern.matches("\\d{13}", cnp);
    }

    public  boolean isValidName(String input) {
        return Pattern.matches("[a-zA-Z]+", input.replace(" ",""));
    }

    public  boolean isValidTelephone(String input) {
        return Pattern.matches("^(07\\d{8}|02\\d{8})$", input);
    }

    public  boolean isValidEmail(String input) {
        return Pattern.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", input);
    }

    public LocalDate isValidDate(String date){
        String expectedFormat = "yyyy-MM-dd";
        LocalDate birthDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
            LocalDate.parse(date, formatter);
            birthDate = LocalDate.parse(date);
            return birthDate;
        } catch (Exception ex) {
            throw new IllegalArgumentException();
        }

    }
}
