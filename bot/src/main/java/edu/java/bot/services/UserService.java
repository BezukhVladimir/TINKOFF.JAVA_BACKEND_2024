package edu.java.bot.services;

import edu.java.bot.models.SessionState;
import edu.java.bot.models.User;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public void clear() {
        users.clear();
    }

    public void addUser(User user) {
        users.put(user.getChatId(), user);
    }

    public Optional<User> findById(long chatId) {
        return users.containsKey(chatId)
            ? Optional.of(users.get(chatId))
            : Optional.empty();
    }

    /**
     * Регистрирует пользователя по идентификатору чата.
     *
     * @param chatId идентификатор чата пользователя
     * @return {@code true} если пользователь успешно зарегистрирован,
     *         {@code false} если пользователь уже зарегистрирован
     */
    public boolean register(long chatId) {
        Optional<User> initiator = findById(chatId);

        if (initiator.isEmpty()) {
            addUser(new User(chatId, List.of(), SessionState.DEFAULT));

            return true;
        }

        return false;
    }

    /**
     * Меняет состояние сессии для указанного пользователя.
     *
     * @param chatId идентификатор чата пользователя
     * @param newState новое состояние сессии
     * @return {@code true} если состояние сессии изменено,
     *         {@code false} если пользователь не найден
     */
    public boolean changeSessionState(long chatId, SessionState newState) {
        Optional<User> initiator = findById(chatId);

        if (initiator.isPresent()) {
            User user = initiator.get();
            user.setState(newState);
            addUser(user);

            return true;
        }

        return false;
    }

    /**
     * Добавляет ссылку в список для отслеживания.
     *
     * @param user пользователь
     * @param uri ссылка
     * @return {@code true} если ссылка добавлена,
     *         {@code false} если ссылка уже есть в списке
     */
    public boolean addLink(User user, URI uri) {
        List<URI> links = new ArrayList<>(user.getLinks());

        if (links.contains(uri)) {
            return false;
        }

        links.add(uri);
        updateLinks(user, links);

        return true;
    }

    /**
     * Удаляет ссылку из списка для отслеживания.
     *
     * @param user пользователь
     * @param uri ссылка
     * @return {@code true} если ссылка удалена,
     *         {@code false} если ссылки не было в списке
     */
    public boolean deleteLink(User user, URI uri) {
        List<URI> links = new ArrayList<>(user.getLinks());

        if (!links.contains(uri)) {
            return false;
        }

        links.remove(uri);
        updateLinks(user, links);

        return true;
    }

    private void updateLinks(User user, List<URI> links) {
        user.setLinks(links);
        user.setState(SessionState.DEFAULT);
        addUser(user);
    }
}
