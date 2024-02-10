package edu.java.bot.services;

import edu.java.bot.models.SessionState;
import edu.java.bot.models.User;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class UserService {
    private UserService() {
    }

    private static final Map<Long, User> USERS = new HashMap<>();

    public static void clear() {
        USERS.clear();
    }

    public static void addUser(User user) {
        USERS.put(user.getChatId(), user);
    }

    public static Optional<User> findById(long chatId) {
        return USERS.containsKey(chatId)
            ? Optional.of(USERS.get(chatId))
            : Optional.empty();
    }

    /**
     * Регистрирует пользователя по идентификатору чата.
     *
     * @param chatId идентификатор чата пользователя
     * @return {@code true} если пользователь успешно зарегистрирован,
     *         {@code false} если пользователь уже зарегистрирован
     */
    public static boolean register(long chatId) {
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
    public static boolean changeSessionState(long chatId, SessionState newState) {
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
    public static boolean addLink(User user, URI uri) {
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
    public static boolean deleteLink(User user, URI uri) {
        List<URI> links = new ArrayList<>(user.getLinks());

        if (!links.contains(uri)) {
            return false;
        }

        links.remove(uri);
        updateLinks(user, links);

        return true;
    }

    private static void updateLinks(User user, List<URI> links) {
        user.setLinks(links);
        user.setState(SessionState.DEFAULT);
        addUser(user);
    }
}
