package com.group37.sentencebuilder.ui_layer.theming;

import javafx.application.ColorScheme;

/**
 * UI color themes. The combo shows {@link #getDisplayLabel()} (e.g. "Light mode", "Dark").
 *
 * <p>Contrast for shell chrome, table headers, and page eyebrows is driven by {@code sb-chrome-*},
 * {@code sb-table-header-*}, and {@code sb-eyebrow-*} tokens defined per theme in {@code theme-palettes.css}.</p>
 *
 * <p>{@link #SYSTEM} maps to the OS light/dark appearance via JavaFX {@link ColorScheme} (same signal as the
 * OS "appearance" setting — not a separate clock). {@link #INVERT_SYSTEM} inverts that pairing.</p>
 */
public enum AppTheme {
    DEFAULT("Dark", "theme-default"),
    LIGHT("Light mode", "theme-light"),
    CHRISTMAS("Christmas mode", "theme-christmas"),
    THANKSGIVING("Thanksgiving mode", "theme-thanksgiving"),
    RAINBOW("Rainbow mode", "theme-rainbow"),
    LUNA("Luna mode", "theme-luna"),
    NEON_NOIR("Neon Noir mode", "theme-neon-noir"),
    FAIRY_LIGHTS("Fairy Lights mode", "theme-fairy-lights"),
    DEEP_INK("Deep Ink mode", "theme-deep-ink"),
    EMERALD_LIGHT("Emerald Light mode", "theme-emerald-light"),
    TRAPPED_RAINBOW("Trapped Rainbow mode", "theme-trapped-rainbow"),
    SYSTEM("System mode", "theme-system"),
    INVERT_SYSTEM("Invert System mode", "theme-invert-system");

    /** Change this one value to switch the default theme used at startup. */
    public static final AppTheme DEFAULT_THEME = SYSTEM;

    private final String displayLabel;
    private final String styleClass;

    AppTheme(String displayLabel, String styleClass) {
        this.displayLabel = displayLabel;
        this.styleClass = styleClass;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getStyleClass() {
        return styleClass;
    }

    /**
     * CSS palette class actually applied on the scene root. For {@link #SYSTEM} and {@link #INVERT_SYSTEM},
     * this follows (or inverts) the OS {@link ColorScheme} instead of using a fixed stylesheet block.
     */
    public String resolvedPaletteStyleClass(ColorScheme systemScheme) {
        ColorScheme s = systemScheme != null ? systemScheme : ColorScheme.LIGHT;
        boolean dark = s == ColorScheme.DARK;
        return switch (this) {
            case SYSTEM -> dark ? DEFAULT.styleClass : LIGHT.styleClass;
            case INVERT_SYSTEM -> dark ? LIGHT.styleClass : INVERT_SYSTEM.styleClass;
            default -> styleClass;
        };
    }

    /**
     * Concrete theme used for ComboBox popup row colors (same rules as {@link #resolvedPaletteStyleClass}).
     */
    public AppTheme resolvedForChrome(ColorScheme systemScheme) {
        ColorScheme s = systemScheme != null ? systemScheme : ColorScheme.LIGHT;
        boolean dark = s == ColorScheme.DARK;
        return switch (this) {
            case SYSTEM -> dark ? DEFAULT : LIGHT;
            case INVERT_SYSTEM -> dark ? LIGHT : INVERT_SYSTEM;
            default -> this;
        };
    }

    /**
     * ComboBox popup list rows are rendered in a separate scene that does not inherit {@code sb-*} CSS
     * lookups from the main shell — use these hex colors for normal (non-selected) rows.
     */
    public String comboPopupRowBgHex() {
        return resolvedForChrome(safeColorScheme()).comboPopupRowBgHexConcrete();
    }

    public String comboPopupRowTextHex() {
        return resolvedForChrome(safeColorScheme()).comboPopupRowTextHexConcrete();
    }

    private static ColorScheme safeColorScheme() {
        try {
            return javafx.application.Platform.getPreferences().getColorScheme();
        } catch (Exception e) {
            return ColorScheme.LIGHT;
        }
    }

    private String comboPopupRowBgHexConcrete() {
        return switch (this) {
            case LIGHT -> "#ffffff";
            case THANKSGIVING -> "#faf7f2";
            case CHRISTMAS -> "#1a2e28";
            case RAINBOW -> "#2e2a5c";
            case LUNA -> "#11243f";
            case NEON_NOIR -> "#0d1a2f";
            case FAIRY_LIGHTS -> "#f7f1ff";
            case DEEP_INK -> "#0f1217";
            case EMERALD_LIGHT -> "#f2fbf8";
            case TRAPPED_RAINBOW -> "#fff8ff";
            case DEFAULT -> "#2d2d33";
            case INVERT_SYSTEM -> "#000000";
            case SYSTEM -> "#ffffff";
        };
    }

    private String comboPopupRowTextHexConcrete() {
        return switch (this) {
            case LIGHT -> "#18181b";
            case THANKSGIVING -> "#3e2723";
            case CHRISTMAS -> "#faecd8";
            case RAINBOW -> "#fafafa";
            case LUNA -> "#dbeafe";
            case NEON_NOIR -> "#e2e8f0";
            case FAIRY_LIGHTS -> "#3b2f56";
            case DEEP_INK -> "#d4e7ef";
            case EMERALD_LIGHT -> "#123b2f";
            case TRAPPED_RAINBOW -> "#6e5a8a";
            case DEFAULT -> "#f4f4f5";
            case INVERT_SYSTEM -> "#ffffff";
            case SYSTEM -> "#18181b";
        };
    }

    @Override
    public String toString() {
        return displayLabel;
    }
}
