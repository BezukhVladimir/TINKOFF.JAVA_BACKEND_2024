package edu.java.scrapper.configurations;

import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.repositories.jooq.JooqChatRepository;
import edu.java.scrapper.repositories.jooq.JooqLinkRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqConfig {
    @Autowired
    private DSLContext dslContext;

    @Bean
    public ChatRepository jooqChatRepository() {
        return new JooqChatRepository(dslContext);
    }

    @Bean
    public LinkRepository jooqLinkRepository() {
        return new JooqLinkRepository(dslContext);
    }
}
