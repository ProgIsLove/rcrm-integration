package com.client.rcrm.integration.raynet.notification;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NotificationDetails {

    private String recipient;
    private String msgBody;
    private String subject;
    private List<String> attachments;

    public NotificationDetails(String recipient, String msgBody, String subject) {
        this.recipient = recipient;
        this.msgBody = msgBody;
        this.subject = subject;
        this.attachments = new ArrayList<>();
    }

    public void addAttachment(String pathToAttachment) {
        this.attachments.add(pathToAttachment);
    }
}
