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

public class JooqIntegrationTest extends IntegrationTest {
    protected static DSLContext dslContext;
    protected static JooqChatRepository jooqChatRepository;
    protected static JooqLinkRepository jooqLinkRepository;

    @BeforeAll
    static void setUp() {
        DefaultConfiguration configuration = new DefaultConfiguration();

        configuration.set(dataSource);
        configuration.set(SQLDialect.POSTGRES);
        configuration.settings().withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED);

        dslContext = DSL.using(configuration);

        jooqChatRepository = new JooqChatRepository(dslContext);
        jooqLinkRepository = new JooqLinkRepository(dslContext);
    }

    @AfterEach
    void clear() {
        jooqChatRepository.removeAll();
        jooqLinkRepository.removeAll();
    }
}
