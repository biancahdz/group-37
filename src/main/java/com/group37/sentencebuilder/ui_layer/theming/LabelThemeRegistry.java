package com.group37.sentencebuilder.ui_layer.theming;

import javafx.application.Platform;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Maps labeled nodes to their dark-mode fill colors.
 *
 * Register labels once in FXML {@code initialize()} via {@link #add}, then call
 * {@link #apply} in {@code onPageEnter()} and from theme-change listeners.
 *
 * Controllers never import {@link DarkSurfaceText} directly; the theme-switching
 * logic is encapsulated here.
 */
public final class LabelThemeRegistry {

    private final List<Entry> entries = new ArrayList<>();

    /** Register a label with a fixed dark-mode color. Returns {@code this} for chaining. */
    public LabelThemeRegistry add(Labeled label, Color darkColor) {
        return add(label, darkColor, (String) null);
    }

    /**
     * Register a label with a fixed dark-mode color and a base inline style that is preserved in
     * both dark and light modes (e.g. {@code "-fx-font-size: 26px; -fx-font-weight: bold;"}).
     * Use this for labels inside Button graphics where stylesheet selectors may not cascade.
     */
    public LabelThemeRegistry add(Labeled label, Color darkColor, String baseStyle) {
        if (label != null && darkColor != null) {
            entries.add(new Entry(label, () -> darkColor, baseStyle));
        }
        return this;
    }

    /**
     * Register a label whose dark-mode color is computed at apply time.
     * Use this when the color depends on context that may change (e.g. accent class of a parent).
     */
    public LabelThemeRegistry add(Labeled label, Supplier<Color> darkColorFn) {
        return add(label, darkColorFn, null);
    }

    /** Like {@link #add(Labeled, Supplier, String)} but with a base inline style. */
    public LabelThemeRegistry add(Labeled label, Supplier<Color> darkColorFn, String baseStyle) {
        if (label != null && darkColorFn != null) {
            entries.add(new Entry(label, darkColorFn, baseStyle));
        }
        return this;
    }

    /**
     * Apply theme colors for every registered label now, and again after the next layout pulse.
     * The deferred pass handles labels whose skin node was not yet built on the first call.
     */
    public void apply() {
        applyNow();
        Platform.runLater(this::applyNow);
    }

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
