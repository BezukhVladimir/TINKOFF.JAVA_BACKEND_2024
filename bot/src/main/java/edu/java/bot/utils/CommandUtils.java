package edu.java.bot.utils;

import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CommandUtils {
    private CommandUtils() {
    }

    private static final Command START = new StartCommand();
    private static final Command TRACK = new TrackCommand();
    private static final Command UNTRACK = new UntrackCommand();
    private static final Command LIST = new ListCommand();
    private static final Command HELP = new HelpCommand(
        List.of(START, TRACK, UNTRACK, LIST)
    );
    private static final Map<String, Command> COMMANDS =
        Map.of(
            "/start",   START,
            "/track",   TRACK,
            "/untrack", UNTRACK,
            "/list",    LIST,
            "/help",    HELP
        );

    public static Optional<Command> findByName(String command) {
        return COMMANDS.containsKey(command)
            ? Optional.of(COMMANDS.get(command))
            : Optional.empty();
    }

    public static Map<String, Command> getCommands() {
        return COMMANDS;
    }
}
