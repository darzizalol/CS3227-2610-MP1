package io.github.darzizalol.focusfarm.model;

import java.time.Instant;

/**
 * Immutable record of one completed harvest.
 *
 * @param crop harvested crop
 * @param plotId source plot identifier
 * @param harvestedAt completion time
 * @param configuredGrowthSeconds original growth duration in seconds
 */
public record HarvestRecord(CropType crop, int plotId, Instant harvestedAt, long configuredGrowthSeconds) {
}
