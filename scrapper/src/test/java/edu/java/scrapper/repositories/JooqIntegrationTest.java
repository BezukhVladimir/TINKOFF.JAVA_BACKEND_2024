package edu.java.scrapper.repositories;

import edu.java.scrapper.repositories.chats.ChatRepository;
import edu.java.scrapper.repositories.chats.JooqChatRepository;
import edu.java.scrapper.repositories.links.JooqLinkRepository;
import edu.java.scrapper.repositories.links.LinkRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

public class JooqIntegrationTest extends IntegrationTest {
    @Autowired
    protected JooqChatRepository jooqChatRepository;
    @Autowired
    protected JooqLinkRepository jooqLinkRepository; //на всякий случай, чтобы точно проверить что всё орк

    @AfterEach
    void clear() {
        jooqChatRepository.removeAll();
        jooqLinkRepository.removeAll();
    }
}
