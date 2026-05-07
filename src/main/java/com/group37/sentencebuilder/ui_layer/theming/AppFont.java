/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    AppFont.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Enum defining the font stack options available in the application shell.
 *      Each entry maps a display label to a CSS style class applied on the scene root.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Define all available font stacks for the application
 *      - Provide CSS style class names used to apply fonts on the shell root
 *      - Provide display labels shown in the Settings font combo box
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.theming;

/**
 * Font stacks applied on the app root. {@link #DEFAULT} matches the UI stack; the combo shows {@link #getDisplayLabel()}
 * (e.g. "System UI"), not a placeholder name.
 */
public enum AppFont {
    DEFAULT("System UI", "font-stack-default"),
    TIMES_NEW_ROMAN("Times New Roman", "font-stack-times"),
    GEORGIA("Georgia", "font-stack-georgia"),
    ARIAL("Arial", "font-stack-arial"),
    VERDANA("Verdana", "font-stack-verdana"),
    CONSOLAS("Consolas", "font-stack-consolas"),
    SERIF("Serif", "font-stack-serif"),
    SANS_SERIF("Sans-Serif", "font-stack-sans");

    /** Change this one value to switch the default font used at startup. */
    public static final AppFont DEFAULT_FONT = DEFAULT;

    private final String displayLabel;
    private final String styleClass;

    AppFont(String displayLabel, String styleClass) {
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
     * Extra layout class on the scene root so letter-spacing and line spacing match dense faces (Consolas, many serifs).
     * Normalized root font-size for each size preset is set per {@link #getStyleClass()} in {@code theme-palettes.css}
     * ({@code .root.font-stack-*.fs-*}) so switching fonts stays a similar visual size; this class only adds tracking.
     * Paired CSS: {@code font-metrics-default} vs {@code font-metrics-relaxed}.
     */
    public String getMetricsStyleClass() {
        return switch (this) {
            case CONSOLAS, SERIF, TIMES_NEW_ROMAN, GEORGIA -> "font-metrics-relaxed";
            default -> "font-metrics-default";
        };
    }

    @Override
    public String toString() {
        return displayLabel;
    }
}
