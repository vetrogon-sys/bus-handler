package com.example.busticketplatform.telegram;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TelegramConfiguration {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

}
