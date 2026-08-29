package io.github.darzizalol.focusfarm.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.github.darzizalol.focusfarm.logic.LogicManager;
import io.github.darzizalol.focusfarm.model.FarmSnapshot;
import io.github.darzizalol.focusfarm.model.PlotState;
import io.github.darzizalol.focusfarm.storage.FarmStorage;
import io.github.darzizalol.focusfarm.storage.StorageException;
import io.github.darzizalol.focusfarm.testutil.MutableClock;

class CountdownRefreshCoordinatorTest {
    @Test
    void refresh_saveFailure_retriesUntilRecoveryWithoutRepeatingError() throws Exception {
        MutableClock clock = new MutableClock(Instant.parse("2026-08-29T00:00:00Z"));
        RecoverableStorage storage = new RecoverableStorage();
        LogicManager logic = new LogicManager(storage, clock);
        logic.execute("/plant 1 carrot 10s");
        logic.execute("/water 1");
        clock.advance(Duration.ofSeconds(10));
        storage.failSave = true;

        List<String> farmMessages = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        int[] uiRefreshes = {0};
        CountdownRefreshCoordinator coordinator = new CountdownRefreshCoordinator(logic,
                () -> uiRefreshes[0]++, farmMessages::add, errorMessages::add);

        coordinator.refresh();
        coordinator.refresh();

        assertEquals(0, uiRefreshes[0]);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.get(0).contains("retry automatically"));
        assertEquals(PlotState.GROWING, logic.snapshot().plots().get(0).state());

        storage.failSave = false;
        coordinator.refresh();
        coordinator.refresh();

        assertEquals(2, uiRefreshes[0]);
        assertEquals(List.of("Countdown refresh recovered."), farmMessages);
        assertEquals(1, errorMessages.size());
        assertEquals(PlotState.READY, logic.snapshot().plots().get(0).state());
    }

    private static final class RecoverableStorage implements FarmStorage {
        private FarmSnapshot snapshot;
        private boolean failSave;

        @Override
        public Optional<FarmSnapshot> load() {
            return Optional.ofNullable(snapshot);
        }

        @Override
        public void save(FarmSnapshot newSnapshot) throws StorageException {
            if (failSave) {
                throw new StorageException("Simulated save failure.", new IllegalStateException());
            }
            snapshot = newSnapshot;
        }
    }
}
