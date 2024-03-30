package edu.java.scrapper.repositories;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JpaIntegrationTest extends IntegrationTest {
    @Autowired
    protected ChatRepository jpaChatRepository;
    @Autowired
    protected LinkRepository jpaLinkRepository;

    @AfterEach
    void clear() {
        jpaChatRepository.removeAll();
        jpaLinkRepository.removeAll();
    }
}
