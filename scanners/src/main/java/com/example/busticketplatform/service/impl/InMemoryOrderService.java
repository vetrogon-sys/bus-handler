package com.example.busticketplatform.service.impl;

import com.example.busticketplatform.entity.Notification;
import com.example.busticketplatform.entity.Order;
import com.example.busticketplatform.entity.Ride;
import com.example.busticketplatform.service.NotificationService;
import com.example.busticketplatform.service.OrderService;
import com.example.busticketplatform.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class InMemoryOrderService implements OrderService {

    private final RideService rideService;
    private final NotificationService notificationService;

    private final Map<Long, Order> orderMap = new ConcurrentHashMap<>();
    private final Map<Long, Set<Ride>> restrictedRides = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public Order placeOrder(Order order) {
        long orderId = idCounter.incrementAndGet();
        order.setId(orderId);
        order.setPostingDate(System.currentTimeMillis());
        orderMap.put(orderId, order);
        return order;
    }

    @Override
    public void pickOrders() {
        orderMap.values().forEach(order -> {
            List<Ride> rides = rideService.getRides(order.getSource(), order.getRideFilter())
                  .stream()
                  .filter(ride -> !restrictedRides.getOrDefault(order.getCustomerId(), Set.of()).contains(ride))
                  .limit(2)
                  .toList();
            restrictedRides.computeIfAbsent(order.getCustomerId(), s -> new HashSet<>())
                        .addAll(rides);
            sendOrder(order, rides);
        });
    }

    @Override
    public void sendOrder(Order order, List<Ride> rides) {
        notificationService.sendOrderNotification(new Notification(order, rides));
    }

    @Override
    public void invalidateOrder(long chatId) {
        orderMap.remove(chatId);
        restrictedRides.remove(chatId);
    }

}
