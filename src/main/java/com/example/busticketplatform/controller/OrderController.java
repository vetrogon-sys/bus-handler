package com.example.busticketplatform.controller;

import com.example.busticketplatform.entity.Order;
import com.example.busticketplatform.serialize.Source;
import com.example.busticketplatform.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{source}")
    public ResponseEntity<Order> placeOrder(@PathVariable String source, @RequestBody Order order) {
        Source orderSource = Source.valueOf(source);
        order.setSource(orderSource);
        orderService.placeOrder(order);
        return ResponseEntity.ok(order);
    }

}
