package com.group37.sentencebuilder.ui;

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
        if (label != null && darkColor != null) {
            entries.add(new Entry(label, () -> darkColor));
        }
        return this;
    }

    /**
     * Register a label whose dark-mode color is computed at apply time.
     * Use this when the color depends on context that may change (e.g. accent class of a parent).
     */
    public LabelThemeRegistry add(Labeled label, Supplier<Color> darkColorFn) {
        if (label != null && darkColorFn != null) {
            entries.add(new Entry(label, darkColorFn));
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
                DarkSurfaceText.forceLabeledFill(e.label, e.darkColor.get());
            } else {
                DarkSurfaceText.clearForcedLabeledPaint(e.label);
            }
        }
    }

    private record Entry(Labeled label, Supplier<Color> darkColor) {}
}
