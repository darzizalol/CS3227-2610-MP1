package io.github.darzizalol.focusfarm.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.darzizalol.focusfarm.model.FarmSnapshot;

/** Stores farm snapshots as human-readable JSON with recoverable atomic writes. */
public final class JsonFarmStorage implements FarmStorage {
    private static final int SCHEMA_VERSION = 1;
    private static final DateTimeFormatter BACKUP_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneOffset.UTC);

    private final Path dataPath;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    /** Creates JSON storage at the supplied path. */
    public JsonFarmStorage(Path dataPath) {
        this(dataPath, Clock.systemUTC());
    }

    /** Creates JSON storage with an injectable clock for deterministic backup names. */
    public JsonFarmStorage(Path dataPath, Clock clock) {
        this.dataPath = Objects.requireNonNull(dataPath);
        this.clock = Objects.requireNonNull(clock);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public Optional<FarmSnapshot> load() throws StorageException {
        if (!Files.exists(dataPath)) {
            return Optional.empty();
        }
        try {
            StoredFarm storedFarm = objectMapper.readValue(dataPath.toFile(), StoredFarm.class);
            if (storedFarm.schemaVersion() != SCHEMA_VERSION || storedFarm.farm() == null) {
                throw new IOException("Unsupported or incomplete farm data.");
            }
            return Optional.of(storedFarm.farm());
        } catch (IOException exception) {
            Path backupPath = backupCorruptData();
            String backupMessage = backupPath == null ? "The original file could not be backed up."
                    : "The original file was moved to " + backupPath + ".";
            throw new StorageException("Saved farm data is unreadable. " + backupMessage, exception);
        }
    }

    @Override
    public void save(FarmSnapshot snapshot) throws StorageException {
        Objects.requireNonNull(snapshot);
        Path parent = dataPath.toAbsolutePath().getParent();
        Path temporaryPath = dataPath.resolveSibling(dataPath.getFileName() + ".tmp");
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            objectMapper.writeValue(temporaryPath.toFile(), new StoredFarm(SCHEMA_VERSION, snapshot));
            try {
                Files.move(temporaryPath, dataPath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryPath, dataPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new StorageException("Could not save farm data to " + dataPath + ".", exception);
        }
    }

    private Path backupCorruptData() {
        String fileName = dataPath.getFileName().toString();
        Path backupPath = dataPath.resolveSibling(fileName + ".corrupt-" + BACKUP_TIMESTAMP.format(clock.instant()));
        try {
            return Files.move(dataPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            return null;
        }
    }

    /** On-disk envelope that allows future schema migrations. */
    public record StoredFarm(int schemaVersion, FarmSnapshot farm) {
    }
}
