package io.github.darzizalol.focusfarm.logic.commands;

import io.github.darzizalol.focusfarm.logic.CommandResult;
import io.github.darzizalol.focusfarm.logic.FarmCommand;
import io.github.darzizalol.focusfarm.logic.FarmEvent;
import io.github.darzizalol.focusfarm.model.Farm;

/** Requests a graceful application exit. */
public final class ExitCommand implements FarmCommand {
    @Override
    public CommandResult execute(Farm farm) {
        return new CommandResult("Farm saved. See you next harvest!", FarmEvent.EXIT, null, false, true);
    }
}
