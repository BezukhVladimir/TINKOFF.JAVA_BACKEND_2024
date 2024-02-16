package edu.java.bot.configurations;

import edu.java.bot.handlers.CommandHandler;
import edu.java.bot.link_validators.LinkValidator;
import edu.java.bot.services.MessageService;
import edu.java.bot.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageServiceConfig {
    @Bean
    public MessageService messageService(
        UserService userService,
        LinkValidator linkValidator,
        CommandHandler commandHandler
    ) {
        return new MessageService(userService, linkValidator, commandHandler);
    }
}
