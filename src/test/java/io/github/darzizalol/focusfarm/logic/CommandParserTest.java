package io.github.darzizalol.focusfarm.logic;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.darzizalol.focusfarm.logic.commands.ExitCommand;
import io.github.darzizalol.focusfarm.logic.commands.FertilizeCommand;
import io.github.darzizalol.focusfarm.logic.commands.HarvestCommand;
import io.github.darzizalol.focusfarm.logic.commands.HelpCommand;
import io.github.darzizalol.focusfarm.logic.commands.PlantCommand;
import io.github.darzizalol.focusfarm.logic.commands.StatusCommand;
import io.github.darzizalol.focusfarm.logic.commands.WaterCommand;
import io.github.darzizalol.focusfarm.model.FarmException;

class CommandParserTest {
    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    @Test
    void parse_allValidCommands_returnsExpectedTypes() throws Exception {
        assertInstanceOf(PlantCommand.class, parser.parse("  /PLANT   1   carrot   10s "));
        assertInstanceOf(WaterCommand.class, parser.parse("/water 1"));
        assertInstanceOf(FertilizeCommand.class, parser.parse("/fertilize 1"));
        assertInstanceOf(HarvestCommand.class, parser.parse("/harvest 1"));
        assertInstanceOf(StatusCommand.class, parser.parse("/status"));
        assertInstanceOf(HelpCommand.class, parser.parse("/help"));
        assertInstanceOf(ExitCommand.class, parser.parse("/exit"));
    }

    @Test
    void parse_invalidInputs_rejected() {
        assertThrows(FarmException.class, () -> parser.parse(null));
        assertThrows(FarmException.class, () -> parser.parse("   "));
        assertThrows(FarmException.class, () -> parser.parse("plant 1 carrot 10s"));
        assertThrows(FarmException.class, () -> parser.parse("/plant"));
        assertThrows(FarmException.class, () -> parser.parse("/plant one carrot 10s"));
        assertThrows(FarmException.class, () -> parser.parse("/plant 1 radish 10s"));
        assertThrows(FarmException.class, () -> parser.parse("/plant 1 carrot ten"));
        assertThrows(FarmException.class, () -> parser.parse("/water"));
        assertThrows(FarmException.class, () -> parser.parse("/status extra"));
    }

    @Test
    void parse_durationOverflow_rejected() {
        assertThrows(FarmException.class,
                () -> DurationParser.parse("999999999999999999999999999999999999999999999999m"));
    }
}
