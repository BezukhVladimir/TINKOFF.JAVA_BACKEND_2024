package edu.java.scrapper.repositories;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.database-access-type=jdbc")
public class JdbcIntegrationTest extends IntegrationTest {
    @Autowired
    protected ChatRepository jdbcChatRepository;
    @Autowired
    protected LinkRepository jdbcLinkRepository;

    @AfterEach
    void clear() {
        jdbcChatRepository.removeAll();
        jdbcLinkRepository.removeAll();
    }
}
