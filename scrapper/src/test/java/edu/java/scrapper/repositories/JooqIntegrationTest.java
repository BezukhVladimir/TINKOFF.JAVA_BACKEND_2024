package edu.java.scrapper.repositories;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.database-access-type=jooq")
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
