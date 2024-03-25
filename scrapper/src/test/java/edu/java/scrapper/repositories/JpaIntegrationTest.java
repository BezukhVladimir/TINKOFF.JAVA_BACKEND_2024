package edu.java.scrapper.repositories;

import edu.java.scrapper.repositories.jpa.JpaChatRepository;
import edu.java.scrapper.repositories.jpa.JpaLinkRepository;
import org.junit.jupiter.api.AfterEach;

public class JpaIntegrationTest extends IntegrationTest {
    protected static JpaChatRepository jpaChatRepository;
    protected static JpaLinkRepository jpaLinkRepository;

    @AfterEach
    void clear() {
        jpaChatRepository.removeAll();
        jpaLinkRepository.removeAll();
    }
}
