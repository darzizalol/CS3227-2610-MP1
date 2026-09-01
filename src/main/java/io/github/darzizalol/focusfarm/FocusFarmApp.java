package io.github.darzizalol.focusfarm;

import java.nio.file.Path;
import java.time.Clock;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Duration;

import io.github.darzizalol.focusfarm.logic.LogicManager;
import io.github.darzizalol.focusfarm.model.FarmException;
import io.github.darzizalol.focusfarm.storage.JsonFarmStorage;
import io.github.darzizalol.focusfarm.ui.MainWindow;

/** JavaFX application lifecycle for Focus Farm. */
public final class FocusFarmApp extends Application {
    static final String SMOKE_TEST_ARGUMENT = "--smoke-test";
    private static final Duration SMOKE_TEST_DURATION = Duration.millis(750);

    private LogicManager logic;
    private MainWindow mainWindow;
    private PauseTransition smokeTestExit;

    /** Creates the JavaFX application. */
    public FocusFarmApp() {
    }

    @Override
    public void init() {
        JsonFarmStorage storage = new JsonFarmStorage(Path.of("data", "farm.json"));
        logic = new LogicManager(storage, Clock.systemUTC());
    }

    @Override
    public void start(Stage primaryStage) {
        mainWindow = new MainWindow(primaryStage, logic, Platform::exit);
        mainWindow.show();
        if (isSmokeTest(getParameters().getRaw())) {
            smokeTestExit = new PauseTransition(SMOKE_TEST_DURATION);
            smokeTestExit.setOnFinished(event -> Platform.exit());
            smokeTestExit.play();
        }
    }

    @Override
    public void stop() {
        if (smokeTestExit != null) {
            smokeTestExit.stop();
        }
        if (mainWindow != null) {
            mainWindow.stop();
        }
        if (logic != null) {
            try {
                logic.close();
            } catch (FarmException exception) {
                System.err.println("Focus Farm could not save during shutdown: " + exception.getMessage());
            }
        }
    }

    static boolean isSmokeTest(List<String> arguments) {
        return arguments.contains(SMOKE_TEST_ARGUMENT);
    }
}
