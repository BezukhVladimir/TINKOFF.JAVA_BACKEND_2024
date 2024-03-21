package edu.java.scrapper_jooq.configurations.clients;

import edu.java.scrapper_jooq.clients.stackoverflow.RegularWebClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StackOverflowConfig {
    @Bean
    @Qualifier("stackoverflow")
    public RegularWebClient stackOverflowRegularWebClient() {
        return new RegularWebClient();
    }
}
