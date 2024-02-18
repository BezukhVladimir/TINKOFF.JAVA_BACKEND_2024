package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StartCommand implements Command {
    private final UserService userService;

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "зарегистрироваться в LinkTracker'е";
    }

    @Override
    public String handle(Update update) {
        long chatId = update.message().chat().id();

        return createText(chatId);
    }

    private String createText(long chatId) {
        if (userService.register(chatId)) {
            return SUCCESSFUL_REGISTRATION_MESSAGE;
        }

        return ALREADY_REGISTERED_MESSAGE;
    }

    static final String SUCCESSFUL_REGISTRATION_MESSAGE =
        "Вы успешно зарегистрированы в LinkTracker'е!";
    static final String ALREADY_REGISTERED_MESSAGE =
        "Вы уже зарегистрированы в LinkTracker'е";
}
