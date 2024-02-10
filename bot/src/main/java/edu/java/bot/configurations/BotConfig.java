package edu.java.bot.configurations;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static edu.java.bot.utils.CommandUtils.getCommands;

@Configuration
public class BotConfig {

    @Bean
    TelegramBot telegramBot(ApplicationConfig applicationConfig) {
        var bot = new TelegramBot(applicationConfig.telegramToken());
        bot.execute(createMenuCommand());
        return bot;
    }

    private SetMyCommands createMenuCommand() {
        return new SetMyCommands(getCommands().values().stream().map(command -> new BotCommand(
            command.command(),
            command.description()
        )).toArray(BotCommand[]::new));
    }
}
