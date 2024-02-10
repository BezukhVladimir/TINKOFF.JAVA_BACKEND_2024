package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import static edu.java.bot.services.UserService.register;

public final class StartCommand implements Command {
    static final String SUCCESSFUL_REGISTRATION_MESSAGE =
        "Вы успешно зарегистрированы в LinkTracker'е!";
    static final String ALREADY_REGISTERED_MESSAGE =
        "Вы уже зарегистрированы в LinkTracker'е";

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
        if (register(chatId)) {
            return SUCCESSFUL_REGISTRATION_MESSAGE;
        }

        return ALREADY_REGISTERED_MESSAGE;
    }
}
