package edu.java.scrapper.repositories;

import edu.java.scrapper.models.Chat;
import java.util.List;

public interface ChatRepository {
    Chat add(Long id);

    void remove(Long id);

    void removeAll();

    Chat findById(Long id);

    List<Chat> findAll();

    List<Chat> findAllChatsByUrl(String url);
}
