package com.client.rcrm.integration.raynet.notification.email;

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
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;

@Service("EmailWithAttachmentNotification")
public class EmailWithAttachmentNotification implements NotificationStrategy {

    private final JavaMailSender javaMailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${raynet.username}")
    private String recipient;

    public EmailWithAttachmentNotification(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendNotification(String emailDetails) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            ClassPathResource classPathResource = new ClassPathResource("static/email_success.json");

            try (InputStream inputStream = classPathResource.getInputStream()) {
                NotificationDetails notificationDetails = objectMapper.readValue(inputStream, NotificationDetails.class);

                // Set email details
                mimeMessageHelper.setFrom(sender);
                mimeMessageHelper.setTo(recipient);
                mimeMessageHelper.setSubject(notificationDetails.getSubject());

                // Set the content using Thymeleaf templates
                Context context = new Context();
                context.setVariable("username", recipient);
                context.setVariable("content", notificationDetails.getMsgBody());

                String processedString = templateEngine.process("mail_template", context);
                mimeMessageHelper.setText(processedString, true);

                // Send email
                javaMailSender.send(mimeMessage);
            }

        } catch (MessagingException e) {
            throw new EmailNotSendException("Email wasn't sent due to messaging error: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Error reading or parsing the email details: " + e.getMessage(), e);
        }
    }
}

