package com.client.rcrm.integration.raynet.notification;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationContext {

    private final Map<NotificationType, NotificationStrategy> strategies;

    public NotificationContext(Map<NotificationType, NotificationStrategy> strategies) {
        this.strategies = strategies;
    }

    public void send(NotificationType notificationType, String message) {
        NotificationStrategy strategy = strategies.get(notificationType);
        if (strategy == null) {
            throw new IllegalArgumentException("Notification type not found: " + notificationType);
        }
        strategy.sendNotification(message);
    }
}
