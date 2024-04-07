package edu.java.bot.configurations.listeners;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.configurations.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {
    @Bean
    public TelegramBot telegramBot(ApplicationConfig applicationConfig) {
        return new TelegramBot(applicationConfig.telegramToken());
    }
}
