package com.example.httpnode.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import java.util.Objects;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Session;
import com.example.httpnode.other.CustomPrinter;
import org.springframework.stereotype.Service;

import javax.mail.Transport;

@Service
public class EmailService {
    private static final Session session;

    static {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");


        session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("materialefacultate0@gmail.com", "tzbs wpqs tzap silv");
            }
        });
    }

    public static void send(String address, String subject, String body, String language) {
        try {
            MimeMessage message = new MimeMessage(session);

            String myAddress = "materialefacultate0@gmail.com";
            message.setFrom(new InternetAddress(myAddress));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
            message.setSubject(subject);

            if (Objects.equals(language, "ro")) {
                message.setContent(body, "text/plain; charset=utf-8");
                message.setContentLanguage(new String[] {"ro"});
            } else {
                message.setHeader("Content-Language", "en");
                message.setContent(body, "text/plain; charset=utf-8");
            }
            message.setHeader("Content-Type", "text/html; charset=UTF-8");
            Transport.send(message);
            CustomPrinter.printInfo("Mail successfully sent");
        } catch (MessagingException mex) {
            String stacktrace = ExceptionUtils.getStackTrace(mex);
            CustomPrinter.printErr(stacktrace);
        }
    }
}
