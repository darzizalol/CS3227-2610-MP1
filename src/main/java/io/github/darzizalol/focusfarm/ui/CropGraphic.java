package io.github.darzizalol.focusfarm.ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import io.github.darzizalol.focusfarm.model.CropType;
import io.github.darzizalol.focusfarm.model.PlotSnapshot;
import io.github.darzizalol.focusfarm.model.PlotState;

/** Draws original, dependency-free crop graphics using simple JavaFX shapes. */
public final class CropGraphic extends Pane {
    private static final double WIDTH = 92;
    private static final double HEIGHT = 72;

    /** Creates an empty crop drawing area. */
    public CropGraphic() {
        setMinSize(WIDTH, HEIGHT);
        setPrefSize(WIDTH, HEIGHT);
        setMaxSize(WIDTH, HEIGHT);
    }

    /** Redraws the crop for the supplied plot state. */
    public void update(PlotSnapshot plot) {
        getChildren().clear();
        if (plot.state() == PlotState.EMPTY) {
            return;
        }
        if (plot.state() == PlotState.PLANTED) {
            Circle seed = new Circle(46, 61, 5, Color.web("#5b3716"));
            getChildren().add(seed);
            return;
        }

        double scale = plot.state() == PlotState.READY ? 1.0 : growthScale(plot);
        Rectangle stem = new Rectangle(43, 28, 6, 34);
        stem.setFill(Color.web("#397b35"));
        Ellipse leftLeaf = new Ellipse(37, 42, 12, 6);
        leftLeaf.setRotate(25);
        leftLeaf.setFill(Color.web("#62a744"));
        Ellipse rightLeaf = new Ellipse(55, 36, 12, 6);
        rightLeaf.setRotate(-25);
        rightLeaf.setFill(Color.web("#76bd4f"));
        getChildren().addAll(stem, leftLeaf, rightLeaf);

        if (plot.state() == PlotState.READY) {
            drawProduce(plot.crop());
        }
        setScaleX(scale);
        setScaleY(scale);
    }

    private double growthScale(PlotSnapshot plot) {
        if (plot.growthSeconds() <= 0) {
            return 0.65;
        }
        double completed = 1.0 - (double) plot.remainingSeconds() / plot.growthSeconds();
        return Math.max(0.55, Math.min(0.9, 0.55 + completed * 0.35));
    }

    private void drawProduce(CropType crop) {
        switch (crop) {
        case CARROT -> drawCarrot();
        case TOMATO -> drawRoundProduce(Color.web("#df453d"), 14);
        case CORN -> drawCorn();
        case STRAWBERRY -> drawStrawberry();
        case PUMPKIN -> drawRoundProduce(Color.web("#ec8e27"), 18);
        case CABBAGE -> drawRoundProduce(Color.web("#8ccf68"), 18);
        default -> throw new IllegalStateException("Unhandled crop type: " + crop);
        }
    }

    private void drawCarrot() {
        Ellipse root = new Ellipse(46, 51, 10, 20);
        root.setFill(Color.web("#ef7b2d"));
        getChildren().add(root);
    }

    private void drawRoundProduce(Color color, double radius) {
        Circle produce = new Circle(46, 48, radius, color);
        produce.setStroke(color.darker());
        produce.setStrokeWidth(3);
        getChildren().add(produce);
    }

    private void drawCorn() {
        Ellipse cob = new Ellipse(46, 48, 11, 22);
        cob.setFill(Color.web("#f5c948"));
        cob.setStroke(Color.web("#d3a630"));
        getChildren().add(cob);
    }

    private void drawStrawberry() {
        Ellipse berry = new Ellipse(46, 49, 14, 17);
        berry.setFill(Color.web("#e5484d"));
        berry.setStroke(Color.web("#a62c35"));
        getChildren().add(berry);
    }
}
