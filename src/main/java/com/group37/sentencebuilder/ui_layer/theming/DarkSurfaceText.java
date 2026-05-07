package com.group37.sentencebuilder.ui_layer.theming;

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
        forceLabeledFill(labeled, color, null);
    }

    /**
     * Like {@link #forceLabeledFill(Labeled, Color)} but prepends {@code baseStyle} to the inline
     * style (e.g. {@code "-fx-font-size: 26px; -fx-font-weight: bold;"}).  The base properties
     * survive both dark and light passes so CSS-resistant labels (e.g. inside Button graphics)
     * always get the correct size/weight without relying on stylesheet cascade.
     */
    public static void forceLabeledFill(Labeled labeled, Color color, String baseStyle) {
        if (labeled == null || color == null) {
            return;
        }
        String css = colorToCss(color);
        String textFill = "-fx-fill: " + css + ";";
        String prefix = (baseStyle != null && !baseStyle.isBlank()) ? baseStyle.stripTrailing() + " " : "";
        String labelStyle = prefix + "-fx-text-fill: " + css + "; -fx-opacity: 1;";
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
        clearForcedLabeledPaint(labeled, null);
    }

    /**
     * Like {@link #clearForcedLabeledPaint(Labeled)} but keeps {@code keepStyle} as the inline
     * style so that base properties (font-size, font-weight) are preserved in light mode even
     * though text-fill is handed back to the CSS cascade.
     */
    public static void clearForcedLabeledPaint(Labeled labeled, String keepStyle) {
        if (labeled == null) {
            return;
        }
        labeled.setStyle(keepStyle);
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
