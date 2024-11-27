package com.client.rcrm.integration.raynet.notification.email;

import com.client.rcrm.integration.raynet.notification.NotificationDetails;
import com.client.rcrm.integration.raynet.notification.NotificationStrategy;
import com.client.rcrm.integration.raynet.notification.exception.EmailNotSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("EmailWithAttachmentNotification")
public class EmailWithAttachmentNotification implements NotificationStrategy {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public EmailWithAttachmentNotification(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendNotification(NotificationDetails details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());

            ClassPathResource resource = new ClassPathResource("data/upload-12077951414892126590-contact.csv");
            details.addAttachment(Objects.requireNonNull(resource.getFilename()));

            for(String attachment : details.getAttachments()) {
                mimeMessageHelper.addAttachment(Objects.requireNonNull(resource.getFilename()), resource);
            }

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailNotSendException("Email wasn't send");
        }
    }
}
