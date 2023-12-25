package com.example.busticketplatform.scheduling;

import com.example.busticketplatform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationsSchedulingService {

    private final List<NotificationService> notificationService;

    @Scheduled(fixedDelay = 30000)
    public void scheduleNotifications() {
        notificationService.stream()
              .filter(NotificationService::containsNotifications)
              .forEach(NotificationService::pickNotifications);
    }

}
