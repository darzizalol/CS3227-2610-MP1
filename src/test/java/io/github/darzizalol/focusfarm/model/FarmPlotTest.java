package io.github.darzizalol.focusfarm.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FarmPlotTest {
    private static final Instant START = Instant.parse("2026-08-29T00:00:00Z");

    private FarmPlot plot;

    @BeforeEach
    void setUp() {
        plot = new FarmPlot(1);
    }

    @Test
    void newPlot_hasEmptySnapshot() {
        PlotSnapshot snapshot = plot.snapshot(START);
        assertEquals(PlotState.EMPTY, snapshot.state());
        assertNull(snapshot.crop());
        assertEquals(0, snapshot.growthSeconds());
    }

    @Test
    void plant_validCrop_waitsForWater() throws Exception {
        plot.plant(CropType.CARROT, Duration.ofSeconds(10));
        PlotSnapshot snapshot = plot.snapshot(START);
        assertEquals(PlotState.PLANTED, snapshot.state());
        assertEquals(CropType.CARROT, snapshot.crop());
        assertNull(snapshot.readyAt());
    }

    @Test
    void plant_invalidDurations_rejected() {
        assertThrows(FarmException.class, () -> plot.plant(CropType.CARROT, Duration.ofSeconds(9)));
        assertThrows(FarmException.class, () -> plot.plant(CropType.CARROT, Duration.ofMinutes(61)));
    }

    @Test
    void occupiedPlot_cannotBePlantedAgain() throws Exception {
        plot.plant(CropType.CORN, Duration.ofSeconds(20));
        assertThrows(FarmException.class, () -> plot.plant(CropType.TOMATO, Duration.ofSeconds(20)));
    }

    @Test
    void water_startsAbsoluteTimerAndCannotRepeat() throws Exception {
        plot.plant(CropType.TOMATO, Duration.ofSeconds(10));
        plot.water(START);
        PlotSnapshot snapshot = plot.snapshot(START.plusSeconds(1));
        assertEquals(PlotState.GROWING, snapshot.state());
        assertEquals(10, snapshot.readyAt().getEpochSecond() - START.getEpochSecond());
        assertThrows(FarmException.class, () -> plot.water(START.plusSeconds(2)));
    }

    @Test
    void water_emptyPlot_rejected() {
        assertThrows(FarmException.class, () -> plot.water(START));
    }

    @Test
    void refresh_exactReadyInstant_marksReady() throws Exception {
        plot.plant(CropType.CABBAGE, Duration.ofSeconds(10));
        plot.water(START);
        assertFalse(plot.refresh(START.plusSeconds(9)));
        assertTrue(plot.refresh(START.plusSeconds(10)));
        assertEquals(PlotState.READY, plot.snapshot(START.plusSeconds(10)).state());
        assertFalse(plot.refresh(START.plusSeconds(11)));
    }

    @Test
    void fertilize_once_reducesRemainingTimeByQuarter() throws Exception {
        plot.plant(CropType.STRAWBERRY, Duration.ofSeconds(20));
        plot.water(START);
        plot.fertilize(START.plusSeconds(4));
        PlotSnapshot snapshot = plot.snapshot(START.plusSeconds(4));
        assertEquals(START.plusSeconds(16), snapshot.readyAt());
        assertTrue(snapshot.fertilized());
        assertThrows(FarmException.class, () -> plot.fertilize(START.plusSeconds(5)));
    }

    @Test
    void fertilize_wrongState_rejected() throws Exception {
        assertThrows(FarmException.class, () -> plot.fertilize(START));
        plot.plant(CropType.PUMPKIN, Duration.ofSeconds(10));
        assertThrows(FarmException.class, () -> plot.fertilize(START));
        plot.water(START);
        assertThrows(FarmException.class, () -> plot.fertilize(START.plusSeconds(10)));
    }

    @Test
    void harvest_readyCrop_returnsRecordAndResetsPlot() throws Exception {
        plot.plant(CropType.PUMPKIN, Duration.ofSeconds(10));
        plot.water(START);
        assertThrows(FarmException.class, () -> plot.harvest(START.plusSeconds(9)));
        HarvestRecord record = plot.harvest(START.plusSeconds(10));
        assertEquals(CropType.PUMPKIN, record.crop());
        assertEquals(1, record.plotId());
        assertEquals(PlotState.EMPTY, plot.snapshot(START.plusSeconds(10)).state());
    }

    @Test
    void restore_validGrowingPlot_refreshesAgainstCurrentTime() throws Exception {
        PlotSnapshot snapshot = new PlotSnapshot(1, CropType.CORN, PlotState.GROWING,
                10, START.plusSeconds(10), false, 10);
        FarmPlot restored = FarmPlot.restore(snapshot, START.plusSeconds(15));
        assertEquals(PlotState.READY, restored.snapshot(START.plusSeconds(15)).state());
    }

    @Test
    void restore_invalidSnapshots_rejected() {
        PlotSnapshot missingCrop = new PlotSnapshot(1, null, PlotState.PLANTED, 10, null, false, 0);
        PlotSnapshot missingReadyAt = new PlotSnapshot(1, CropType.CARROT, PlotState.GROWING,
                10, null, false, 0);
        PlotSnapshot earlyReadyAt = new PlotSnapshot(1, CropType.CARROT, PlotState.PLANTED,
                10, START, false, 0);
        assertThrows(FarmException.class, () -> FarmPlot.restore(missingCrop, START));
        assertThrows(FarmException.class, () -> FarmPlot.restore(missingReadyAt, START));
        assertThrows(FarmException.class, () -> FarmPlot.restore(earlyReadyAt, START));
    }
}
