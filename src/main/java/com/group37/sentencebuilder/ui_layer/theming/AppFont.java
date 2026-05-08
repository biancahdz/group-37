/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    AppFont.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Enum defining available font stacks; each entry maps a display label to a CSS style class on the scene root.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Define all font stack options and their CSS style class names
 *      - Provide display labels and metrics classes used by the Settings UI and shell
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.theming;

/**
 * Font stacks applied on the app shell root. DEFAULT matches the UI stack; the combo shows getDisplayLabel()
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

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Constructs an AppFont entry with its display label and CSS style class.
     *
     * @param displayLabel human-readable name shown in the Settings combo box
     * @param styleClass CSS class applied on the shell root to activate this font stack
     */
    AppFont(String displayLabel, String styleClass) {
        this.displayLabel = displayLabel;
        this.styleClass = styleClass;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the human-readable font name shown in the Settings combo box.
     *
     * @return display label string
     */
    public String getDisplayLabel() {
        return displayLabel;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the CSS style class applied on the shell root to activate this font stack.
     *
     * @return CSS style class string
     */
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns an extra layout class for the scene root so letter-spacing and line spacing
     *      match dense faces such as Consolas and serif fonts. Normalized root font-size for each
     *      size preset is set per getStyleClass() in theme-palettes.css so switching fonts stays
     *      a similar visual size; this class only adds tracking.
     *
     * @return "font-metrics-relaxed" for dense faces, "font-metrics-default" otherwise
     */
    public String getMetricsStyleClass() {
        return switch (this) {
            case CONSOLAS, SERIF, TIMES_NEW_ROMAN, GEORGIA -> "font-metrics-relaxed";
            default -> "font-metrics-default";
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
