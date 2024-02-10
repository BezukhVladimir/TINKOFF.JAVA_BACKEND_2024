package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.models.SessionState;
import static edu.java.bot.services.UserService.changeSessionState;

public final class TrackCommand implements Command {
    static final String TRACK_MESSAGE =
        "Пришлите ссылку для отслеживания";
    static final String UNKNOWN_USER_MESSAGE =
        "Вы не зарегистрированы в LinkTracker'е";

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public String description() {
        return "начать отслеживание ссылки";
    }

    @Override
    public String handle(Update update) {
        var chatId = update.message().chat().id();

        return createText(chatId);
    }

    private String createText(long chatId) {
        if (changeSessionState(chatId, SessionState.WAITING_LINK_FOR_TRACKING)) {
            return TRACK_MESSAGE;
        }

        return UNKNOWN_USER_MESSAGE;
    }
}
