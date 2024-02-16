package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.models.SessionState;
import edu.java.bot.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TrackCommand implements Command {
    private final UserService userService;

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
        if (userService.changeSessionState(chatId, SessionState.WAITING_LINK_FOR_TRACKING)) {
            return TRACK_MESSAGE;
        }

        return UNKNOWN_USER_MESSAGE;
    }

    static final String TRACK_MESSAGE =
        "Пришлите ссылку для отслеживания";
    static final String UNKNOWN_USER_MESSAGE =
        "Вы не зарегистрированы в LinkTracker'е";
}
