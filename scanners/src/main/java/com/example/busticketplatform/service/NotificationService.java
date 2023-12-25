package com.example.busticketplatform.service;

import com.example.busticketplatform.entity.Notification;

import java.util.function.Consumer;

public interface NotificationService {

    void sendOrderNotification(Notification notification);

    String pickNotifications();

    boolean containsNotifications();

    void addNotificator(long chatId, Consumer<Notification> notification);

    void closeNotification(long chatId);

}
