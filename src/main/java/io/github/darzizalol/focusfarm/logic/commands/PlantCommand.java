package io.github.darzizalol.focusfarm.logic.commands;

import java.time.Duration;
import java.util.Objects;

import io.github.darzizalol.focusfarm.logic.CommandResult;
import io.github.darzizalol.focusfarm.logic.FarmCommand;
import io.github.darzizalol.focusfarm.logic.FarmEvent;
import io.github.darzizalol.focusfarm.model.CropType;
import io.github.darzizalol.focusfarm.model.Farm;
import io.github.darzizalol.focusfarm.model.FarmException;

/**
 * Plants a crop in an empty plot.
 *
 * @param plotId one-based plot identifier
 * @param crop crop to plant
 * @param duration configured growth duration
 */
public record PlantCommand(int plotId, CropType crop, Duration duration) implements FarmCommand {
    /** Validates immutable command values. */
    public PlantCommand {
        Objects.requireNonNull(crop);
        Objects.requireNonNull(duration);
    }

    @Override
    public CommandResult execute(Farm farm) throws FarmException {
        farm.plant(plotId, crop, duration);
        return CommandResult.of("Planted " + crop.displayName() + " in plot " + plotId
                + ". Water it to start the " + formatDuration(duration) + " timer.",
                FarmEvent.PLANTED, plotId, true);
    }

    private String formatDuration(Duration value) {
        long seconds = value.toSeconds();
        return seconds % 60 == 0 ? seconds / 60 + "m" : seconds + "s";
    }
}
