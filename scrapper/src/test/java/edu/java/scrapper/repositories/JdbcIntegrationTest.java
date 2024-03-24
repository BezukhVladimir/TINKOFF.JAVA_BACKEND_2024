package edu.java.scrapper.repositories;

import edu.java.scrapper.repositories.chats.ChatRepository;
import edu.java.scrapper.repositories.links.LinkRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
