package edu.java.bot.configurations.services;

import edu.java.bot.clients.ScrapperWebClient;
import edu.java.bot.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfig {
    @Bean
    public UserService userService(ScrapperWebClient scrapperWebClient) {
        return new UserService(scrapperWebClient);
    }
}
