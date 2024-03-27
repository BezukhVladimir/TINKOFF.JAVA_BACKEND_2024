package edu.java.scrapper.repositories;

import edu.java.scrapper.configurations.JdbcConfig;
import edu.java.scrapper.repositories.jdbc.JdbcChatRepository;
import edu.java.scrapper.repositories.jdbc.JdbcLinkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

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
