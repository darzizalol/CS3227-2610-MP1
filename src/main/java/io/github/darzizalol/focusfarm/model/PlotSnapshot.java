package io.github.darzizalol.focusfarm.model;

import java.time.Instant;

/**
 * Immutable plot data used by storage and presentation layers.
 *
 * @param id one-based plot identifier
 * @param crop planted crop, or {@code null} when empty
 * @param state current lifecycle state
 * @param growthSeconds configured growth duration in seconds
 * @param readyAt readiness time, or {@code null} before watering
 * @param fertilized whether fertilizer was applied
 * @param remainingSeconds displayed whole seconds until readiness
 */
public record PlotSnapshot(
        int id,
        CropType crop,
        PlotState state,
        long growthSeconds,
        Instant readyAt,
        boolean fertilized,
        long remainingSeconds) {
}
