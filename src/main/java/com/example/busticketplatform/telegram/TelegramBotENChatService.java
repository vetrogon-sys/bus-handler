package com.example.busticketplatform.telegram;

import com.example.busticketplatform.dto.Filter;
import com.example.busticketplatform.dto.FilterToken;
import com.example.busticketplatform.entity.Notification;
import com.example.busticketplatform.entity.Order;
import com.example.busticketplatform.entity.Ride;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramBotENChatService implements TelegramBotChatService {


    @Override
    public String getStartMessage() {
        return """
              Hello, enter for place use next tokens:
              %s
              """.formatted(Arrays.stream(FilterToken.values())
              .map(token -> "`%s`  %s".formatted(token.token, token.description))
              .collect(Collectors.joining("\n")));
    }

    @Override
    public String getPlaceOrderMessage(Order order) {
        return
              """
              You order id: %s
              We back after find ride for you :)
              """.formatted(order.getId());
    }

    @Override
    public String getFilterMessage(Filter filter) {
        return """
              Start city: %s
              End city: %s
              Date: %s
              Places: %s
              Type tokenize message again ti change filter
              Or type '/find' to start searching for a ride
              """.formatted(filter.getFrom(), filter.getTo(), filter.getDate(), filter.getRequiredPlaces());
    }

    @Override
    public String getNotificationMessage(Notification notification) {
        return """
              Here rides for you request
              %s
              """.formatted(notification.getAvailableRides().stream()
              .map(Ride::toShortString)
              .collect(Collectors.joining("---\n")));
    }

    @Override
    public String getIncorrectExpression() {
        return "Incorrect expression";
    }

    @Override
    public String getStopNotificationMessage() {
        return """
              You're notification request is closed
              Thanks for using our platform
              """;
    }
}
