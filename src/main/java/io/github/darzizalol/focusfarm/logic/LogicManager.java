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
    private final Farm farm;
    private final String startupMessage;

    /** Loads an existing farm or starts a recoverable empty farm. */
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

    /** Executes one command and persists successful state changes. */
    public CommandResult execute(String input) throws FarmException {
        FarmCommand command = parser.parse(input);
        CommandResult result = command.execute(farm);
        if (result.stateChanged()) {
            save();
        }
        return result;
    }

    /** Refreshes time-driven state and saves only when a crop becomes ready. */
    public boolean refreshGrowth() throws FarmException {
        boolean changed = farm.refreshGrowth();
        if (changed) {
            save();
        }
        return changed;
    }

    /** Returns the current immutable farm view. */
    public FarmSnapshot snapshot() {
        return farm.snapshot();
    }

    /** Returns the startup or recovery message for the chat panel. */
    public String startupMessage() {
        return startupMessage;
    }

    /** Persists current state before application shutdown. */
    public void close() throws FarmException {
        save();
    }

    private void save() throws FarmException {
        try {
            storage.save(farm.snapshot());
        } catch (StorageException exception) {
            throw new FarmException(exception.getMessage(), exception);
        }
    }
}
