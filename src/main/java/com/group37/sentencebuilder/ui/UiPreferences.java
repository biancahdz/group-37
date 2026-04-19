package com.group37.sentencebuilder.ui;

import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Single place for theme, font, and base font size. Updating these properties reapplies style classes
 * on the app shell so the whole UI picks up changes from one set of variables (CSS custom properties on the root).
 */
public final class UiPreferences {

    private static final UiPreferences INSTANCE = new UiPreferences();

    private final ObjectProperty<AppTheme> theme =
            new SimpleObjectProperty<>(this, "theme", AppTheme.DEFAULT_THEME);
    private final ObjectProperty<AppFont> font =
            new SimpleObjectProperty<>(this, "font", AppFont.DEFAULT_FONT);
    private final ObjectProperty<FontSizePreset> fontSize =
            new SimpleObjectProperty<>(this, "fontSize", FontSizePreset.DEFAULT_SIZE);

    private Node shellRoot;

    private boolean colorSchemeListenerInstalled;

    private UiPreferences() {
        theme.addListener((obs, o, n) -> applyShellStyles());
        font.addListener((obs, o, n) -> applyShellStyles());
        fontSize.addListener((obs, o, n) -> applyShellStyles());
    }

    public static UiPreferences get() {
        return INSTANCE;
    }

    /**
     * Attach the main shell (scene root / BorderPane). Required for preferences to affect the UI.
     */
    public void attachShell(Node shellRoot) {
        this.shellRoot = shellRoot;
        ensureColorSchemeListener();
        applyShellStyles();
    }

    private void ensureColorSchemeListener() {
        if (colorSchemeListenerInstalled) {
            return;
        }
        colorSchemeListenerInstalled = true;
        try {
            Platform.getPreferences().colorSchemeProperty().addListener((obs, o, n) -> {
                AppTheme t = getTheme();
                if (t == AppTheme.SYSTEM || t == AppTheme.INVERT_SYSTEM) {
                    applyShellStyles();
                }
            });
        } catch (Exception ignored) {
            // Unsupported platform or JavaFX build without Platform preferences
        }
    }

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
        Set<String> toRemove = new HashSet<>();
        for (String c : shellRoot.getStyleClass()) {
            if (isManagedUiClass(c)) {
                toRemove.add(c);
            }
        }
        shellRoot.getStyleClass().removeAll(toRemove);

        String paletteClass = getTheme().resolvedPaletteStyleClass(currentColorScheme());
        shellRoot.getStyleClass().addAll(
                paletteClass,
                getFont().getStyleClass(),
                getFont().getMetricsStyleClass(),
                getFontSize().getStyleClass());
        shellRoot.getStyleClass().add(isDarkSurfacePalette(paletteClass) ? "sb-dark-ui" : "sb-light-ui");
    }

    /**
     * True when chrome should use dark-surface text fixes. Prefer the shell’s {@code sb-dark-ui} /
     * {@code sb-light-ui} when attached so this matches CSS after {@link #applyShellStyles()}.
     */
    public boolean isResolvedDarkSurface() {
        if (shellRoot != null) {
            if (shellRoot.getStyleClass().contains("sb-dark-ui")) {
                return true;
            }
            if (shellRoot.getStyleClass().contains("sb-light-ui")) {
                return false;
            }
        }
        return isDarkSurfacePalette(getTheme().resolvedPaletteStyleClass(currentColorScheme()));
    }

    /**
     * Palettes with dark canvas/surfaces — drives {@code .root.sb-dark-ui} for shared contrast rules
     * (prompt text, optional Modena overrides) without listing every {@code theme-*} block again.
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
