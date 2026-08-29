package io.github.darzizalol.focusfarm.ui;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import io.github.darzizalol.focusfarm.model.CropType;
import io.github.darzizalol.focusfarm.model.FarmSnapshot;
import io.github.darzizalol.focusfarm.model.HarvestRecord;
import io.github.darzizalol.focusfarm.model.PlotState;

/** Harvest inventory and concise farm statistics shown below the field. */
public final class DashboardView extends VBox {
    private static final DateTimeFormatter HARVEST_TIME = DateTimeFormatter.ofPattern("dd MMM HH:mm")
            .withZone(ZoneId.systemDefault());

    private final Map<CropType, Label> cropCounts = new EnumMap<>(CropType.class);
    private final Label totalsLabel;
    private final Label recentLabel;

    /** Creates the dashboard controls. */
    public DashboardView() {
        getStyleClass().add("dashboard");
        setSpacing(8);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("HARVEST DASHBOARD");
        title.getStyleClass().add("dashboard-title");
        totalsLabel = new Label();
        totalsLabel.getStyleClass().add("dashboard-totals");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, totalsLabel);

        FlowPane inventory = new FlowPane(8, 6);
        for (CropType crop : CropType.values()) {
            Label count = new Label();
            count.getStyleClass().add("inventory-chip");
            cropCounts.put(crop, count);
            inventory.getChildren().add(count);
        }

        recentLabel = new Label("Recent: no harvests yet");
        recentLabel.getStyleClass().add("recent-harvest");
        getChildren().addAll(header, inventory, recentLabel);
    }

    /** Refreshes counts and recent harvest information. */
    public void update(FarmSnapshot snapshot) {
        for (CropType crop : CropType.values()) {
            cropCounts.get(crop).setText(crop.displayName() + "  " + snapshot.inventory().getOrDefault(crop, 0));
        }
        long growing = snapshot.plots().stream().filter(plot -> plot.state() == PlotState.GROWING).count();
        long ready = snapshot.plots().stream().filter(plot -> plot.state() == PlotState.READY).count();
        totalsLabel.setText("Total " + snapshot.totalHarvests() + "  •  Growing " + growing + "  •  Ready " + ready);
        if (snapshot.harvestHistory().isEmpty()) {
            recentLabel.setText("Recent: no harvests yet");
        } else {
            HarvestRecord recent = snapshot.harvestHistory().get(0);
            recentLabel.setText("Recent: " + recent.crop().displayName() + " from plot " + recent.plotId()
                    + " • " + HARVEST_TIME.format(recent.harvestedAt()));
        }
    }
}
