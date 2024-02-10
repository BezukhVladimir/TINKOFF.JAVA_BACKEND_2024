package edu.java.bot.services;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.link_validators.GitHubLinkValidator;
import edu.java.bot.link_validators.LinkValidator;
import edu.java.bot.link_validators.StackOverflowLinkValidator;
import edu.java.bot.models.User;
import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Service;
import static edu.java.bot.services.UserService.addLink;
import static edu.java.bot.services.UserService.deleteLink;
import static edu.java.bot.utils.CommandUtils.findByName;

@Service
public final class MessageService {
    private MessageService() {
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

    private static final LinkValidator LINK_VALIDATOR = LinkValidator.link(
        new GitHubLinkValidator(),
        new StackOverflowLinkValidator()
    );

    public static String createResponseText(Update update) {
        long chatId = update.message().chat().id();
        String text = update.message().text();
        Optional<Command> command = findByName(text);

        if (command.isPresent()) {
            return command.get().handle(update);
        } else {
            return createNonCommandText(chatId, text);
        }
    }

    private static String createNonCommandText(long chatId, String text) {
        Optional<User> initiator = UserService.findById(chatId);

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

    private static String createLinkValidatorText(User user, URI uri) {
        if (user.waitingLinkForTracking()) {
            return createWaitingLinkForTrackingText(user, uri);
        }

        if (user.waitingLinkForUntracking()) {
            return createWaitingLinkForUntrackingText(user, uri);
        }

        return INVALID_COMMAND_MESSAGE;
    }

    private static String createWaitingLinkForTrackingText(User user, URI url) {
        if (LINK_VALIDATOR.isValid(url)) {
            return addLink(user, url)
                ? SUCCESSFUL_TRACKING_MESSAGE
                : DUPLICATE_TRACKING_MESSAGE;
        }

        return NOT_SUPPORTED_LINK_MESSAGE;
    }

    private static String createWaitingLinkForUntrackingText(User user, URI url) {
        if (LINK_VALIDATOR.isValid(url)) {
            return deleteLink(user, url)
                ? SUCCESSFUL_UNTRACKING_MESSAGE
                : ABSENT_UNTRACKING_MESSAGE;

        }

        return NOT_SUPPORTED_LINK_MESSAGE;
    }
}
