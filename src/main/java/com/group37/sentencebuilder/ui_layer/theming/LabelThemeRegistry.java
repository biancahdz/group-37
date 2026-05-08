/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    LabelThemeRegistry.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Registry that maps Labeled nodes to dark-mode fill colors; apply() is called on page enter and theme change.
 *
 *  Version: 1.0
 *  Created: 2026-04-19
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Register Labeled nodes with fixed or computed dark-mode colors for theme-aware styling
 *      - Apply or clear inline fills via DarkSurfaceText so controllers stay decoupled from it
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.theming;

import javafx.application.Platform;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Maps Labeled nodes to their dark-mode fill colors. Register labels once in FXML initialize()
 * via add(), then call apply() in onPageEnter() and from theme-change listeners.
 * Controllers never import DarkSurfaceText directly — the switching logic is encapsulated here.
 */
public final class LabelThemeRegistry {

    private final List<Entry> entries = new ArrayList<>();

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Registers a label with a fixed dark-mode color. Delegates to the three-argument
     *      overload with no base style.
     *
     * @param label the Labeled node to register
     * @param darkColor the fill color to apply in dark mode
     * @return this registry instance for chaining
     */
    public LabelThemeRegistry add(Labeled label, Color darkColor) {
        return add(label, darkColor, (String) null);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Registers a label with a fixed dark-mode color and a base inline style that is
     *      preserved in both dark and light modes (e.g. "-fx-font-size: 26px; -fx-font-weight: bold;").
     *      Use this for labels inside Button graphics where stylesheet selectors may not cascade.
     *
     * @param label the Labeled node to register
     * @param darkColor the fill color to apply in dark mode
     * @param baseStyle inline style to preserve in both modes; null to omit
     * @return this registry instance for chaining
     */
    public LabelThemeRegistry add(Labeled label, Color darkColor, String baseStyle) {
        if (label != null && darkColor != null) {
            entries.add(new Entry(label, () -> darkColor, baseStyle));
        }
        return this;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Registers a label whose dark-mode color is computed at apply time. Use this when
     *      the color depends on context that may change (e.g. the accent class of a parent).
     *
     * @param label the Labeled node to register
     * @param darkColorFn supplier that returns the fill color at apply time
     * @return this registry instance for chaining
     */
    public LabelThemeRegistry add(Labeled label, Supplier<Color> darkColorFn) {
        return add(label, darkColorFn, null);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Registers a label with a computed dark-mode color and a base inline style preserved
     *      in both dark and light modes.
     *
     * @param label the Labeled node to register
     * @param darkColorFn supplier that returns the fill color at apply time
     * @param baseStyle inline style to preserve in both modes; null to omit
     * @return this registry instance for chaining
     */
    public LabelThemeRegistry add(Labeled label, Supplier<Color> darkColorFn, String baseStyle) {
        if (label != null && darkColorFn != null) {
            entries.add(new Entry(label, darkColorFn, baseStyle));
        }
        return this;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Applies theme colors for every registered label now, and again after the next
     *      layout pulse. The deferred pass handles labels whose skin node was not yet built
     *      on the first call.
     */
    public void apply() {
        applyNow();
        Platform.runLater(this::applyNow);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Iterates all registered entries and either forces dark fills or clears them
     *      based on the current resolved surface brightness from UiPreferences.
     */
    private void applyNow() {
        boolean dark = UiPreferences.get().isResolvedDarkSurface();
        for (Entry e : entries) {
            if (dark) {
                DarkSurfaceText.forceLabeledFill(e.label, e.darkColor.get(), e.baseStyle);
            } else {
                DarkSurfaceText.clearForcedLabeledPaint(e.label, e.baseStyle);
            }
        }
    }

    private record Entry(Labeled label, Supplier<Color> darkColor, String baseStyle) {}
}
