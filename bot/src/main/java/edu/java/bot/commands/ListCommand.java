package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.models.User;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import static edu.java.bot.services.UserService.findById;

public final class ListCommand implements Command {
    static final String EMPTY_LIST_MESSAGE =
        "Список отслеживаемых ссылок пустой";
    static final String UNKNOWN_USER_MESSAGE =
        "Вы не зарегистрированы в LinkTracker'е";
    static final String LIST_MESSAGE =
        "LinkTracker отслеживает следующие ссылки:" + System.lineSeparator();

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "показать список отслеживаемых ссылок";
    }

    @Override
    public String handle(Update update) {
        Long chatId = update.message().chat().id();

        return createText(chatId);
    }

    private String createText(long chatId) {
        Optional<User> initiator = findById(chatId);

        if (initiator.isPresent()) {
            List<URI> links = initiator.get().getLinks();

            if (links.isEmpty()) {
                return EMPTY_LIST_MESSAGE;
            }

            return createTextList(links);
        }

        return UNKNOWN_USER_MESSAGE;
    }

    private String createTextList(List<URI> links) {
        var text = new StringBuilder();

        text.append(LIST_MESSAGE);
        for (URI uri : links) {
            text.append(uri.toString()).append(System.lineSeparator());
        }

        return text.toString();
    }
}
