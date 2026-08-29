package io.github.darzizalol.focusfarm.logic;

import io.github.darzizalol.focusfarm.model.Farm;
import io.github.darzizalol.focusfarm.model.FarmException;

/** Executable command that operates on the farm model. */
public interface FarmCommand {
    /** Executes the command and returns its user-facing result. */
    CommandResult execute(Farm farm) throws FarmException;
}
