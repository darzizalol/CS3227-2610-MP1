package io.github.darzizalol.focusfarm.logic;

/**
 * Result of one successful command execution.
 *
 * @param feedback user-facing response
 * @param event cosmetic event for the UI
 * @param plotId affected plot, or {@code null}
 * @param stateChanged whether farm state changed
 * @param exitRequested whether the application should close
 */
public record CommandResult(
        String feedback,
        FarmEvent event,
        Integer plotId,
        boolean stateChanged,
        boolean exitRequested) {

    /**
     * Creates a non-exiting command result.
     *
     * @param feedback user-facing response
     * @param event cosmetic event for the UI
     * @param plotId affected plot, or {@code null}
     * @param stateChanged whether farm state changed
     * @return the command result
     */
    public static CommandResult of(String feedback, FarmEvent event, Integer plotId, boolean stateChanged) {
        return new CommandResult(feedback, event, plotId, stateChanged, false);
    }
}
