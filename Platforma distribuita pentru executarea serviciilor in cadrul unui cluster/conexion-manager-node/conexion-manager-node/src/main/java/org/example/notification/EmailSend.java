package org.example.notification;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.other.CustomPrinter;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Session;
import javax.mail.Transport;

public class EmailSend {
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

    public static void send(String address, String subject, String body) {
        try {
            MimeMessage message = new MimeMessage(session);

            String myAddress = "materialefacultate0@gmail.com";
            message.setFrom(new InternetAddress(myAddress));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));

            message.setSubject(subject);

            message.setContent("<p>Hello,</p><br>" +
                    "<p>" + body + "</p><br>" +
                    "<p>With pleasure,</p>" +
                    "<p>Team ClusterServExe</p>", "text/html");

            Transport.send(message);
            CustomPrinter.printInfo("Mail successfully sent");
        } catch (MessagingException mex) {
            String stacktrace = ExceptionUtils.getStackTrace(mex);
            CustomPrinter.printErr(stacktrace);
        }
    }
}
