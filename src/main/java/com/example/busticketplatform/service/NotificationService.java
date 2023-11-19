package com.example.busticketplatform.service;

import com.example.busticketplatform.entity.Notification;
import com.example.busticketplatform.entity.Order;
import com.example.busticketplatform.entity.Ride;

import java.util.List;

public interface NotificationService {

    void sendOrderNotification(Notification notification);

    void pickNotifications();

    boolean containsNotifications();

}
