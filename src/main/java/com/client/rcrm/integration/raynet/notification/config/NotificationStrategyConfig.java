package com.client.rcrm.integration.raynet.notification.config;

import com.client.rcrm.integration.raynet.notification.NotificationStrategy;
import com.client.rcrm.integration.raynet.notification.NotificationType;
import com.client.rcrm.integration.raynet.notification.email.EmailNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class NotificationStrategyConfig {


    @Bean
    public Map<NotificationType, NotificationStrategy> notificationStrategies(
            EmailNotification emailStrategy) {
        return Map.of(
                NotificationType.EMAIL, emailStrategy
        );
    }
}
