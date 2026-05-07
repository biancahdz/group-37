/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    DarkSurfaceText.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Utility that forces text fill on Labeled nodes where Modena's LabeledSkin ignores stylesheet fills on dark surfaces.
 *
 *  Version: 1.0
 *  Created: 2026-04-10
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Force and clear inline text fills on Labeled nodes for dark/light palette switching
 *      - Propagate fill to inner Text nodes of LabeledSkin via a deferred retry on the next pulse
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.theming;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Forces text color on Labeled nodes where Modena's LabeledSkin ignores stylesheet fills.
 * Uses only inline setStyle — never setFill/unbind — so that clearForcedLabeledPaint can
 * call setStyle(null) and let the CSS cascade restore the correct palette color.
 */
public final class DarkSurfaceText {

    private DarkSurfaceText() {
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Forces the text fill color on a Labeled node for dark surface compatibility.
     *      Delegates to the three-argument overload with no base style.
     *
     * @param labeled the Labeled node to apply the fill to
     * @param color the fill color to apply
     */
    public static void forceLabeledFill(Labeled labeled, Color color) {
        forceLabeledFill(labeled, color, null);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Forces the text fill color on a Labeled node, optionally prepending a base inline
     *      style (e.g. "-fx-font-size: 26px; -fx-font-weight: bold;") so that CSS-resistant
     *      labels inside Button graphics always get the correct size and weight. A deferred
     *      retry on the next pulse handles labels whose skin does not yet exist at call time.
     *
     * @param labeled the Labeled node to apply the fill to
     * @param color the fill color to apply
     * @param baseStyle optional inline style to prepend; null or blank to omit
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
     * Author: Sebastian Sarinana
     * Description:
     *      Removes all inline fills so the CSS palette cascade takes over automatically.
     *      Delegates to the two-argument overload with no style to keep.
     *
     * @param labeled the Labeled node to restore
     */
    public static void clearForcedLabeledPaint(Labeled labeled) {
        clearForcedLabeledPaint(labeled, null);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Removes forced text fills and optionally keeps a base inline style (font-size,
     *      font-weight) so that CSS-resistant labels still get the correct metrics in light
     *      mode while text-fill is handed back to the CSS cascade.
     *
     * @param labeled the Labeled node to restore
     * @param keepStyle inline style to preserve after clearing fills; null to clear completely
     */
    public static void clearForcedLabeledPaint(Labeled labeled, String keepStyle) {
        if (labeled == null) {
            return;
        }
        labeled.setStyle(keepStyle);
        applyTextChildStyle(labeled, null);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Applies an inline style to all inner Text nodes of a Labeled (both via lookupAll
     *      and a direct lookup) to ensure the fill is set even when the skin is partially built.
     *
     * @param labeled the Labeled whose inner Text nodes will be styled
     * @param style the inline style to apply; null clears the style
     */
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

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Converts a JavaFX Color to a CSS hex string (#rrggbb) for opaque colors or an
     *      rgba() string for translucent colors, for use in inline style properties.
     *
     * @param c the color to convert
     * @return CSS color string
     */
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
