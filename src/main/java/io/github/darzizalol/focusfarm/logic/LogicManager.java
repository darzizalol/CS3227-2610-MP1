package io.github.darzizalol.focusfarm.logic;

import java.time.Clock;
import java.util.Objects;
import java.util.Optional;

import io.github.darzizalol.focusfarm.model.Farm;
import io.github.darzizalol.focusfarm.model.FarmException;
import io.github.darzizalol.focusfarm.model.FarmSnapshot;
import io.github.darzizalol.focusfarm.storage.FarmStorage;
import io.github.darzizalol.focusfarm.storage.StorageException;

/** Coordinates parsing, domain changes, and persistence. */
public final class LogicManager {
    private final FarmStorage storage;
    private final CommandParser parser;
    private final String startupMessage;
    private Farm farm;

    /**
     * Loads a saved farm or starts an empty farm after a recoverable load failure.
     *
     * @param storage persistence provider
     * @param clock authoritative clock for crop timing
     */
    public LogicManager(FarmStorage storage, Clock clock) {
        this.storage = Objects.requireNonNull(storage);
        parser = new CommandParser();
        Farm loadedFarm;
        String message;
        try {
            Optional<FarmSnapshot> savedFarm = storage.load();
            loadedFarm = savedFarm.isPresent() ? Farm.restore(clock, savedFarm.get()) : new Farm(clock);
            message = savedFarm.isPresent() ? "Welcome back. Your saved farm has been loaded."
                    : "Welcome to Focus Farm. Type /help to begin.";
        } catch (StorageException | FarmException exception) {
            loadedFarm = new Farm(clock);
            message = exception.getMessage() + " Focus Farm started with an empty farm.";
        }
        farm = loadedFarm;
        startupMessage = message;
    }

    /**
     * Executes one command and persists successful state changes.
     *
     * @param input command text
     * @return the command result
     * @throws FarmException if parsing, execution, or persistence fails
     */
    public CommandResult execute(String input) throws FarmException {
        FarmCommand command = parser.parse(input);
        Farm candidate = farm.copy();
        CommandResult result = command.execute(candidate);
        if (result.stateChanged()) {
            commit(candidate);
        } else if (result.exitRequested()) {
            save(candidate.snapshot());
        }
        return result;
    }

    /**
     * Refreshes time-driven state and saves new ready crops.
     *
     * @return {@code true} if at least one crop became ready
     * @throws FarmException if changed state cannot be saved
     */
    public boolean refreshGrowth() throws FarmException {
        Farm candidate = farm.copy();
        boolean changed = candidate.refreshGrowth();
        if (changed) {
            commit(candidate);
        }
        return changed;
    }

    /**
     * Returns the current immutable farm view.
     *
     * @return the farm snapshot
     */
    public FarmSnapshot snapshot() {
        return farm.snapshot();
    }

    /**
     * Returns the startup or recovery message.
     *
     * @return message for the chat panel
     */
    public String startupMessage() {
        return startupMessage;
    }

    /**
     * Persists current state before shutdown.
     *
     * @throws FarmException if the farm cannot be saved
     */
    public void close() throws FarmException {
        save(farm.snapshot());
    }

    private void commit(Farm candidate) throws FarmException {
        save(candidate.snapshot());
        farm = candidate;
    }

    private void save(FarmSnapshot snapshot) throws FarmException {
        try {
            storage.save(snapshot);
        } catch (StorageException exception) {
            throw new FarmException(exception.getMessage(), exception);
        }
    }
}
