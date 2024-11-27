package com.client.rcrm.integration.raynet.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationContext notificationContext;

    public NotificationService(NotificationContext notificationContext) {
        this.notificationContext = notificationContext;
    }

    public void sendNotification(String channel, NotificationDetails details) {
        notificationContext.send(channel, details);
    }
}
