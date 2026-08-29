package io.github.darzizalol.focusfarm.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.darzizalol.focusfarm.model.FarmException;
import io.github.darzizalol.focusfarm.model.FarmSnapshot;
import io.github.darzizalol.focusfarm.model.PlotState;
import io.github.darzizalol.focusfarm.storage.FarmStorage;
import io.github.darzizalol.focusfarm.storage.StorageException;
import io.github.darzizalol.focusfarm.testutil.MutableClock;

class LogicManagerTest {
    private MutableClock clock;
    private MemoryStorage storage;
    private LogicManager logic;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(Instant.parse("2026-08-29T00:00:00Z"));
        storage = new MemoryStorage();
        logic = new LogicManager(storage, clock);
    }

    @Test
    void execute_fullCommandFlow_updatesAndPersistsFarm() throws Exception {
        assertEquals(FarmEvent.PLANTED, logic.execute("/plant 1 carrot 10s").event());
        assertEquals(FarmEvent.WATERED, logic.execute("/water 1").event());
        assertEquals(FarmEvent.FERTILIZED, logic.execute("/fertilize 1").event());
        assertEquals(3, storage.saveCount);

        clock.advance(Duration.ofSeconds(8));
        assertTrue(logic.refreshGrowth());
        assertEquals(PlotState.READY, logic.snapshot().plots().get(0).state());

        CommandResult harvest = logic.execute("/harvest 1");
        assertEquals(FarmEvent.HARVESTED, harvest.event());
        assertEquals(1, logic.snapshot().totalHarvests());
        assertEquals(5, storage.saveCount);
    }

    @Test
    void execute_readOnlyCommands_doNotSave() throws Exception {
        assertEquals(FarmEvent.HELP, logic.execute("/help").event());
        assertEquals(FarmEvent.STATUS, logic.execute("/status").event());
        CommandResult exit = logic.execute("/exit");
        assertTrue(exit.exitRequested());
        assertEquals(0, storage.saveCount);
        logic.close();
        assertEquals(1, storage.saveCount);
    }

    @Test
    void refreshGrowth_beforeReady_doesNotSave() throws Exception {
        logic.execute("/plant 2 tomato 10s");
        logic.execute("/water 2");
        int savesBeforeRefresh = storage.saveCount;
        clock.advance(Duration.ofSeconds(9));
        assertFalse(logic.refreshGrowth());
        assertEquals(savesBeforeRefresh, storage.saveCount);
    }

    @Test
    void constructor_savedFarm_restoresState() throws Exception {
        logic.execute("/plant 4 corn 20s");
        LogicManager restored = new LogicManager(storage, clock);
        assertTrue(restored.startupMessage().contains("loaded"));
        assertEquals(PlotState.PLANTED, restored.snapshot().plots().get(3).state());
    }

    @Test
    void constructor_loadFailure_startsEmptyFarmWithWarning() {
        storage.failLoad = true;
        LogicManager recovered = new LogicManager(storage, clock);
        assertTrue(recovered.startupMessage().contains("empty farm"));
        assertEquals(0, recovered.snapshot().totalHarvests());
    }

    @Test
    void execute_saveFailure_reportedAsFarmError() {
        storage.failSave = true;
        assertThrows(FarmException.class, () -> logic.execute("/plant 1 cabbage 10s"));
    }

    private static final class MemoryStorage implements FarmStorage {
        private FarmSnapshot snapshot;
        private int saveCount;
        private boolean failLoad;
        private boolean failSave;

        @Override
        public Optional<FarmSnapshot> load() throws StorageException {
            if (failLoad) {
                throw new StorageException("Simulated load failure.", new IllegalStateException());
            }
            return Optional.ofNullable(snapshot);
        }

        @Override
        public void save(FarmSnapshot newSnapshot) throws StorageException {
            if (failSave) {
                throw new StorageException("Simulated save failure.", new IllegalStateException());
            }
            snapshot = newSnapshot;
            saveCount++;
        }
    }
}
