package io.github.darzizalol.focusfarm.storage;

import java.util.Optional;

import io.github.darzizalol.focusfarm.model.FarmSnapshot;

/** Persistence boundary for Focus Farm data. */
public interface FarmStorage {
    /** Loads a saved farm, or returns empty when no data file exists yet. */
    Optional<FarmSnapshot> load() throws StorageException;

    /** Saves the complete current farm state. */
    void save(FarmSnapshot snapshot) throws StorageException;
}
