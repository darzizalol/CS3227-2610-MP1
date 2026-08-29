package io.github.darzizalol.focusfarm.storage;

import java.util.Optional;

import io.github.darzizalol.focusfarm.model.FarmSnapshot;

/** Persistence boundary for Focus Farm data. */
public interface FarmStorage {
    /**
     * Loads the saved farm.
     *
     * @return saved state, or empty if no data file exists
     * @throws StorageException if existing data cannot be read safely
     */
    Optional<FarmSnapshot> load() throws StorageException;

    /**
     * Saves the complete farm state.
     *
     * @param snapshot state to persist
     * @throws StorageException if the state cannot be written safely
     */
    void save(FarmSnapshot snapshot) throws StorageException;
}
