package io.github.darzizalol.focusfarm.ui;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.darzizalol.focusfarm.logic.LogicManager;
import io.github.darzizalol.focusfarm.model.FarmException;

/** Retries countdown refreshes while reporting storage outages without flooding the transcript. */
final class CountdownRefreshCoordinator {
    private final LogicManager logic;
    private final Runnable refreshUi;
    private final Consumer<String> appendFarm;
    private final Consumer<String> appendError;
    private boolean failureReported;

    /**
     * Creates a coordinator for one countdown timeline.
     *
     * @param logic farm logic to refresh
     * @param refreshUi callback that redraws farm views
     * @param appendFarm callback for recovery messages
     * @param appendError callback for persistence errors
     */
    CountdownRefreshCoordinator(LogicManager logic, Runnable refreshUi, Consumer<String> appendFarm,
            Consumer<String> appendError) {
        this.logic = Objects.requireNonNull(logic);
        this.refreshUi = Objects.requireNonNull(refreshUi);
        this.appendFarm = Objects.requireNonNull(appendFarm);
        this.appendError = Objects.requireNonNull(appendError);
    }

    /** Attempts one refresh and leaves subsequent timer ticks available for retries. */
    void refresh() {
        try {
            logic.refreshGrowth();
            refreshUi.run();
            if (failureReported) {
                appendFarm.accept("Countdown refresh recovered.");
                failureReported = false;
            }
        } catch (FarmException exception) {
            if (!failureReported) {
                appendError.accept(exception.getMessage() + " Countdown refresh will retry automatically.");
                failureReported = true;
            }
        }
    }
}
