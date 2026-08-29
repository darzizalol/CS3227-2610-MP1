package io.github.darzizalol.focusfarm.ui;

import javafx.application.Application;

/** Classpath-safe entry point for the visual-inspection harness. */
public final class UiSnapshotLauncher {
    private UiSnapshotLauncher() {
    }

    /** Launches the deterministic UI snapshot application. */
    public static void main(String[] args) {
        Application.launch(UiSnapshotApp.class, args);
    }
}
