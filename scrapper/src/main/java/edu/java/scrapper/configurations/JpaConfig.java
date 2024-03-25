package edu.java.scrapper.configurations;

import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.repositories.jpa.JpaChatRepository;
import edu.java.scrapper.repositories.jpa.JpaChatRepositoryInterface;
import edu.java.scrapper.repositories.jpa.JpaLinkRepository;
import edu.java.scrapper.repositories.jpa.JpaLinkRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaConfig {
    @Autowired
    private JpaChatRepositoryInterface jpaChatRepositoryInterface;

    @Autowired
    private JpaLinkRepositoryInterface jpaLinkRepositoryInterface;

    @Bean
    public ChatRepository jpaChatRepository() {
        return new JpaChatRepository(jpaChatRepositoryInterface);
    }

    @Bean
    public LinkRepository jpaLinkRepository() {

        return new JpaLinkRepository(jpaLinkRepositoryInterface);
    }
}
