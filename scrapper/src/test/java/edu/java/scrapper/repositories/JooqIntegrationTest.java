package edu.java.scrapper.repositories;

import edu.java.scrapper.repositories.chats.ChatRepository;
import edu.java.scrapper.repositories.chats.JooqChatRepository;
import edu.java.scrapper.repositories.links.JooqLinkRepository;
import edu.java.scrapper.repositories.links.LinkRepository;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JooqIntegrationTest extends IntegrationTest {
    @Autowired
    protected DSLContext dslContext;

    protected ChatRepository jooqChatRepository;
    protected LinkRepository jooqLinkRepository;

    @BeforeEach
    void setUp() {
        jooqChatRepository = new JooqChatRepository(dslContext);
        jooqLinkRepository = new JooqLinkRepository(dslContext);
    }

    @AfterEach
    void clear() {
        jooqChatRepository.removeAll();
        jooqLinkRepository.removeAll();
    }
}
