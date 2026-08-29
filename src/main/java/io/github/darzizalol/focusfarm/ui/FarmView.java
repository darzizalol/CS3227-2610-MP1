package io.github.darzizalol.focusfarm.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import io.github.darzizalol.focusfarm.logic.FarmEvent;
import io.github.darzizalol.focusfarm.model.FarmSnapshot;
import io.github.darzizalol.focusfarm.model.PlotSnapshot;

/** Pixel-inspired 2D field containing the six plot cards. */
public final class FarmView extends VBox {
    private final Map<Integer, PlotView> plotViews = new LinkedHashMap<>();

    /** Creates the two-row, three-column farm. */
    public FarmView() {
        getStyleClass().add("farm-view");
        setPadding(new Insets(18, 22, 20, 22));
        setSpacing(12);
        setAlignment(Pos.TOP_CENTER);

        Label fieldSign = new Label("SUNNYBROOK FIELD • SIX PLOTS");
        fieldSign.getStyleClass().add("field-sign");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        VBox.setVgrow(grid, Priority.ALWAYS);
        for (int column = 0; column < 3; column++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(33.333);
            constraints.setHalignment(HPos.CENTER);
            constraints.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(constraints);
        }
        for (int plotId = 1; plotId <= 6; plotId++) {
            PlotView plotView = new PlotView(plotId);
            plotView.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(plotView, Priority.ALWAYS);
            GridPane.setVgrow(plotView, Priority.ALWAYS);
            grid.add(plotView, (plotId - 1) % 3, (plotId - 1) / 3);
            plotViews.put(plotId, plotView);
        }
        getChildren().addAll(fieldSign, grid);
    }

    /**
     * Refreshes all six plots.
     *
     * @param snapshot farm state to display
     */
    public void update(FarmSnapshot snapshot) {
        for (PlotSnapshot plot : snapshot.plots()) {
            plotViews.get(plot.id()).update(plot);
        }
    }

    /**
     * Plays a plot animation for a successful state-changing command.
     *
     * @param event animation category
     * @param plotId affected plot, or {@code null} for a farm-wide event
     */
    public void animate(FarmEvent event, Integer plotId) {
        if (plotId != null && plotViews.containsKey(plotId)) {
            plotViews.get(plotId).animate(event);
        }
    }
}
