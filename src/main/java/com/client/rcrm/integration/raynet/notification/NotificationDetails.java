package com.client.rcrm.integration.raynet.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NotificationDetails {

    @JsonIgnore
    private String recipient;
    private String msgBody;
    private String subject;
    @JsonIgnore
    private List<String> attachments;

    public NotificationDetails() {
        this.attachments = new ArrayList<>();
    }

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
