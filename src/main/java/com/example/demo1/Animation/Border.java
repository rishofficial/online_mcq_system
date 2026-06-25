package com.example.demo1.Animation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Border {

    public static StackPane wrapWithAnimatedBorder(Node content, double arc, double strokeWidth, Color borderColor, double animDurationMillis) {
        StackPane pane = new StackPane();
        pane.setPadding(new javafx.geometry.Insets(8));

        Rectangle rect = new Rectangle();
        rect.setFill(Color.TRANSPARENT);
        rect.setArcWidth(arc);
        rect.setArcHeight(arc);
        rect.setStroke(borderColor);
        rect.setStrokeWidth(strokeWidth);
        rect.getStrokeDashArray().setAll(400.0, 400.0);
        rect.setStrokeDashOffset(400);
        rect.setVisible(false);

        pane.getChildren().addAll(rect, content);

        // Adjust border when container is resized
        pane.widthProperty().addListener((o, oldW, newW) ->
                rect.setWidth(newW.doubleValue() - 4));
        pane.heightProperty().addListener((o, oldH, newH) ->
                rect.setHeight(newH.doubleValue() - 4));

        // Animation objects
        Timeline showAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rect.strokeDashOffsetProperty(), 400)),
                new KeyFrame(Duration.millis(animDurationMillis), new KeyValue(rect.strokeDashOffsetProperty(), 0))
        );
        Timeline hideAnim = new Timeline(
                new KeyFrame(Duration.millis(animDurationMillis / 1.5), new KeyValue(rect.strokeDashOffsetProperty(), 400))
        );

        // Public BooleanProperty to allow hover "linking"
        BooleanProperty revealed = new SimpleBooleanProperty(false);

        // Listen to property
        revealed.addListener((obs, oldVal, show) -> {
            if (show) {
                rect.setVisible(true);
                hideAnim.stop();
                showAnim.playFromStart();
            } else {
                showAnim.stop();
                hideAnim.setOnFinished(e -> rect.setVisible(false));
                hideAnim.playFromStart();
            }
        });

        // Attach a property to the pane for external hover control
        pane.getProperties().put("border:revealed", revealed);

        return pane;
    }

    // Helper to reveal/hide from StackPane (for use in ListCell etc.)
    @SuppressWarnings("unchecked")
    public static void setRevealed(StackPane pane, boolean shown) {
        BooleanProperty prop = (BooleanProperty) pane.getProperties().get("border:revealed");
        if (prop != null) prop.set(shown);
    }
}
