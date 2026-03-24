package org.example.service;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class ValidateParameter {
    public boolean isValidUserId(String time) {
        String regex = "^[A-Za-z0-9]+(-[A-Za-z0-9]+)*$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }

    public boolean isValidType(String time) {
        String regex = "^[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }

    public boolean isValidDateTimeFormat(String dateTime) {
        String[] formats = {"dd/MM/yyyy", "dd/MM/yyyy HH", "dd/MM/yyyy HH:mm"};

        for (String format : formats) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);

            try {
                sdf.parse(dateTime);
                return true;
            } catch (ParseException ignored) {
            }
        }

        return true;
    }

    // @Param("address") String address,
    public  boolean isValidIPAddress(String ip) {
        String regex = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(?::\\d{1,5})?$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}

