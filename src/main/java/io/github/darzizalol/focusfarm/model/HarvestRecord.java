package io.github.darzizalol.focusfarm.model;

import java.time.Instant;

/** Immutable record of one completed harvest. */
public record HarvestRecord(CropType crop, int plotId, Instant harvestedAt, long configuredGrowthSeconds) {
}
