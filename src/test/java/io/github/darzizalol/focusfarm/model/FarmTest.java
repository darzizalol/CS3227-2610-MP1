package io.github.darzizalol.focusfarm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.darzizalol.focusfarm.testutil.MutableClock;

class FarmTest {
    private MutableClock clock;
    private Farm farm;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(Instant.parse("2026-08-29T00:00:00Z"));
        farm = new Farm(clock);
    }

    @Test
    void newFarm_hasSixEmptyPlotsAndZeroInventory() {
        FarmSnapshot snapshot = farm.snapshot();
        assertEquals(6, snapshot.plots().size());
        assertEquals(0, snapshot.totalHarvests());
        assertTrue(snapshot.inventory().values().stream().allMatch(count -> count == 0));
    }

    @Test
    void fullLifecycle_updatesInventoryAndHistory() throws Exception {
        farm.plant(3, CropType.CARROT, Duration.ofSeconds(10));
        farm.water(3);
        clock.advance(Duration.ofSeconds(10));
        assertTrue(farm.refreshGrowth());
        HarvestRecord record = farm.harvest(3);
        FarmSnapshot snapshot = farm.snapshot();
        assertEquals(CropType.CARROT, record.crop());
        assertEquals(1, snapshot.inventory().get(CropType.CARROT));
        assertEquals(1, snapshot.totalHarvests());
        assertEquals(record, snapshot.harvestHistory().get(0));
    }

    @Test
    void snapshot_elapsedCrop_doesNotAdvanceLifecycle() throws Exception {
        farm.plant(1, CropType.CARROT, Duration.ofSeconds(10));
        farm.water(1);
        clock.advance(Duration.ofSeconds(10));

        assertEquals(PlotState.GROWING, farm.snapshot().plots().get(0).state());
        assertTrue(farm.refreshGrowth());
        assertEquals(PlotState.READY, farm.snapshot().plots().get(0).state());
    }

    @Test
    void invalidPlotIds_rejectedByAllOperations() {
        assertThrows(FarmException.class, () -> farm.plant(0, CropType.CORN, Duration.ofSeconds(10)));
        assertThrows(FarmException.class, () -> farm.water(7));
        assertThrows(FarmException.class, () -> farm.fertilize(-1));
        assertThrows(FarmException.class, () -> farm.harvest(9));
    }

    @Test
    void restore_preservesPlotsInventoryAndHistory() throws Exception {
        farm.plant(1, CropType.TOMATO, Duration.ofSeconds(20));
        farm.water(1);
        FarmSnapshot saved = farm.snapshot();
        Farm restored = Farm.restore(clock, saved);
        assertEquals(CropType.TOMATO, restored.snapshot().plots().get(0).crop());
        assertEquals(PlotState.GROWING, restored.snapshot().plots().get(0).state());
    }

    @Test
    void restore_invalidShapeAndCounts_rejected() {
        FarmSnapshot wrongPlotCount = new FarmSnapshot(List.of(), Map.of(), List.of(), 0);
        assertThrows(FarmException.class, () -> Farm.restore(clock, wrongPlotCount));

        List<PlotSnapshot> unordered = new ArrayList<>(farm.snapshot().plots());
        unordered.set(0, new PlotSnapshot(2, null, PlotState.EMPTY, 0, null, false, 0));
        assertThrows(FarmException.class,
                () -> Farm.restore(clock, new FarmSnapshot(unordered, Map.of(), List.of(), 0)));

        EnumMap<CropType, Integer> invalidCounts = new EnumMap<>(CropType.class);
        invalidCounts.put(CropType.CORN, -1);
        assertThrows(FarmException.class,
                () -> Farm.restore(clock, new FarmSnapshot(farm.snapshot().plots(), invalidCounts, List.of(), 0)));
    }
}
