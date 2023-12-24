package com.example.busticketplatform.service.impl;

import com.example.busticketplatform.entity.Notification;
import com.example.busticketplatform.service.NotificationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

//@Service
public class ConsoleNotificationService implements NotificationService {
    private static final Logger log = getLogger(ConsoleNotificationService.class);
    private final Queue<String> notificationMessageQueue = new ConcurrentLinkedQueue<>();
    private final Map<Long, Notification> cachedNotifications = new ConcurrentHashMap<>();

    @Override
    public void sendOrderNotification(Notification notification) {
        if (cachedNotifications.containsKey(notification.getOrder().getId())) {
            Notification cachedNotification = cachedNotifications.get(notification.getOrder().getId());
            if (cachedNotification.getAvailableRides().equals(notification.getAvailableRides())) {
                return;
            }
        }
        StringBuilder message = new StringBuilder("Available Rides by criteria: ").append(notification.getAvailableRides().size());
        appendNewLine(message);
        message.append(notification.getOrder().getRideFilter());
        appendNewLine(message);
        notification.getAvailableRides().forEach(ride -> appendSeparationLine(message.append(ride)));

        cachedNotifications.put(notification.getOrder().getId(), notification);
        notificationMessageQueue.add(notification.toString());
    }

    @Override
    public String pickNotifications() {
        ScheduledExecutorService notificationExecutor = Executors.newScheduledThreadPool(5);

        notificationExecutor.scheduleWithFixedDelay(() -> {
            if (notificationMessageQueue.isEmpty()) {
                notificationExecutor.shutdown();
            }
            String notification = notificationMessageQueue.poll();
            if (StringUtils.isBlank(notification)) {
                return;
            }

            log.info(notification);
        }, 0L, 1L, TimeUnit.MILLISECONDS);
        return "";
    }

    @Override
    public boolean containsNotifications() {
        return !notificationMessageQueue.isEmpty();
    }

    @Override
    public void addNotificator(long chatId, Consumer<Notification> notification) {

    }

    @Override
    public void closeNotification(long chatId) {

    }

    private void appendSeparationLine(StringBuilder message) {
        message.append("===============");
    }

    private void appendNewLine(StringBuilder message) {
        message.append("\n");
    }

}
