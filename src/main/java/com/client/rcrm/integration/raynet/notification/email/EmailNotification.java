package com.client.rcrm.integration.raynet.notification.email;

import com.client.rcrm.integration.raynet.batch.exception.InvalidEmailException;
import com.client.rcrm.integration.raynet.batch.validation.ValidationService;
import com.client.rcrm.integration.raynet.notification.NotificationDetails;
import com.client.rcrm.integration.raynet.notification.NotificationStrategy;
import com.client.rcrm.integration.raynet.notification.exception.EmailNotSendException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;

@Component
public class EmailNotification implements NotificationStrategy {

    private final JavaMailSender javaMailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TemplateEngine templateEngine;
    private final ValidationService validationService;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${raynet.username}")
    private String recipient;

    public EmailNotification(JavaMailSender javaMailSender, TemplateEngine templateEngine, ValidationService validationService) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.validationService = validationService;
    }

    @Override
    public void sendNotification(String emailDetails) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        this.isEmailValid(sender);
        this.isEmailValid(recipient);

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            ClassPathResource classPathResource = new ClassPathResource("static/email_success.json");

            try (InputStream inputStream = classPathResource.getInputStream()) {
                NotificationDetails notificationDetails = objectMapper.readValue(inputStream, NotificationDetails.class);

                mimeMessageHelper.setFrom(sender);
                mimeMessageHelper.setTo(recipient);
                mimeMessageHelper.setSubject(notificationDetails.getSubject());

                Context context = new Context();
                context.setVariable("username", recipient);
                context.setVariable("content", notificationDetails.getMsgBody());

                String processedString = templateEngine.process("mail_template", context);
                mimeMessageHelper.setText(processedString, true);

                javaMailSender.send(mimeMessage);
            }

        } catch (MessagingException e) {
            throw new EmailNotSendException("Email wasn't sent due to messaging error: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Error reading or parsing the email details: " + e.getMessage(), e);
        }
    }

    private void isEmailValid(String mail) {
        if (!this.validationService.isEmailValid(mail)) {
            throw new InvalidEmailException("Invalid email for " + mail);
        }
    }
}

