package com.example.busticketplatform.scheduling;

import com.example.busticketplatform.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSchedulingService {

    private final OrderService orderService;

    @Scheduled(fixedDelay = 5000)
    public void schedule() {
        orderService.pickOrders();
    }

}
