package io.github.darzizalol.focusfarm.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/** Encapsulates the state machine and timing data for one plot. */
public final class FarmPlot {
    public static final Duration MINIMUM_GROWTH_DURATION = Duration.ofSeconds(10);
    public static final Duration MAXIMUM_GROWTH_DURATION = Duration.ofMinutes(60);

    private final int id;
    private CropType crop;
    private PlotState state;
    private Duration growthDuration;
    private Instant readyAt;
    private boolean fertilized;

    /** Creates an empty plot with a stable one-based identifier. */
    public FarmPlot(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Plot ID must be positive.");
        }
        this.id = id;
        reset();
    }

    /** Restores a plot from validated persisted data. */
    public static FarmPlot restore(PlotSnapshot snapshot, Instant now) throws FarmException {
        Objects.requireNonNull(snapshot);
        FarmPlot plot = new FarmPlot(snapshot.id());
        plot.state = Objects.requireNonNull(snapshot.state());
        plot.crop = snapshot.crop();
        plot.growthDuration = Duration.ofSeconds(snapshot.growthSeconds());
        plot.readyAt = snapshot.readyAt();
        plot.fertilized = snapshot.fertilized();
        plot.validateRestoredState();
        plot.refresh(now);
        return plot;
    }

    /** Plants a crop without starting its timer. */
    public void plant(CropType cropType, Duration duration) throws FarmException {
        if (state != PlotState.EMPTY) {
            throw new FarmException("Plot " + id + " is occupied. Harvest its crop before planting again.");
        }
        validateDuration(duration);
        crop = Objects.requireNonNull(cropType);
        growthDuration = duration;
        state = PlotState.PLANTED;
        readyAt = null;
        fertilized = false;
    }

    /** Waters a planted crop and starts its authoritative growth timer. */
    public void water(Instant now) throws FarmException {
        if (state == PlotState.EMPTY) {
            throw new FarmException("Plot " + id + " is empty. Plant a crop before watering it.");
        }
        if (state != PlotState.PLANTED) {
            throw new FarmException("Plot " + id + " can only be watered once, immediately after planting.");
        }
        readyAt = Objects.requireNonNull(now).plus(growthDuration);
        state = PlotState.GROWING;
    }

    /** Applies fertilizer once, reducing the current remaining time by 25 percent. */
    public void fertilize(Instant now) throws FarmException {
        refresh(now);
        if (state != PlotState.GROWING) {
            throw new FarmException("Plot " + id + " can only be fertilized while its crop is growing.");
        }
        if (fertilized) {
            throw new FarmException("Plot " + id + " has already been fertilized for this crop.");
        }
        Duration remaining = Duration.between(now, readyAt);
        readyAt = readyAt.minus(remaining.dividedBy(4));
        fertilized = true;
    }

    /** Advances a growing plot to ready when its timestamp has elapsed. */
    public boolean refresh(Instant now) {
        if (state == PlotState.GROWING && !Objects.requireNonNull(now).isBefore(readyAt)) {
            state = PlotState.READY;
            return true;
        }
        return false;
    }

    /** Returns an independent mutable copy without advancing time-driven state. */
    FarmPlot copy() {
        FarmPlot copy = new FarmPlot(id);
        copy.crop = crop;
        copy.state = state;
        copy.growthDuration = growthDuration;
        copy.readyAt = readyAt;
        copy.fertilized = fertilized;
        return copy;
    }

    /** Harvests a ready crop and resets the plot. */
    public HarvestRecord harvest(Instant now) throws FarmException {
        refresh(now);
        if (state != PlotState.READY) {
            throw new FarmException("Plot " + id + " is not ready to harvest yet.");
        }
        HarvestRecord record = new HarvestRecord(crop, id, now, growthDuration.toSeconds());
        reset();
        return record;
    }

    /** Returns an immutable snapshot at the supplied instant. */
    public PlotSnapshot snapshot(Instant now) {
        long remainingSeconds = 0;
        if (state == PlotState.GROWING) {
            long remainingMillis = Duration.between(Objects.requireNonNull(now), readyAt).toMillis();
            remainingSeconds = Math.max(1, (remainingMillis + 999) / 1000);
        }
        long growthSeconds = growthDuration == null ? 0 : growthDuration.toSeconds();
        return new PlotSnapshot(id, crop, state, growthSeconds, readyAt, fertilized, remainingSeconds);
    }

    private void validateDuration(Duration duration) throws FarmException {
        Objects.requireNonNull(duration);
        if (duration.compareTo(MINIMUM_GROWTH_DURATION) < 0
                || duration.compareTo(MAXIMUM_GROWTH_DURATION) > 0) {
            throw new FarmException("Growth duration must be between 10 seconds and 60 minutes.");
        }
    }

    private void validateRestoredState() throws FarmException {
        if (state == PlotState.EMPTY) {
            reset();
            return;
        }
        if (crop == null || growthDuration.isZero() || growthDuration.isNegative()) {
            throw new FarmException("Plot " + id + " contains incomplete crop data.");
        }
        validateDuration(growthDuration);
        if ((state == PlotState.GROWING || state == PlotState.READY) && readyAt == null) {
            throw new FarmException("Plot " + id + " is missing its ready time.");
        }
        if (state == PlotState.PLANTED && readyAt != null) {
            throw new FarmException("Plot " + id + " has an unexpected ready time before watering.");
        }
    }

    private void reset() {
        crop = null;
        state = PlotState.EMPTY;
        growthDuration = null;
        readyAt = null;
        fertilized = false;
    }
}
