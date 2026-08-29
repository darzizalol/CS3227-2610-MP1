package io.github.darzizalol.focusfarm.logic.commands;

import io.github.darzizalol.focusfarm.logic.CommandResult;
import io.github.darzizalol.focusfarm.logic.FarmCommand;
import io.github.darzizalol.focusfarm.logic.FarmEvent;
import io.github.darzizalol.focusfarm.model.Farm;
import io.github.darzizalol.focusfarm.model.FarmException;
import io.github.darzizalol.focusfarm.model.HarvestRecord;

/**
 * Harvests a ready plot and records its crop.
 *
 * @param plotId one-based plot identifier
 */
public record HarvestCommand(int plotId) implements FarmCommand {
    @Override
    public CommandResult execute(Farm farm) throws FarmException {
        HarvestRecord record = farm.harvest(plotId);
        return CommandResult.of("Harvested " + record.crop().displayName() + " from plot " + plotId
                + ". The plot is empty again.", FarmEvent.HARVESTED, plotId, true);
    }
}
