package io.github.darzizalol.focusfarm.model;

import java.util.List;
import java.util.Map;

/** Immutable view of the full farm. */
public record FarmSnapshot(
        List<PlotSnapshot> plots,
        Map<CropType, Integer> inventory,
        List<HarvestRecord> harvestHistory,
        int totalHarvests) {
}
