package com.example.httpnode.service;

import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParameterValidateService {
    public boolean usernameRegex(String username){
        String regexPattern = "^[a-zA-Z0-9.-]{5,100}$";
        return this.patternMatches(username,regexPattern);
    }

    //"email": "string",
    public boolean emailValidate(String email){
        String  regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return this.patternMatches(email,regexPattern);
    }

    //"password": "string",
    public boolean passwordValidate(String password){
        String regexPattern = "^[a-zA-Z0-9.-]{5,100}$";
        return this.patternMatches(password,regexPattern);
    }

    public  boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public  boolean isValidUUID(String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean name(String username) {
        String regexPattern = "^[a-zA-Z]{5,100}$";
        return username.matches(regexPattern);
    }
}
