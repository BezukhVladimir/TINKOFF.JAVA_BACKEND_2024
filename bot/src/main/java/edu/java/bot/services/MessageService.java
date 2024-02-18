package edu.java.bot.services;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.handlers.CommandHandler;
import edu.java.bot.link_validators.LinkValidator;
import edu.java.bot.models.User;
import java.net.URI;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageService {
    private final UserService userService;
    private final LinkValidator linkValidator;
    private final CommandHandler commandHandler;

    public String createResponseText(Update update) {
        long chatId = update.message().chat().id();
        String text = update.message().text();
        Optional<Command> command = commandHandler.findByName(text);

        if (command.isPresent()) {
            return command.get().handle(update);
        } else {
            return createNonCommandText(chatId, text);
        }
    }

    private String createNonCommandText(long chatId, String text) {
        Optional<User> initiator = userService.findById(chatId);

        if (initiator.isEmpty()) {
            return UNKNOWN_USER_MESSAGE;
        }

        User user = initiator.get();
        try {
            // TODO
            // Нужно как-то обрабатывать пользовательский ввод...
            String uri;
            String scheme = "https://";
            if (text.startsWith(scheme)) {
                uri = text;
            } else {
                uri = scheme + text;
            }

            return createLinkValidatorText(user, URI.create(uri));
        } catch (IllegalArgumentException e) {
            return INVALID_LINK_MESSAGE;
        }
    }

    private String createLinkValidatorText(User user, URI uri) {
        if (user.waitingLinkForTracking()) {
            return createWaitingLinkForTrackingText(user, uri);
        }

        if (user.waitingLinkForUntracking()) {
            return createWaitingLinkForUntrackingText(user, uri);
        }

        return INVALID_COMMAND_MESSAGE;
    }

    private String createWaitingLinkForTrackingText(User user, URI url) {
        if (linkValidator.isValid(url)) {
            return userService.addLink(user, url)
                ? SUCCESSFUL_TRACKING_MESSAGE
                : DUPLICATE_TRACKING_MESSAGE;
        }

        return NOT_SUPPORTED_LINK_MESSAGE;
    }

    private String createWaitingLinkForUntrackingText(User user, URI url) {
        if (linkValidator.isValid(url)) {
            return userService.deleteLink(user, url)
                ? SUCCESSFUL_UNTRACKING_MESSAGE
                : ABSENT_UNTRACKING_MESSAGE;

        }

        return NOT_SUPPORTED_LINK_MESSAGE;
    }

    static final String UNKNOWN_USER_MESSAGE =
        "Вы не зарегистрированы в LinkTracker'е";
    static final String INVALID_LINK_MESSAGE =
        "Вы ввели некорректную ссылку";
    static final String INVALID_COMMAND_MESSAGE =
        "Вы ввели некорректную команду";
    static final String SUCCESSFUL_TRACKING_MESSAGE =
        "Теперь эта ссылка отслеживается!";
    static final String DUPLICATE_TRACKING_MESSAGE =
        "Эта ссылка уже отслеживается";
    static final String SUCCESSFUL_UNTRACKING_MESSAGE =
        "Больше эта ссылка не отслеживается!";
    static final String ABSENT_UNTRACKING_MESSAGE =
        "Эта ссылка не отслеживалась";
    static final String NOT_SUPPORTED_LINK_MESSAGE =
        "Эта ссылка не поддерживается";
}
