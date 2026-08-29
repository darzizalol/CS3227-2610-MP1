package io.github.darzizalol.focusfarm.model;

import java.time.Instant;

/** Immutable plot data used by storage and presentation layers. */
public record PlotSnapshot(
        int id,
        CropType crop,
        PlotState state,
        long growthSeconds,
        Instant readyAt,
        boolean fertilized,
        long remainingSeconds) {
}
