/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    AppTheme.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Enum defining all color themes; handles OS color scheme resolution for SYSTEM and INVERT_SYSTEM modes.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Define color theme options and their resolved CSS palette class names
 *      - Supply per-theme hex colors for ComboBox popup rows that cannot inherit scene CSS
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.theming;

import javafx.application.ColorScheme;

/**
 * UI color themes. The Settings combo shows getDisplayLabel() (e.g. "Light mode", "Dark").
 * SYSTEM maps to the OS light/dark appearance via JavaFX ColorScheme; INVERT_SYSTEM inverts that pairing.
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

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Constructs an AppTheme entry with its display label and CSS palette class.
     *
     * @param displayLabel human-readable name shown in the Settings combo box
     * @param styleClass CSS class applied on the shell root to activate this theme palette
     */
    AppTheme(String displayLabel, String styleClass) {
        this.displayLabel = displayLabel;
        this.styleClass = styleClass;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the human-readable theme name shown in the Settings combo box.
     *
     * @return display label string
     */
    public String getDisplayLabel() {
        return displayLabel;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the raw CSS palette class for this theme entry before OS-scheme resolution.
     *
     * @return CSS style class string
     */
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the CSS palette class actually applied on the scene root. For SYSTEM and
     *      INVERT_SYSTEM this follows (or inverts) the OS ColorScheme instead of using a
     *      fixed stylesheet block.
     *
     * @param systemScheme the current OS color scheme; treated as LIGHT if null
     * @return resolved CSS palette class string
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
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the concrete AppTheme used for ComboBox popup row colors, applying the same
     *      SYSTEM / INVERT_SYSTEM resolution rules as resolvedPaletteStyleClass.
     *
     * @param systemScheme the current OS color scheme; treated as LIGHT if null
     * @return resolved concrete AppTheme constant
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
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the background hex color for non-selected ComboBox popup rows. Popup rows
     *      live in a separate JavaFX scene and cannot inherit CSS variables from the main shell.
     *
     * @return hex color string (e.g. "#ffffff")
     */
    public String comboPopupRowBgHex() {
        return resolvedForChrome(safeColorScheme()).comboPopupRowBgHexConcrete();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the text hex color for non-selected ComboBox popup rows. Popup rows
     *      live in a separate JavaFX scene and cannot inherit CSS variables from the main shell.
     *
     * @return hex color string (e.g. "#18181b")
     */
    public String comboPopupRowTextHex() {
        return resolvedForChrome(safeColorScheme()).comboPopupRowTextHexConcrete();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the current OS color scheme, falling back to LIGHT if the platform
     *      does not expose a color scheme preference.
     *
     * @return current ColorScheme, never null
     */
    private static ColorScheme safeColorScheme() {
        try {
            return javafx.application.Platform.getPreferences().getColorScheme();
        } catch (Exception e) {
            return ColorScheme.LIGHT;
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the concrete background hex color for this theme's popup rows without
     *      any OS-scheme resolution. Only valid to call on fully concrete themes.
     *
     * @return hex color string
     */
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

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the concrete text hex color for this theme's popup rows without
     *      any OS-scheme resolution. Only valid to call on fully concrete themes.
     *
     * @return hex color string
     */
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

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the display label as the string representation of this enum constant.
     *
     * @return display label string
     */
    @Override
    public String toString() {
        return displayLabel;
    }
}
