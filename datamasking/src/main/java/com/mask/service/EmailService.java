package com.mask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        
        String subject = "Password Reset Request - DataMasking Application";
        String body = "Dear User,\n\n" +
                     "You have requested to reset your password for DataMasking Application.\n\n" +
                     "Please click on the following link to reset your password:\n" +
                     resetUrl + "\n\n" +
                     "This link will expire in 1 hour.\n\n" +
                     "If you did not request this password reset, please ignore this email.\n\n" +
                     "Best regards,\n" +
                     "DataMasking Team";
        
        sendEmail(toEmail, subject, body);
    }
    
    public void sendWelcomeEmail(String toEmail, String userName) {
        String subject = "Welcome to DataMasking Application";
        String body = "Dear " + userName + ",\n\n" +
                     "Welcome to DataMasking Application!\n\n" +
                     "Your account has been successfully created. You can now log in and start using our services.\n\n" +
                     "Login URL: " + baseUrl + "/login\n\n" +
                     "Best regards,\n" +
                     "DataMasking Team";
        
        sendEmail(toEmail, subject, body);
    }
    
    private void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + toEmail);
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}