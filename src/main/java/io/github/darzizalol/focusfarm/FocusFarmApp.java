package io.github.darzizalol.focusfarm;

import java.nio.file.Path;
import java.time.Clock;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import io.github.darzizalol.focusfarm.logic.LogicManager;
import io.github.darzizalol.focusfarm.model.FarmException;
import io.github.darzizalol.focusfarm.storage.JsonFarmStorage;
import io.github.darzizalol.focusfarm.ui.MainWindow;

/** JavaFX application lifecycle for Focus Farm. */
public final class FocusFarmApp extends Application {
    private LogicManager logic;
    private MainWindow mainWindow;

    @Override
    public void init() {
        JsonFarmStorage storage = new JsonFarmStorage(Path.of("data", "farm.json"));
        logic = new LogicManager(storage, Clock.systemUTC());
    }

    @Override
    public void start(Stage primaryStage) {
        mainWindow = new MainWindow(primaryStage, logic, Platform::exit);
        mainWindow.show();
    }

    @Override
    public void stop() {
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
}
