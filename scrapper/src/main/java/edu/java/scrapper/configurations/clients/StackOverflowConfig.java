package edu.java.scrapper.configurations.clients;

import edu.java.scrapper.clients.stackoverflow.RegularWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StackOverflowConfig {
    @Bean
    public RegularWebClient stackOverflowRegularWebClient() {
        return new RegularWebClient();
    }
}
