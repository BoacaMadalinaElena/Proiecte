package com.example.appointmentcomponent.service;

import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Pattern;

@Service
public class ParameterValidationsService {
    public LocalDateTime isValidDate(String date){
        String expectedFormat = "yyyy-MM-dd HH:mm";
        try {
            String decodedUrl = URLDecoder.decode(date, StandardCharsets.UTF_8.toString());
            System.out.println("URL decodat: " + decodedUrl);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
            LocalDateTime parsedDate = LocalDateTime.parse(decodedUrl, formatter);
            return parsedDate;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Data furnizată nu este validă.", ex);
        }
    }

    public  boolean isValidName(String input) {
        return input == null || Pattern.matches("[a-zA-Z]+", input.replace(" ",""));
    }
}
