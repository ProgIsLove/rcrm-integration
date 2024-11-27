package com.client.rcrm.integration.raynet.notification;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public String sendNotification(
            @RequestParam String channel,
            @RequestBody NotificationDetails details
    ) {
        notificationService.sendNotification(channel, details);
        return "Notification sent via " + channel;
    }
}
