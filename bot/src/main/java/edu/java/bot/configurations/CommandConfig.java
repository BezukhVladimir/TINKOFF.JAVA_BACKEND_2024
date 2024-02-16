package edu.java.bot.configurations;

import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.services.UserService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfig {
    @Bean
    public StartCommand startCommand(UserService userService) {
        return new StartCommand(userService);
    }

    @Bean
    public TrackCommand trackCommand(UserService userService) {
        return new TrackCommand(userService);
    }

    @Bean
    public UntrackCommand untrackCommand(UserService userService) {
        return new UntrackCommand(userService);
    }

    @Bean
    public ListCommand listCommand(UserService userService) {
        return new ListCommand(userService);
    }

    @Bean
    public HelpCommand helpCommand(List<Command> commands) {
        return new HelpCommand(commands);
    }
}
