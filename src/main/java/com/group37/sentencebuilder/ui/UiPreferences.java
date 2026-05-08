/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    UiPreferences.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Singleton managing the active theme, font stack, and font size for the
 *      application shell. Updating any property reapplies CSS style classes on
 *      the shell root so the entire UI reflects the change immediately.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Hold and expose theme, font, and font size as JavaFX properties
 *      - Reapply shell style classes whenever a preference changes
 *      - Track OS color scheme changes for SYSTEM and INVERT_SYSTEM themes
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui;

import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.util.HashSet;
import java.util.Set;

/*
 * Single place for theme, font, and base font size. Updating these properties reapplies style classes
 * on the app shell so the whole UI picks up changes from one set of variables (CSS custom properties on the root).
 */
public final class UiPreferences {

    // Singleton — one set of preferences drives the entire app shell
    private static final UiPreferences INSTANCE = new UiPreferences();

    // JavaFX properties so any controller can listen for changes without polling
    private final ObjectProperty<AppTheme> theme =
            new SimpleObjectProperty<>(this, "theme", AppTheme.DEFAULT_THEME);
    private final ObjectProperty<AppFont> font =
            new SimpleObjectProperty<>(this, "font", AppFont.DEFAULT_FONT);
    private final ObjectProperty<FontSizePreset> fontSize =
            new SimpleObjectProperty<>(this, "fontSize", FontSizePreset.DEFAULT_SIZE);

    // Set once when the main shell FXML is loaded; null until attachShell() is called
    private Node shellRoot;

    // Guards against registering the OS color-scheme listener more than once
    private boolean colorSchemeListenerInstalled;

    private UiPreferences() {
        // Any preference change automatically refreshes the shell style classes
        theme.addListener((obs, o, n) -> applyShellStyles());
        font.addListener((obs, o, n) -> applyShellStyles());
        fontSize.addListener((obs, o, n) -> applyShellStyles());
    }

    public static UiPreferences get() {
        return INSTANCE;
    }

    /* Attach the main shell (scene root / BorderPane). Required for preferences to affect the UI. */
    public void attachShell(Node shellRoot) {
        this.shellRoot = shellRoot;
        ensureColorSchemeListener();
        applyShellStyles();
    }

    // Registers the OS color-scheme listener exactly once — safe to call on every attachShell
    private void ensureColorSchemeListener() {
        if (colorSchemeListenerInstalled) {
            return;
        }
        colorSchemeListenerInstalled = true;
        try {
            Platform.getPreferences().colorSchemeProperty().addListener((obs, o, n) -> {
                // Only SYSTEM and INVERT_SYSTEM care about the OS appearance signal
                AppTheme t = getTheme();
                if (t == AppTheme.SYSTEM || t == AppTheme.INVERT_SYSTEM) {
                    applyShellStyles();
                }
            });
        } catch (Exception ignored) {
            // Unsupported platform or JavaFX build without Platform preferences
        }
    }

    // Falls back to LIGHT if the platform doesn't expose a color scheme (e.g. older JavaFX builds)
    private static ColorScheme currentColorScheme() {
        try {
            return Platform.getPreferences().getColorScheme();
        } catch (Exception e) {
            return ColorScheme.LIGHT;
        }
    }

    public AppTheme getTheme() {
        return theme.get();
    }

    public void setTheme(AppTheme value) {
        theme.set(value);
    }

    public ObjectProperty<AppTheme> themeProperty() {
        return theme;
    }

    public AppFont getFont() {
        return font.get();
    }

    public void setFont(AppFont value) {
        font.set(value);
    }

    public ObjectProperty<AppFont> fontProperty() {
        return font;
    }

    public FontSizePreset getFontSize() {
        return fontSize.get();
    }

    public void setFontSize(FontSizePreset value) {
        fontSize.set(value);
    }

    public ObjectProperty<FontSizePreset> fontSizeProperty() {
        return fontSize;
    }

    private void applyShellStyles() {
        if (shellRoot == null || getTheme() == null || getFont() == null || getFontSize() == null) {
            return;
        }
        // Collect all previously applied managed classes first, then remove them in one batch
        // to avoid CSS re-triggering multiple times during the swap
        Set<String> toRemove = new HashSet<>();
        for (String c : shellRoot.getStyleClass()) {
            if (isManagedUiClass(c)) {
                toRemove.add(c);
            }
        }
        shellRoot.getStyleClass().removeAll(toRemove);

        // Resolve the palette class (handles SYSTEM / INVERT_SYSTEM → actual light or dark palette)
        String paletteClass = getTheme().resolvedPaletteStyleClass(currentColorScheme());
        shellRoot.getStyleClass().addAll(
                paletteClass,
                getFont().getStyleClass(),       // e.g. font-stack-default
                getFont().getMetricsStyleClass(), // e.g. font-metrics-relaxed (dense faces need extra tracking)
                getFontSize().getStyleClass());   // e.g. fs-medium
        // sb-dark-ui / sb-light-ui lets CSS and controllers detect surface brightness without
        // re-reading and switching on the theme name
        shellRoot.getStyleClass().add(isDarkSurfacePalette(paletteClass) ? "sb-dark-ui" : "sb-light-ui");
    }

    /*
     * Returns the CSS palette class currently applied on the shell root, resolving
     * SYSTEM / INVERT_SYSTEM against the OS color scheme at call time.
     */
    public String resolvedPaletteClass() {
        return getTheme().resolvedPaletteStyleClass(currentColorScheme());
    }

    /* Returns true when the active palette uses a dark canvas, meaning labels may need forced text fills. */
    public boolean isResolvedDarkSurface() {
        // Read directly from the shell’s style classes when available — they are always in sync with CSS
        if (shellRoot != null) {
            if (shellRoot.getStyleClass().contains("sb-dark-ui")) {
                return true;
            }
            if (shellRoot.getStyleClass().contains("sb-light-ui")) {
                return false;
            }
        }
        // Shell not attached yet — fall back to computing from the current theme and OS color scheme
        return isDarkSurfacePalette(getTheme().resolvedPaletteStyleClass(currentColorScheme()));
    }

    /*
     * Palettes with dark canvas/surfaces — drives .root.sb-dark-ui for shared contrast rules
     * (prompt text, optional Modena overrides) without listing every theme-* block again.
     */
    private static boolean isDarkSurfacePalette(String paletteClass) {
        if (paletteClass == null) {
            return false;
        }
        return switch (paletteClass) {
            case "theme-default",
                 "theme-christmas",
                 "theme-rainbow",
                 "theme-luna",
                 "theme-neon-noir",
                 "theme-deep-ink",
                 "theme-invert-system" -> true;
            default -> false;
        };
    }

    private static boolean isManagedUiClass(String c) {
        return c.startsWith("theme-")
                || c.startsWith("font-stack-")
                || c.startsWith("font-metrics-")
                || c.startsWith("fs-")
                || "sb-dark-ui".equals(c)
                || "sb-light-ui".equals(c);
    }
}
