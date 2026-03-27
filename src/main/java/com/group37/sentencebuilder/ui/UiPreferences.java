package com.group37.sentencebuilder.ui;

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
        applyShellStyles();
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

        shellRoot.getStyleClass().addAll(
                getTheme().getStyleClass(),
                getFont().getStyleClass(),
                getFont().getMetricsStyleClass(),
                getFontSize().getStyleClass());
    }

    private static boolean isManagedUiClass(String c) {
        return c.startsWith("theme-")
                || c.startsWith("font-stack-")
                || c.startsWith("font-metrics-")
                || c.startsWith("fs-");
    }
}
