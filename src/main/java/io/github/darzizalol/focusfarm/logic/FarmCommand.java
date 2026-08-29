package io.github.darzizalol.focusfarm.logic;

import io.github.darzizalol.focusfarm.model.Farm;
import io.github.darzizalol.focusfarm.model.FarmException;

/** Executable command that operates on the farm model. */
public interface FarmCommand {
    /**
     * Executes this command against a farm.
     *
     * @param farm farm to inspect or modify
     * @return the command result
     * @throws FarmException if the requested operation is invalid
     */
    CommandResult execute(Farm farm) throws FarmException;
}
