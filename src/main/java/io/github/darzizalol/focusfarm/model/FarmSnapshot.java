package io.github.darzizalol.focusfarm.model;

import java.util.List;
import java.util.Map;

/**
 * Immutable view of the full farm.
 *
 * @param plots ordered plot states
 * @param inventory harvested crop counts
 * @param harvestHistory newest-first harvest records
 * @param totalHarvests total number of harvested crops
 */
public record FarmSnapshot(
        List<PlotSnapshot> plots,
        Map<CropType, Integer> inventory,
        List<HarvestRecord> harvestHistory,
        int totalHarvests) {
}
