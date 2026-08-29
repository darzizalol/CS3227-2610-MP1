package io.github.darzizalol.focusfarm.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import io.github.darzizalol.focusfarm.logic.LogicManager;
import io.github.darzizalol.focusfarm.model.FarmSnapshot;
import io.github.darzizalol.focusfarm.storage.FarmStorage;
import io.github.darzizalol.focusfarm.storage.StorageException;
import io.github.darzizalol.focusfarm.testutil.MutableClock;

/** Deterministic visual-inspection harness for the JavaFX interface. */
public final class UiSnapshotApp extends Application {
    private MainWindow mainWindow;

    /** Launches the snapshot harness. */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MutableClock clock = new MutableClock(Instant.parse("2026-08-29T08:00:00Z"));
        LogicManager logic = new LogicManager(new MemoryStorage(), clock);
        logic.execute("/plant 1 carrot 30s");
        logic.execute("/water 1");
        logic.execute("/plant 2 tomato 1m");
        logic.execute("/plant 3 corn 10s");
        logic.execute("/water 3");
        logic.execute("/plant 4 strawberry 1m");
        logic.execute("/water 4");
        clock.advance(Duration.ofSeconds(10));
        logic.refreshGrowth();

        mainWindow = new MainWindow(stage, logic, Platform::exit);
        mainWindow.show();

        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(750));
        pause.setOnFinished(event -> saveSnapshot(stage));
        pause.play();
    }

    @Override
    public void stop() {
        if (mainWindow != null) {
            mainWindow.stop();
        }
    }

    private void saveSnapshot(Stage stage) {
        Path outputPath = Path.of("build", "visual", "focus-farm.png");
        try {
            Files.createDirectories(outputPath.getParent());
            WritableImage image = stage.getScene().snapshot(null);
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputPath.toFile());
        } catch (IOException exception) {
            throw new IllegalStateException("Could not save UI preview.", exception);
        } finally {
            Platform.exit();
        }
    }

    private static final class MemoryStorage implements FarmStorage {
        private FarmSnapshot snapshot;

        @Override
        public Optional<FarmSnapshot> load() {
            return Optional.ofNullable(snapshot);
        }

        @Override
        public void save(FarmSnapshot newSnapshot) throws StorageException {
            snapshot = newSnapshot;
        }
    }
}
