package com.example.busticketplatform.service.impl;

import com.example.busticketplatform.entity.Notification;
import com.example.busticketplatform.service.NotificationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class TelegramNotificationService implements NotificationService {

    private final Map<Long, Notification> notifications = new ConcurrentHashMap<>();
    private final Map<Long, Consumer<Notification>> notificationConsumer = new ConcurrentHashMap<>();
    private final ExecutorService notificator = Executors.newCachedThreadPool();

    @Override
    public void sendOrderNotification(Notification notification) {
        notifications.put(notification.getOrder().getCustomerId(), notification);
    }

    @Override
    public String pickNotifications() {
        for (Map.Entry<Long, Notification> notificationEntry : notifications.entrySet()) {
            notificator.submit(() -> {
                if (CollectionUtils.isEmpty(notificationEntry.getValue().getAvailableRides())) {
                    return;
                }
                Consumer<Notification> consumer = notificationConsumer.get(notificationEntry.getKey());
                if (consumer != null) {
                    consumer.accept(notificationEntry.getValue());
                }
            });
        }
        return "";
    }

    @Override
    public boolean containsNotifications() {
        return MapUtils.isNotEmpty(notifications);
    }

    @Override
    public void addNotificator(long chatId, Consumer<Notification> notification) {
        notificationConsumer.put(chatId, notification);
    }

    @Override
    public void closeNotification(long chatId) {
        notificationConsumer.remove(chatId);
        notifications.remove(chatId);
    }


}
