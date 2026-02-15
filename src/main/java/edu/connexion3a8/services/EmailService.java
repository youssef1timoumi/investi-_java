package edu.connexion3a8.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String EMAIL_FROM = "6ysfcrybaby9@gmail.com";
    private static final String APP_PASSWORD = "vqds vbpk kbol zjbv";

    private String currentOtp;

    public String generateOtp() {
        currentOtp = String.format("%06d", new Random().nextInt(999999));
        return currentOtp;
    }

    public boolean verifyOtp(String inputOtp) {
        return currentOtp != null && currentOtp.equals(inputOtp);
    }

    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("INVESTI - Email Verification Code");
        message.setContent(
            "<div style='font-family:Arial,sans-serif;max-width:480px;margin:auto;padding:30px;border:1px solid #e0e0e0;border-radius:12px;'>" +
            "<h2 style='color:#456990;text-align:center;'>INVESTI</h2>" +
            "<p style='text-align:center;color:#333;'>Your verification code is:</p>" +
            "<h1 style='text-align:center;letter-spacing:8px;color:#9B7E46;font-size:36px;'>" + otp + "</h1>" +
            "<p style='text-align:center;color:#888;font-size:13px;'>This code expires in 5 minutes.</p>" +
            "</div>",
            "text/html; charset=utf-8"
        );

        Transport.send(message);
    }
}
