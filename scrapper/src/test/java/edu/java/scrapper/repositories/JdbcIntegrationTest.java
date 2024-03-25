package edu.java.scrapper.repositories;

import edu.java.scrapper.repositories.jdbc.JdbcChatRepository;
import edu.java.scrapper.repositories.jdbc.JdbcLinkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcIntegrationTest extends IntegrationTest {
    protected static JdbcTemplate jdbcTemplate;
    protected static JdbcChatRepository jdbcChatRepository;
    protected static JdbcLinkRepository jdbcLinkRepository;

    @BeforeAll
    static void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcChatRepository = new JdbcChatRepository(jdbcTemplate);
        jdbcLinkRepository = new JdbcLinkRepository(jdbcTemplate);
    }

    @AfterEach
    void clear() {
        jdbcChatRepository.removeAll();
        jdbcLinkRepository.removeAll();
    }
}
