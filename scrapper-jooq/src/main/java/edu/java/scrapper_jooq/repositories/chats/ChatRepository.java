package edu.java.scrapper_jooq.repositories.chats;

import edu.java.scrapper_jooq.models.Chat;
import java.net.URI;
import java.util.List;

public interface ChatRepository {
    Chat add(Long id);

    void remove(Long id);

    void removeAll();

    Chat findById(Long id);

    List<Chat> findAll();

    List<Chat> findAllChatsByUrl(URI url);
}
