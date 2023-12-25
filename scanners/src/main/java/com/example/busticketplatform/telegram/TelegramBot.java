package com.example.busticketplatform.telegram;

import com.example.busticketplatform.dto.Filter;
import com.example.busticketplatform.entity.Order;
import com.example.busticketplatform.serialize.BusSource;
import com.example.busticketplatform.serialize.Source;
import com.example.busticketplatform.service.FilterService;
import com.example.busticketplatform.service.NotificationService;
import com.example.busticketplatform.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = getLogger(TelegramBot.class);

    private final TelegramConfiguration botConfig;
    private final TelegramBotChatService chatService;
    private final FilterService filterService;
    private final OrderService orderService;
    private final NotificationService notificationService;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

            if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            String key = switch (messageText) {
                case "/start" -> {
                    filterService.initFilter(chatId);
                    yield chatService.getStartMessage();
                }
                case "/find" -> {
                    Filter filter = filterService.getFilter(chatId);
                    Order order = orderService.placeOrder(Order.builder()
                          .rideFilter(filter)
                          .customerId(chatId)
                          .source(BusSource.atlas)
                          .postingDate(System.currentTimeMillis())
                          .build());
                    notificationService.addNotificator(chatId, notification -> sendMessage(chatId, chatService.getNotificationMessage(notification), ParseMode.HTML));
                    yield chatService.getPlaceOrderMessage(order);
                }
                case "/stop" -> {
                    notificationService.closeNotification(chatId);
                    orderService.invalidateOrder(chatId);
                    yield chatService.getStopNotificationMessage();
                }
                default -> {
                    if (filterService.isStartWorkWithFilter(chatId)) {
                        yield chatService.getFilterMessage(filterService.addFilterParam(chatId, messageText));
                    }
                    yield chatService.getIncorrectExpression();
                }
            };
            sendMessage(chatId, key, ParseMode.MARKDOWNV2);
        }

    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    private void sendMessage(Long chatId, String textToSend, String parseMode){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(ParseMode.MARKDOWNV2.equals(parseMode) ? MarkdownV2EscapeUtil.escapeMarkdown(textToSend) : textToSend);
        sendMessage.setParseMode(parseMode);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
