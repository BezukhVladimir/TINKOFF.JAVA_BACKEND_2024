package edu.java.scrapper.repositories;

import edu.java.scrapper.repositories.jooq.JooqChatRepository;
import edu.java.scrapper.repositories.jooq.JooqLinkRepository;
import edu.java.scrapper.repositories.jpa.JpaChatRepository;
import edu.java.scrapper.repositories.jpa.JpaChatRepositoryInterface;
import edu.java.scrapper.repositories.jpa.JpaLinkRepository;
import edu.java.scrapper.repositories.jpa.JpaLinkRepositoryInterface;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
