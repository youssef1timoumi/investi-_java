package services;

import models.Product;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static final String SENDER_EMAIL = "touilmoezzz@gmail.com";
    private static final String SENDER_PASSWORD = "xqob ztln unqu ybla";
    private static final String TARGET_EMAIL = "touilmoez358@gmail.com";

    public static void sendDiscountEmail(Product product) {
        if (product.getRemise() <= 0)
            return;

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // TLS

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(TARGET_EMAIL));
            message.setSubject("Une remise a été appliquée sur : " + product.getName());

            String text = "Une remise de " + product.getRemise() + "% a été appliquée sur le produit "
                    + product.getName() + ".\n\n" +
                    "Le nouveau prix est de : " + calculateDiscountedPrice(product) + " " + product.getCurrency()
                    + "\n\n" +
                    "Merci.";

            message.setText(text);

            Transport.send(message);

            System.out.println("[EmailService] Email de notification de remise envoyé avec succès.");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("[EmailService] Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    private static double calculateDiscountedPrice(Product p) {
        return p.getPrice() * (1 - (p.getRemise() / 100.0));
    }
}
