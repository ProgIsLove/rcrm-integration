package com.client.rcrm.integration.raynet.notification.email;

import com.client.rcrm.integration.raynet.notification.NotificationDetails;
import com.client.rcrm.integration.raynet.notification.NotificationStrategy;
import com.client.rcrm.integration.raynet.notification.exception.EmailNotSendException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("SimpleEmailNotification")
public class SimpleEmailNotification implements NotificationStrategy {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public SimpleEmailNotification(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendNotification(NotificationDetails details) {
        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
        }

        catch (MailException e) {
            throw new EmailNotSendException("Email wasn't send");
        }
    }
}
