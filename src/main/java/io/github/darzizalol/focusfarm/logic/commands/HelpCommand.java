package io.github.darzizalol.focusfarm.logic.commands;

import io.github.darzizalol.focusfarm.logic.CommandResult;
import io.github.darzizalol.focusfarm.logic.FarmCommand;
import io.github.darzizalol.focusfarm.logic.FarmEvent;
import io.github.darzizalol.focusfarm.model.CropType;
import io.github.darzizalol.focusfarm.model.Farm;

/** Displays the complete MVP command reference. */
public final class HelpCommand implements FarmCommand {
    @Override
    public CommandResult execute(Farm farm) {
        String help = """
                Commands:
                /plant <plot> <crop> <duration>  Example: /plant 2 carrot 10s
                /water <plot>                    Start that crop's timer
                /fertilize <plot>                Reduce remaining time by 25%% once
                /harvest <plot>                  Harvest a ready crop
                /status                          Show all plot states
                /help                            Show this guide
                /exit                            Close Focus Farm

                Crops: %s
                Durations: 10 seconds to 60 minutes, using s or m.
                """.formatted(CropType.availableCrops());
        return CommandResult.of(help.strip(), FarmEvent.HELP, null, false);
    }
}
