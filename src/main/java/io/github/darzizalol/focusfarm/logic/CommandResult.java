package io.github.darzizalol.focusfarm.logic;

/** Result of one successful command execution. */
public record CommandResult(
        String feedback,
        FarmEvent event,
        Integer plotId,
        boolean stateChanged,
        boolean exitRequested) {

    /** Creates a non-exiting command result. */
    public static CommandResult of(String feedback, FarmEvent event, Integer plotId, boolean stateChanged) {
        return new CommandResult(feedback, event, plotId, stateChanged, false);
    }
}
