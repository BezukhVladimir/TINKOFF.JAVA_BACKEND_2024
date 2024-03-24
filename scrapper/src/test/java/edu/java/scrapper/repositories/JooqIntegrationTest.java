package edu.java.scrapper.repositories;

import edu.java.scrapper.repositories.chats.ChatRepository;
import edu.java.scrapper.repositories.links.LinkRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

public class JooqIntegrationTest extends IntegrationTest {
    @Autowired
    protected ChatRepository jooqChatRepository;
    @Autowired
    protected LinkRepository jooqLinkRepository;

    @AfterEach
    void clear() {
        jooqChatRepository.removeAll();
        jooqLinkRepository.removeAll();
    }
}
