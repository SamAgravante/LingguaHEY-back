package edu.cit.lingguahey.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    
    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name:Lingguahey Team}")
    private String fromName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String name, String verificationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            try {
                helper.setFrom(fromEmail, fromName);
            } catch (java.io.UnsupportedEncodingException e) {
                logger.error("Unsupported encoding for sender name: " + fromName, e);
                throw new RuntimeException("Failed to encode sender name.", e);
            }
            helper.setTo(to);
            helper.setSubject("Verify Your Email for Lingguahey");

            String htmlContent = "<html>"
                    + "<body>"
                    + "<p>Dear " + name + ",</p>"
                    + "<p>Thank you for registering with Lingguahey! Please click the following link to verify your email address:</p>"
                    + "<p><a href=\"" + verificationLink + "\">Verify My Email</a></p>"
                    + "<p>If the link above does not work, copy and paste the following URL into your browser:</p>"
                    + "<p>" + verificationLink + "</p>"
                    + "<p>If you did not register for an account, please ignore this email.</p>"
                    + "<p>Thanks,</p>"
                    + "<p>Lingguahey Team</p>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("Verification email sent successfully to " + to);

        } catch (MailException | MessagingException ex) {
            logger.error("Error sending verification email to " + to + ": " + ex.getMessage(), ex);
            throw new RuntimeException("Failed to send verification email.", ex);
        }
    }
}