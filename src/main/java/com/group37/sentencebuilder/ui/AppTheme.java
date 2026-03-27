package com.group37.sentencebuilder.ui;

/**
 * UI color themes. {@link #DEFAULT} is the built-in default (dark); the combo shows {@link #getDisplayLabel()}
 * (e.g. "Dark"), not a placeholder name.
 */
public enum AppTheme {
    DEFAULT("Dark", "theme-default"),
    LIGHT("Light mode", "theme-light"),
    CHRISTMAS("Christmas mode", "theme-christmas"),
    THANKSGIVING("Thanksgiving mode", "theme-thanksgiving"),
    RAINBOW("Rainbow mode", "theme-rainbow"),
    SYSTEM("System mode", "theme-system"),
    INVERT_SYSTEM("Invert System mode", "theme-invert-system");

    /** Change this one value to switch the default theme used at startup. */
    public static final AppTheme DEFAULT_THEME = DEFAULT;

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
     * ComboBox popup list rows are rendered in a separate scene that does not inherit {@code sb-*} CSS
     * lookups from the main shell — use these hex colors for normal (non-selected) rows.
     */
    public String comboPopupRowBgHex() {
        return switch (this) {
            case LIGHT, SYSTEM -> "#ffffff";
            case THANKSGIVING -> "#faf7f2";
            case CHRISTMAS -> "#1a2e28";
            case RAINBOW -> "#2e2a5c";
            case DEFAULT, INVERT_SYSTEM -> "#2d2d33";
        };
    }

    public String comboPopupRowTextHex() {
        return switch (this) {
            case LIGHT, SYSTEM -> "#18181b";
            case THANKSGIVING -> "#3e2723";
            case CHRISTMAS -> "#faecd8";
            case RAINBOW -> "#fafafa";
            case DEFAULT, INVERT_SYSTEM -> "#f4f4f5";
        };
    }

    @Override
    public String toString() {
        return displayLabel;
    }
}
