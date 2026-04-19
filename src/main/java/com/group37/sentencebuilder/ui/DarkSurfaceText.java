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

    /**
     * Clears programmatic fills so stylesheet / palette tokens apply again (e.g. light mode after dark).
     * Uses the same deferred passes as {@link #forceLabeledFill} so stale {@code paintTextChild} runs from
     * a previous theme cannot repaint white after this clear (sidebar invisible in light mode).
     */
    public static void clearForcedLabeledPaint(Labeled labeled) {
        if (labeled == null) {
            return;
        }
        labeled.setTextFill(null);
        labeled.setStyle(null);
        clearTextChildFills(labeled);
        Platform.runLater(() -> clearTextChildFills(labeled));
        Platform.runLater(() -> Platform.runLater(() -> clearTextChildFills(labeled)));
    }

    private static void clearTextChildFills(Labeled labeled) {
        for (Node n : labeled.lookupAll(".text")) {
            if (n instanceof Text t) {
                if (t.fillProperty().isBound()) t.fillProperty().unbind();
                t.setFill(null);
                t.setStyle(null);
            }
        }
        Node n = labeled.lookup(".text");
        if (n instanceof Text t) {
            if (t.fillProperty().isBound()) t.fillProperty().unbind();
            t.setFill(null);
            t.setStyle(null);
        }
    }

    private static void paintTextChild(Labeled labeled, Color color) {
        Set<Node> nodes = labeled.lookupAll(".text");
        for (Node n : nodes) {
            if (n instanceof Text t) {
                if (t.fillProperty().isBound()) t.fillProperty().unbind();
                t.setFill(color);
            }
        }
        if (nodes.isEmpty()) {
            Node n = labeled.lookup(".text");
            if (n instanceof Text t) {
                if (t.fillProperty().isBound()) t.fillProperty().unbind();
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
