package io.github.darzizalol.focusfarm.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.darzizalol.focusfarm.model.Farm;
import io.github.darzizalol.focusfarm.model.FarmSnapshot;
import io.github.darzizalol.focusfarm.model.CropType;
import io.github.darzizalol.focusfarm.testutil.MutableClock;

class JsonFarmStorageTest {
    private static final Instant NOW = Instant.parse("2026-08-29T12:34:56Z");

    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmpty() throws Exception {
        JsonFarmStorage storage = new JsonFarmStorage(temporaryDirectory.resolve("data/farm.json"));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveThenLoad_roundTripsSnapshotAndCreatesDirectory() throws Exception {
        Path dataPath = temporaryDirectory.resolve("nested/data/farm.json");
        JsonFarmStorage storage = new JsonFarmStorage(dataPath);
        FarmSnapshot original = new Farm(Clock.fixed(NOW, ZoneOffset.UTC)).snapshot();
        storage.save(original);

        assertTrue(Files.exists(dataPath));
        FarmSnapshot restored = storage.load().orElseThrow();
        assertEquals(6, restored.plots().size());
        assertEquals(0, restored.totalHarvests());
        assertFalse(Files.exists(dataPath.resolveSibling("farm.json.tmp")));
    }

    @Test
    void saveThenLoad_nonEmptyFarmPreservesTimersInventoryAndHistory() throws Exception {
        Path dataPath = temporaryDirectory.resolve("farm.json");
        JsonFarmStorage storage = new JsonFarmStorage(dataPath);
        MutableClock clock = new MutableClock(NOW);
        Farm farm = new Farm(clock);
        farm.plant(1, CropType.CARROT, Duration.ofSeconds(10));
        farm.water(1);
        farm.plant(2, CropType.TOMATO, Duration.ofSeconds(20));
        farm.water(2);
        clock.advance(Duration.ofSeconds(10));
        farm.harvest(1);
        FarmSnapshot original = farm.snapshot();

        storage.save(original);
        FarmSnapshot restored = storage.load().orElseThrow();

        assertEquals(original, restored);
        assertEquals(1, restored.inventory().get(CropType.CARROT));
        assertEquals(1, restored.harvestHistory().size());
    }

    @Test
    void load_corruptFile_movesRecoverableBackup() throws Exception {
        Path dataPath = temporaryDirectory.resolve("farm.json");
        Files.writeString(dataPath, "not-json");
        JsonFarmStorage storage = new JsonFarmStorage(dataPath, Clock.fixed(NOW, ZoneOffset.UTC));

        StorageException exception = assertThrows(StorageException.class, storage::load);
        Path backup = temporaryDirectory.resolve("farm.json.corrupt-20260829-123456");
        assertTrue(exception.getMessage().contains(backup.toString()));
        assertFalse(Files.exists(dataPath));
        assertTrue(Files.exists(backup));
    }

    @Test
    void load_wrongSchema_movesRecoverableBackup() throws Exception {
        Path dataPath = temporaryDirectory.resolve("farm.json");
        Files.writeString(dataPath, "{\"schemaVersion\":99,\"farm\":null}");
        JsonFarmStorage storage = new JsonFarmStorage(dataPath, Clock.fixed(NOW, ZoneOffset.UTC));
        assertThrows(StorageException.class, storage::load);
        assertTrue(Files.exists(temporaryDirectory.resolve("farm.json.corrupt-20260829-123456")));
    }
}
