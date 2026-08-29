package io.github.darzizalol.focusfarm.ui;

import java.util.Objects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import io.github.darzizalol.focusfarm.logic.CommandResult;
import io.github.darzizalol.focusfarm.logic.LogicManager;
import io.github.darzizalol.focusfarm.model.FarmException;
import io.github.darzizalol.focusfarm.model.FarmSnapshot;

/** Main application window joining farm, dashboard, and terminal. */
public final class MainWindow {
    private final Stage stage;
    private final LogicManager logic;
    private final Runnable exitHandler;
    private final FarmView farmView;
    private final DashboardView dashboardView;
    private final ChatPanel chatPanel;
    private final CountdownRefreshCoordinator refreshCoordinator;
    private final Timeline refreshTimeline;

    /** Creates the complete 70:30 Focus Farm window. */
    public MainWindow(Stage stage, LogicManager logic, Runnable exitHandler) {
        this.stage = Objects.requireNonNull(stage);
        this.logic = Objects.requireNonNull(logic);
        this.exitHandler = Objects.requireNonNull(exitHandler);

        farmView = new FarmView();
        dashboardView = new DashboardView();
        chatPanel = new ChatPanel(this::executeCommand);
        refreshCoordinator = new CountdownRefreshCoordinator(logic, this::refreshFarm,
                chatPanel::appendFarm, chatPanel::appendError);

        VBox farmSide = createFarmSide();
        SplitPane splitPane = new SplitPane(farmSide, chatPanel);
        splitPane.setDividerPositions(0.70);
        splitPane.getStyleClass().add("main-split");

        Scene scene = new Scene(splitPane, 1280, 800);
        scene.getStylesheets().add(Objects.requireNonNull(
                MainWindow.class.getResource("/styles/focus-farm.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Focus Farm — Pixel Countdown Timer");
        stage.setMinWidth(980);
        stage.setMinHeight(650);

        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> refreshCoordinator.refresh()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshFarm();
        chatPanel.appendFarm(logic.startupMessage());
    }

    /** Displays the window and starts countdown refreshes. */
    public void show() {
        stage.show();
        refreshTimeline.play();
        Platform.runLater(chatPanel::focusInput);
    }

    /** Stops the finite-lifetime JavaFX ticker. */
    public void stop() {
        refreshTimeline.stop();
    }

    private VBox createFarmSide() {
        VBox farmSide = new VBox();
        farmSide.getStyleClass().add("farm-side");

        Label title = new Label("FOCUS FARM");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Plant a timer • Water to begin • Harvest when ready");
        subtitle.getStyleClass().add("app-subtitle");
        VBox titleBlock = new VBox(2, title, subtitle);
        titleBlock.setAlignment(Pos.CENTER_LEFT);
        titleBlock.setPadding(new Insets(14, 22, 12, 22));
        titleBlock.getStyleClass().add("title-block");

        VBox.setVgrow(farmView, Priority.ALWAYS);
        farmSide.getChildren().addAll(titleBlock, farmView, dashboardView);
        return farmSide;
    }

    private void executeCommand(String commandText) {
        chatPanel.appendUser(commandText);
        try {
            CommandResult result = logic.execute(commandText);
            chatPanel.appendFarm(result.feedback());
            refreshFarm();
            farmView.animate(result.event(), result.plotId());
            if (result.exitRequested()) {
                Platform.runLater(exitHandler);
            }
        } catch (FarmException exception) {
            chatPanel.appendError(exception.getMessage());
        }
    }

    private void refreshFarm() {
        FarmSnapshot snapshot = logic.snapshot();
        farmView.update(snapshot);
        dashboardView.update(snapshot);
    }
}
