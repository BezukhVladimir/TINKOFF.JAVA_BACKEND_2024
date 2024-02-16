package edu.java.bot.listeners;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BotStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private final TelegramBot telegramBot;
    private final List<Command> commands;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        telegramBot.execute(createCommandMenu());
    }

    private SetMyCommands createCommandMenu() {
        return new SetMyCommands(commands.stream().map(command -> new BotCommand(
            command.command(),
            command.description()
        )).toArray(BotCommand[]::new));
    }
}
