package com.group37.sentencebuilder.ui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Forces text color on {@link Labeled} nodes where Modena's LabeledSkin ignores stylesheet fills.
 *
 * Uses only inline {@code setStyle} — never {@code setFill}/{@code unbind} — so that
 * {@code clearForcedLabeledPaint} can simply call {@code setStyle(null)} and let the CSS
 * cascade restore the correct palette color without any transparent-text side effects.
 *
 * The single deferred retry in {@code forceLabeledFill} handles the common case where the
 * Label's skin (and its inner Text node) does not yet exist during FXML initialize. It guards
 * itself by checking that the label's inline style hasn't been cleared (i.e. the theme hasn't
 * already switched back to light), so it never overwrites a light-mode clear.
 */
public final class DarkSurfaceText {

    private DarkSurfaceText() {
    }

    public static void forceLabeledFill(Labeled labeled, Color color) {
        if (labeled == null || color == null) {
            return;
        }
        String css = colorToCss(color);
        String textFill = "-fx-fill: " + css + ";";
        String labelStyle = "-fx-text-fill: " + css + "; -fx-opacity: 1;";
        labeled.setStyle(labelStyle);
        applyTextChildStyle(labeled, textFill);
        // Retry once after the next pulse in case the skin/Text node didn't exist yet.
        // The guard ensures this no-ops if clearForcedLabeledPaint has already been called.
        Platform.runLater(() -> {
            String current = labeled.getStyle();
            if (current != null && !current.isEmpty()) {
                applyTextChildStyle(labeled, textFill);
            }
        });
    }

    /**
     * Removes all inline fills so the CSS palette takes over automatically.
     * No deferred calls — inline styles are removed immediately and CSS re-applies on the
     * next pulse without any race against a queued forceLabeledFill retry (the retry guards
     * itself by checking the label's style).
     */
    public static void clearForcedLabeledPaint(Labeled labeled) {
        if (labeled == null) {
            return;
        }
        labeled.setStyle(null);
        applyTextChildStyle(labeled, null);
    }

    private static void applyTextChildStyle(Labeled labeled, String style) {
        for (Node n : labeled.lookupAll(".text")) {
            if (n instanceof Text t) {
                t.setStyle(style);
            }
        }
        Node n = labeled.lookup(".text");
        if (n instanceof Text t) {
            t.setStyle(style);
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
