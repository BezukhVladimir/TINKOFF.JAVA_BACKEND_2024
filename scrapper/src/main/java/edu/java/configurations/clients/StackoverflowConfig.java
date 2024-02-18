package edu.java.configurations.clients;

import edu.java.clients.stackoverflow.RegularWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StackoverflowConfig {
    @Bean
    public RegularWebClient regularWebClient() {
        return new RegularWebClient();
    }
}
