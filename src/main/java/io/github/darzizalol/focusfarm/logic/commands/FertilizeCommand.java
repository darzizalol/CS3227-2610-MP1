package io.github.darzizalol.focusfarm.logic.commands;

import io.github.darzizalol.focusfarm.logic.CommandResult;
import io.github.darzizalol.focusfarm.logic.FarmCommand;
import io.github.darzizalol.focusfarm.logic.FarmEvent;
import io.github.darzizalol.focusfarm.model.Farm;
import io.github.darzizalol.focusfarm.model.FarmException;

/** Fertilizes a growing plot once to reduce its remaining time by 25 percent. */
public record FertilizeCommand(int plotId) implements FarmCommand {
    @Override
    public CommandResult execute(Farm farm) throws FarmException {
        farm.fertilize(plotId);
        return CommandResult.of("Fertilized plot " + plotId + ". Its remaining time was reduced by 25%.",
                FarmEvent.FERTILIZED, plotId, true);
    }
}
