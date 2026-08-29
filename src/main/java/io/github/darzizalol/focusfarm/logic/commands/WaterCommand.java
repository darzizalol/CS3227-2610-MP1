package io.github.darzizalol.focusfarm.logic.commands;

import io.github.darzizalol.focusfarm.logic.CommandResult;
import io.github.darzizalol.focusfarm.logic.FarmCommand;
import io.github.darzizalol.focusfarm.logic.FarmEvent;
import io.github.darzizalol.focusfarm.model.Farm;
import io.github.darzizalol.focusfarm.model.FarmException;

/**
 * Waters a planted plot to start its growth timer.
 *
 * @param plotId one-based plot identifier
 */
public record WaterCommand(int plotId) implements FarmCommand {
    @Override
    public CommandResult execute(Farm farm) throws FarmException {
        farm.water(plotId);
        return CommandResult.of("Watered plot " + plotId + ". Its crop is now growing.",
                FarmEvent.WATERED, plotId, true);
    }
}
