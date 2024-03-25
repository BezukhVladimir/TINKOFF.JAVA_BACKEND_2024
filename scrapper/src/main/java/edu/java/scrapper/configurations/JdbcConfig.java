package edu.java.scrapper.configurations;

import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.repositories.jdbc.JdbcChatRepository;
import edu.java.scrapper.repositories.jdbc.JdbcLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcConfig {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public ChatRepository jdbcChatRepository() {
        return new JdbcChatRepository(jdbcTemplate);
    }

    @Bean
    public LinkRepository jdbcLinkRepository() {
        return new JdbcLinkRepository(jdbcTemplate);
    }
}
