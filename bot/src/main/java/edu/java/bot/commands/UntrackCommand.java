package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.models.SessionState;
import edu.java.bot.models.User;
import edu.java.bot.services.UserService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UntrackCommand implements Command {
    private final UserService userService;

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "прекратить отслеживание ссылки";
    }

    @Override
    public String handle(Update update) {
        var chatId = update.message().chat().id();

        return createText(chatId);
    }

    private String createText(long chatId) {
        Optional<User> initiator = userService.findById(chatId);

        if (initiator.isPresent()) {
            List<URI> links = initiator.get().getLinks();

            if (links.isEmpty()) {
                return EMPTY_LIST_MESSAGE;
            }
        }

        if (userService.changeSessionState(chatId, SessionState.WAITING_LINK_FOR_UNTRACKING)) {
            return UNTRACK_MESSAGE;
        }

        return UNKNOWN_USER_MESSAGE;
    }

    static final String UNTRACK_MESSAGE =
        "Отправьте ссылку для прекращения отслеживания";
    static final String UNKNOWN_USER_MESSAGE =
        "Вы не зарегистрированы в LinkTracker'е";
    static final String EMPTY_LIST_MESSAGE =
        "Список отслеживаемых ссылок пустой";
}
