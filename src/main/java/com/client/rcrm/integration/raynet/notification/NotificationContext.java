package com.client.rcrm.integration.raynet.notification;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationContext {

    private final Map<String, NotificationStrategy> strategies;

    public NotificationContext(Map<String, NotificationStrategy> strategies) {
        this.strategies = strategies;
    }

    public void send(String strategyKey, String emailDetails) {
        NotificationStrategy strategy = strategies.get(strategyKey);
        if (strategy == null) {
            throw new IllegalArgumentException("Notification strategy not found: " + strategyKey);
        }
        strategy.sendNotification(emailDetails);
    }
}
