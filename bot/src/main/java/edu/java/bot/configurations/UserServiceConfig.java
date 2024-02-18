package edu.java.bot.configurations;

import edu.java.bot.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfig {
    @Bean
    public UserService userService() {
        return new UserService();
    }
}
