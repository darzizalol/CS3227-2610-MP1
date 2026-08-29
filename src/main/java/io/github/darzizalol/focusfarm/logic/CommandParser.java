package io.github.darzizalol.focusfarm.logic;

import java.time.Duration;
import java.util.Locale;

import io.github.darzizalol.focusfarm.logic.commands.ExitCommand;
import io.github.darzizalol.focusfarm.logic.commands.FertilizeCommand;
import io.github.darzizalol.focusfarm.logic.commands.HarvestCommand;
import io.github.darzizalol.focusfarm.logic.commands.HelpCommand;
import io.github.darzizalol.focusfarm.logic.commands.PlantCommand;
import io.github.darzizalol.focusfarm.logic.commands.StatusCommand;
import io.github.darzizalol.focusfarm.logic.commands.WaterCommand;
import io.github.darzizalol.focusfarm.model.CropType;
import io.github.darzizalol.focusfarm.model.FarmException;

/** Converts chat-style slash commands into executable command objects. */
public final class CommandParser {
    /** Parses a complete user command. */
    public FarmCommand parse(String input) throws FarmException {
        if (input == null || input.isBlank()) {
            throw new FarmException("Enter a command. Type /help to see the available commands.");
        }
        String[] tokens = input.trim().split("\\s+");
        String keyword = tokens[0].toLowerCase(Locale.ROOT);
        return switch (keyword) {
        case "/plant" -> parsePlant(tokens);
        case "/water" -> new WaterCommand(parsePlotOnly(tokens, "/water"));
        case "/fertilize" -> new FertilizeCommand(parsePlotOnly(tokens, "/fertilize"));
        case "/harvest" -> new HarvestCommand(parsePlotOnly(tokens, "/harvest"));
        case "/status" -> parseWithoutArguments(tokens, new StatusCommand(), "/status");
        case "/help" -> parseWithoutArguments(tokens, new HelpCommand(), "/help");
        case "/exit" -> parseWithoutArguments(tokens, new ExitCommand(), "/exit");
        default -> throw new FarmException("Unknown command '" + tokens[0] + "'. Type /help for available commands.");
        };
    }

    private FarmCommand parsePlant(String[] tokens) throws FarmException {
        if (tokens.length != 4) {
            throw new FarmException("Usage: /plant <plot 1-6> <crop> <duration>, for example /plant 2 carrot 10s.");
        }
        int plotId = parsePlot(tokens[1]);
        CropType crop = CropType.parse(tokens[2]);
        Duration duration = DurationParser.parse(tokens[3]);
        return new PlantCommand(plotId, crop, duration);
    }

    private int parsePlotOnly(String[] tokens, String usage) throws FarmException {
        if (tokens.length != 2) {
            throw new FarmException("Usage: " + usage + " <plot 1-6>.");
        }
        return parsePlot(tokens[1]);
    }

    private int parsePlot(String value) throws FarmException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new FarmException("Plot must be a number from 1 to 6.", exception);
        }
    }

    private FarmCommand parseWithoutArguments(String[] tokens, FarmCommand command, String usage)
            throws FarmException {
        if (tokens.length != 1) {
            throw new FarmException("Usage: " + usage + ".");
        }
        return command;
    }
}
