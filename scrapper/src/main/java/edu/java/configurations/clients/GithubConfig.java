package edu.java.configurations.clients;

import edu.java.clients.github.RegularWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GithubConfig {
    @Bean
    public RegularWebClient regularWebClient() {
        return new RegularWebClient();
    }
}
