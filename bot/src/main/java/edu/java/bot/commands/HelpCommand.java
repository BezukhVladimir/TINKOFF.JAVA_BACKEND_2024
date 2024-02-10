package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import java.util.List;

public final class HelpCommand implements Command {
    static final String HELP_MESSAGE =
        "Команды LinkTracker'а:" + System.lineSeparator();
    private static final String COMMAND_DESCRIPTION_MESSAGE =
        "%s — %s" + System.lineSeparator();

    private final List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "показать список команд";
    }

    @Override
    public String handle(Update update) {
        return createText();
    }

    private String createText() {
        var text = new StringBuilder();

        text.append(HELP_MESSAGE);
        commands.forEach(command ->
            text.append(
                COMMAND_DESCRIPTION_MESSAGE.formatted(
                    command.command(), command.description()
            ))
        );

        return text.toString();
    }
}
