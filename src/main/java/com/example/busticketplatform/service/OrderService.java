package com.example.busticketplatform.service;

import com.example.busticketplatform.entity.Order;
import com.example.busticketplatform.entity.Ride;

import java.util.List;

public interface OrderService {

    Order placeOrder(Order order);

    void pickOrders();

    void sendOrder(Order order, List<Ride> rides);

}
