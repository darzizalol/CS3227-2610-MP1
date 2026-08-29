package io.github.darzizalol.focusfarm.model;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Owns the six plots, harvest inventory, and harvest history. */
public final class Farm {
    /** Number of plots in every farm. */
    public static final int PLOT_COUNT = 6;
    private static final int MAXIMUM_HISTORY_SIZE = 100;

    private final Clock clock;
    private final List<FarmPlot> plots;
    private final EnumMap<CropType, Integer> inventory;
    private final List<HarvestRecord> harvestHistory;

    /**
     * Creates an empty six-plot farm.
     *
     * @param clock authoritative clock for crop timing
     */
    public Farm(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
        plots = new ArrayList<>();
        for (int id = 1; id <= PLOT_COUNT; id++) {
            plots.add(new FarmPlot(id));
        }
        inventory = emptyInventory();
        harvestHistory = new ArrayList<>();
    }

    /**
     * Restores a farm from persisted data.
     *
     * @param clock authoritative clock for crop timing
     * @param snapshot persisted farm state
     * @return the restored farm
     * @throws FarmException if the snapshot is inconsistent
     */
    public static Farm restore(Clock clock, FarmSnapshot snapshot) throws FarmException {
        Objects.requireNonNull(snapshot);
        if (snapshot.plots() == null || snapshot.plots().size() != PLOT_COUNT) {
            throw new FarmException("Saved farm must contain exactly six plots.");
        }
        Farm farm = new Farm(clock);
        farm.plots.clear();
        Instant now = clock.instant();
        for (int index = 0; index < PLOT_COUNT; index++) {
            PlotSnapshot plotSnapshot = snapshot.plots().get(index);
            if (plotSnapshot.id() != index + 1) {
                throw new FarmException("Saved plot IDs must be ordered from 1 to 6.");
            }
            farm.plots.add(FarmPlot.restore(plotSnapshot, now));
        }
        farm.restoreInventory(snapshot.inventory());
        if (snapshot.harvestHistory() != null) {
            farm.harvestHistory.addAll(snapshot.harvestHistory().stream()
                    .filter(Objects::nonNull)
                    .limit(MAXIMUM_HISTORY_SIZE)
                    .toList());
        }
        return farm;
    }

    /**
     * Plants a crop in an empty plot.
     *
     * @param plotId one-based plot identifier
     * @param crop crop to plant
     * @param duration configured growth duration
     * @throws FarmException if the plot or duration is invalid
     */
    public void plant(int plotId, CropType crop, Duration duration) throws FarmException {
        findPlot(plotId).plant(crop, duration);
    }

    /**
     * Waters a planted plot and starts its timer.
     *
     * @param plotId one-based plot identifier
     * @throws FarmException if the plot cannot be watered
     */
    public void water(int plotId) throws FarmException {
        findPlot(plotId).water(clock.instant());
    }

    /**
     * Fertilizes a growing plot.
     *
     * @param plotId one-based plot identifier
     * @throws FarmException if the plot cannot be fertilized
     */
    public void fertilize(int plotId) throws FarmException {
        findPlot(plotId).fertilize(clock.instant());
    }

    /**
     * Harvests a ready crop and records it in the inventory.
     *
     * @param plotId one-based plot identifier
     * @return the completed harvest
     * @throws FarmException if the plot is not ready
     */
    public HarvestRecord harvest(int plotId) throws FarmException {
        HarvestRecord record = findPlot(plotId).harvest(clock.instant());
        inventory.compute(record.crop(), (crop, count) -> Objects.requireNonNull(count) + 1);
        harvestHistory.add(0, record);
        if (harvestHistory.size() > MAXIMUM_HISTORY_SIZE) {
            harvestHistory.remove(harvestHistory.size() - 1);
        }
        return record;
    }

    /**
     * Refreshes plot states from the authoritative clock.
     *
     * @return {@code true} if at least one crop became ready
     */
    public boolean refreshGrowth() {
        boolean changed = false;
        Instant now = clock.instant();
        for (FarmPlot plot : plots) {
            changed |= plot.refresh(now);
        }
        return changed;
    }

    /**
     * Copies the farm without advancing time-driven state.
     *
     * @return an independent mutable copy
     */
    public Farm copy() {
        Farm copy = new Farm(clock);
        copy.plots.clear();
        for (FarmPlot plot : plots) {
            copy.plots.add(plot.copy());
        }
        copy.inventory.putAll(inventory);
        copy.harvestHistory.addAll(harvestHistory);
        return copy;
    }

    /**
     * Captures the current farm state.
     *
     * @return an immutable farm snapshot
     */
    public FarmSnapshot snapshot() {
        Instant now = clock.instant();
        List<PlotSnapshot> plotSnapshots = plots.stream().map(plot -> plot.snapshot(now)).toList();
        Map<CropType, Integer> inventorySnapshot = Collections.unmodifiableMap(new EnumMap<>(inventory));
        List<HarvestRecord> historySnapshot = List.copyOf(harvestHistory);
        int totalHarvests = inventory.values().stream().mapToInt(Integer::intValue).sum();
        return new FarmSnapshot(plotSnapshots, inventorySnapshot, historySnapshot, totalHarvests);
    }

    private FarmPlot findPlot(int plotId) throws FarmException {
        if (plotId < 1 || plotId > PLOT_COUNT) {
            throw new FarmException("Plot number must be between 1 and 6.");
        }
        return plots.get(plotId - 1);
    }

    private void restoreInventory(Map<CropType, Integer> restoredInventory) throws FarmException {
        if (restoredInventory == null) {
            return;
        }
        for (Map.Entry<CropType, Integer> entry : restoredInventory.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() < 0) {
                throw new FarmException("Saved inventory contains an invalid crop count.");
            }
            inventory.put(entry.getKey(), entry.getValue());
        }
    }

    private static EnumMap<CropType, Integer> emptyInventory() {
        EnumMap<CropType, Integer> counts = new EnumMap<>(CropType.class);
        for (CropType crop : CropType.values()) {
            counts.put(crop, 0);
        }
        return counts;
    }
}
