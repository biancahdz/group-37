package com.group37.sentencebuilder.ui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Set;

/**
 * JavaFX Modena often paints {@link Labeled} text via a child {@link Text} node whose fill does not
 * reliably track stylesheet or {@link Labeled#setTextFill}; this forces both the property and the leaf
 * shape so tertiary labels stay readable on near-black surfaces.
 */
public final class DarkSurfaceText {

    private DarkSurfaceText() {
    }

    public static void forceLabeledFill(Labeled labeled, Color color) {
        if (labeled == null || color == null) {
            return;
        }
        labeled.setTextFill(color);
        labeled.setStyle("-fx-text-fill: " + colorToCss(color) + "; -fx-opacity: 1;");
        paintTextChild(labeled, color);
        Platform.runLater(() -> paintTextChild(labeled, color));
        Platform.runLater(() -> Platform.runLater(() -> paintTextChild(labeled, color)));
    }

    private static void paintTextChild(Labeled labeled, Color color) {
        Set<Node> nodes = labeled.lookupAll(".text");
        for (Node n : nodes) {
            if (n instanceof Text t) {
                t.setFill(color);
            }
        }
        if (nodes.isEmpty()) {
            Node n = labeled.lookup(".text");
            if (n instanceof Text t) {
                t.setFill(color);
            }
        }
    }

    static String colorToCss(Color c) {
        if (c.isOpaque()) {
            return String.format("#%02x%02x%02x",
                    Math.round(c.getRed() * 255),
                    Math.round(c.getGreen() * 255),
                    Math.round(c.getBlue() * 255));
        }
        return String.format("rgba(%d,%d,%d,%.4f)",
                Math.round(c.getRed() * 255),
                Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255),
                c.getOpacity());
    }
}
