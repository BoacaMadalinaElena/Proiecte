package com.example.physician_component.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
public class ParameterValidationsService {
    public  boolean isValidSpecialization(String input) {
        return Pattern.matches("[a-zA-Z]+", input.replace(" ",""));
    }
    public  boolean isValidName(String input) {
        return Pattern.matches("[a-zA-Z]+", input.replace(" ",""));
    }
    public  boolean isValidTelephone(String input) {
        return Pattern.matches("^(07\\d{8}|02\\d{8})$", input);
    }
    public  boolean isValidEmail(String input) {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", input);
    }

}
