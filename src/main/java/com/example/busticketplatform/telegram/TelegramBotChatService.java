package com.example.busticketplatform.telegram;

import com.example.busticketplatform.dto.Filter;
import com.example.busticketplatform.entity.Notification;
import com.example.busticketplatform.entity.Order;

public interface TelegramBotChatService {

    String getStartMessage();

    String getPlaceOrderMessage(Order order);

    String getFilterMessage(Filter filter);

    String getNotificationMessage(Notification notification);

    String getIncorrectExpression();

    String getStopNotificationMessage();

}
