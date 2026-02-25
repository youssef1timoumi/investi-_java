package edu.collaboration.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * EmailService — sends email notifications via Gmail SMTP.
 *
 * ⚠️ SETUP: Replace SENDER_EMAIL and SENDER_PASSWORD with your Gmail
 * credentials.
 * For Gmail, use an "App Password" (not your regular password):
 * -> Google Account -> Security -> 2-Step Verification -> App Passwords
 *
 * This service is called on:
 * - Investment ACCEPTED (email to entrepreneur + investor)
 * - Investment REFUSED (email to investor)
 * - Project VALIDATED (email to entrepreneur)
 */
public class EmailService {

    // ─── CONFIG: Change these to real values ───────────────────────────────────
    private static final String SENDER_EMAIL = "investi.platform@gmail.com";
    private static final String SENDER_PASSWORD = "vtvg crqw cuxn okjn"; // Authenticated App Password
    private static final boolean EMAIL_ENABLED = true; // Actively sending emails now
    // ──────────────────────────────────────────────────────────────────────────

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
    }

    /**
     * Core method — sends an email. Silently logs if EMAIL_ENABLED is false.
     */
    private static void send(String toEmail, String subject, String body) {
        String overrideEmail = "ninmenin@gmail.com"; // Requested by user
        if (!EMAIL_ENABLED) {
            System.out.println("[EmailService] (Disabled) Would send email to: " + overrideEmail
                    + " (Originally intended for: " + toEmail + ")");
            System.out.println("  Subject: " + subject);
            System.out.println("  Body: " + body);
            return;
        }
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Investi Platform"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(overrideEmail));
            message.setSubject(subject);

            // HTML body
            message.setContent(buildHtmlBody(subject, body), "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("[EmailService] Email sent to: " + overrideEmail);

        } catch (Exception e) {
            System.err.println("[EmailService] Failed to send email: " + e.getMessage());
        }
    }

    private static String buildHtmlBody(String title, String message) {
        return "<!DOCTYPE html><html><body style=\"margin:0;padding:0;background-color:#F7F0F5;font-family:'Helvetica Neue', Helvetica, Arial, sans-serif;\">"
                + "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color:#F7F0F5;padding:40px 20px;\">"
                + "<tr><td align=\"center\">"
                + "<table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color:#ffffff;border-radius:16px;box-shadow:0 10px 25px rgba(0,0,0,0.05);overflow:hidden;\">"
                + "<!-- Header -->"
                + "<tr><td style=\"background:linear-gradient(135deg, #456990 0%, #304a66 100%);padding:40px;text-align:center;\">"
                + "<h1 style=\"color:#ffffff;margin:0;font-size:28px;letter-spacing:1px;font-weight:700;\">INVESTI</h1>"
                + "<p style=\"color:#c2d6eb;margin:10px 0 0 0;font-size:14px;letter-spacing:2px;text-transform:uppercase;\">Premium Platform</p>"
                + "</td></tr>"
                + "<!-- Content -->"
                + "<tr><td style=\"padding:40px;\">"
                + "<h2 style=\"color:#000501;font-size:22px;margin:0 0 20px 0;\">" + title + "</h2>"
                + "<p style=\"color:#64748b;font-size:16px;line-height:1.6;margin:0 0 30px 0;\">" + message + "</p>"
                + "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td align=\"center\">"
                + "<a href=\"#\" style=\"background-color:#A62639;color:#ffffff;text-decoration:none;padding:14px 32px;border-radius:30px;font-weight:bold;font-size:14px;text-transform:uppercase;letter-spacing:1px;display:inline-block;\">Log in to Investi</a>"
                + "</td></tr></table>"
                + "</td></tr>"
                + "<!-- Footer -->"
                + "<tr><td style=\"background-color:#f8fafc;padding:30px;text-align:center;border-top:1px solid #e2e8f0;\">"
                + "<p style=\"color:#94a3b8;font-size:12px;margin:0;\">This is an automated notification from Investi Platform.</p>"
                + "<p style=\"color:#94a3b8;font-size:12px;margin:10px 0 0 0;\">&copy; 2026 Investi. All rights reserved.</p>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }

    // ─── Public notification methods ────────────────────────────────────────────

    /**
     * Called when an entrepreneur ACCEPTS an investment offer.
     */
    public static void sendInvestmentAccepted(String entrepreneurEmail, String investorEmail,
            String projectTitle, double amount) {
        // Email to entrepreneur
        send(entrepreneurEmail,
                "🎉 Investment Accepted — " + projectTitle,
                "Congratulations! You have accepted an investment of <strong>$" + String.format("%.2f", amount)
                        + "</strong> for your project <strong>" + projectTitle + "</strong>. "
                        + "Your project is now marked as <strong>FUNDED</strong>.");

        // Email to investor
        send(investorEmail,
                "✅ Your Investment Was Accepted — " + projectTitle,
                "Great news! Your investment of <strong>$" + String.format("%.2f", amount)
                        + "</strong> in the project <strong>" + projectTitle
                        + "</strong> has been <strong>ACCEPTED</strong> by the entrepreneur.");
    }

    /**
     * Called when an entrepreneur REFUSES an investment offer.
     */
    public static void sendInvestmentRefused(String investorEmail, String projectTitle, double amount) {
        send(investorEmail,
                "❌ Investment Offer Declined — " + projectTitle,
                "Unfortunately, your investment offer of <strong>$" + String.format("%.2f", amount)
                        + "</strong> for project <strong>" + projectTitle
                        + "</strong> was not accepted at this time. "
                        + "Keep exploring other opportunities on Investi!");
    }

    /**
     * Called when an admin VALIDATES a project (status -> OPEN).
     */
    public static void sendProjectValidated(String entrepreneurEmail, String projectTitle) {
        send(entrepreneurEmail,
                "✅ Project Validated — " + projectTitle,
                "Your project <strong>" + projectTitle + "</strong> has been reviewed and approved by our admin team. "
                        + "It is now <strong>OPEN</strong> for investors to discover and fund!");
    }

    /**
     * Called when an admin REJECTS a project.
     */
    public static void sendProjectRejected(String entrepreneurEmail, String projectTitle) {
        send(entrepreneurEmail,
                "❌ Project Rejected — " + projectTitle,
                "Unfortunately, your project <strong>" + projectTitle
                        + "</strong> was not approved in its current form. "
                        + "Please review our guidelines and consider resubmitting with improvements.");
    }

    /**
     * Called when an investor CREATES a new investment offer.
     */
    public static void sendNewInvestmentOffer(String entrepreneurEmail, String projectTitle, double amount) {
        send(entrepreneurEmail,
                "💰 New Investment Offer Received! — " + projectTitle,
                "Great news! Your project <strong>" + projectTitle
                        + "</strong> just received a new investment offer of <strong>$"
                        + String.format("%.2f", amount) + "</strong>. "
                        + "<br><br>Log in to your Investi dashboard to review and accept or decline the offer.");
    }
}
