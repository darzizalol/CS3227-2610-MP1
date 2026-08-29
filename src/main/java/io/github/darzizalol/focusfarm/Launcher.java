package io.github.darzizalol.focusfarm;

import javafx.application.Application;

/** Classpath-safe entry point for the packaged JavaFX application. */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches Focus Farm.
     *
     * @param args JavaFX launch arguments
     */
    public static void main(String[] args) {
        Application.launch(FocusFarmApp.class, args);
    }
}
