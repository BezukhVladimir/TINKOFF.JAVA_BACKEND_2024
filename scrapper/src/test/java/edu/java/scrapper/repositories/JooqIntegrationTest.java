package edu.java.scrapper.repositories;

import edu.java.scrapper.repositories.jooq.JooqChatRepository;
import edu.java.scrapper.repositories.jooq.JooqLinkRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
