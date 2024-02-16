package edu.java.bot.handlers;

import edu.java.bot.commands.Command;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public record CommandHandler(Map<String, Command> commands) {
    public Optional<Command> findByName(String command) {
        return commands.containsKey(command)
            ? Optional.of(commands.get(command))
            : Optional.empty();
    }
}
