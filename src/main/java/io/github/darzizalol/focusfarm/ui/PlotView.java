package io.github.darzizalol.focusfarm.ui;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import io.github.darzizalol.focusfarm.logic.FarmEvent;
import io.github.darzizalol.focusfarm.model.PlotSnapshot;
import io.github.darzizalol.focusfarm.model.PlotState;

/** Visual card for one of the six farm plots. */
public final class PlotView extends VBox {
    private final Label cropLabel;
    private final Label statusLabel;
    private final CropGraphic cropGraphic;
    private PlotState displayedState;

    /**
     * Creates a plot card with a stable plot number.
     *
     * @param plotId one-based plot identifier
     */
    public PlotView(int plotId) {
        getStyleClass().add("plot-card");
        setAlignment(Pos.CENTER);
        setSpacing(4);

        Label plotLabel = new Label("PLOT " + plotId);
        plotLabel.getStyleClass().add("plot-number");

        cropLabel = new Label("EMPTY");
        cropLabel.getStyleClass().add("crop-name");

        cropGraphic = new CropGraphic();
        StackPane soil = new StackPane(cropGraphic);
        soil.getStyleClass().add("soil-bed");
        soil.setMinHeight(86);

        statusLabel = new Label("Ready for planting");
        statusLabel.getStyleClass().add("plot-status");

        getChildren().addAll(plotLabel, cropLabel, soil, statusLabel);
    }

    /**
     * Refreshes labels and crop stage from immutable model state.
     *
     * @param plot plot state to display
     */
    public void update(PlotSnapshot plot) {
        displayedState = plot.state();
        cropLabel.setText(plot.crop() == null ? "EMPTY" : plot.crop().displayName().toUpperCase());
        statusLabel.setText(statusText(plot));
        cropGraphic.update(plot);
        getStyleClass().removeAll("plot-empty", "plot-planted", "plot-growing", "plot-ready");
        getStyleClass().add("plot-" + plot.state().name().toLowerCase());
    }

    /**
     * Plays a short cosmetic pulse after a successful plot command.
     *
     * @param event event that controls pulse emphasis
     */
    public void animate(FarmEvent event) {
        double peak = event == FarmEvent.FERTILIZED ? 1.12 : 1.06;
        ScaleTransition pulse = new ScaleTransition(Duration.millis(180), this);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(peak);
        pulse.setToY(peak);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    private String statusText(PlotSnapshot plot) {
        return switch (plot.state()) {
        case EMPTY -> "Ready for planting";
        case PLANTED -> "Needs water";
        case GROWING -> plot.remainingSeconds() + "s remaining" + (plot.fertilized() ? " • fertilized" : "");
        case READY -> "READY TO HARVEST";
        };
    }
}
