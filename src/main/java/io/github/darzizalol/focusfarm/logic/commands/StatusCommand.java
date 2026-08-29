package io.github.darzizalol.focusfarm.logic.commands;

import java.util.StringJoiner;

import io.github.darzizalol.focusfarm.logic.CommandResult;
import io.github.darzizalol.focusfarm.logic.FarmCommand;
import io.github.darzizalol.focusfarm.logic.FarmEvent;
import io.github.darzizalol.focusfarm.model.Farm;
import io.github.darzizalol.focusfarm.model.FarmSnapshot;
import io.github.darzizalol.focusfarm.model.PlotSnapshot;

/** Displays concise status for all six plots. */
public final class StatusCommand implements FarmCommand {
    @Override
    public CommandResult execute(Farm farm) {
        FarmSnapshot snapshot = farm.snapshot();
        StringJoiner status = new StringJoiner(System.lineSeparator(), "Farm status:" + System.lineSeparator(), "");
        for (PlotSnapshot plot : snapshot.plots()) {
            status.add(formatPlot(plot));
        }
        return CommandResult.of(status.toString(), FarmEvent.STATUS, null, false);
    }

    private String formatPlot(PlotSnapshot plot) {
        if (plot.crop() == null) {
            return "Plot " + plot.id() + ": empty";
        }
        String suffix = plot.state().name().toLowerCase();
        if (plot.remainingSeconds() > 0) {
            suffix += ", " + plot.remainingSeconds() + "s remaining";
        }
        return "Plot " + plot.id() + ": " + plot.crop().displayName() + " (" + suffix + ")";
    }
}
