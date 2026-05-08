/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    FontSizePreset.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Enum defining base font size presets for the shell root; most typography uses em so the whole UI scales together.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Define Small, Medium, Large, and Extra Large font size options with CSS class names
 *      - Provide display labels shown in the Settings font size combo box
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.theming;

/**
 * Base font size on the app shell root; most typography uses em so it scales together.
 */
public enum FontSizePreset {
    SMALL("Small", "fs-small", 11),
    MEDIUM("Medium", "fs-medium", 13),
    LARGE("Large", "fs-large", 15),
    EXTRA_LARGE("Extra large", "fs-xl", 17);

    public static final FontSizePreset DEFAULT_SIZE = MEDIUM;

    private final String displayLabel;
    private final String styleClass;
    private final int basePx;

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Constructs a FontSizePreset entry with its display label, CSS style class, and base pixel size.
     *
     * @param displayLabel human-readable name shown in the Settings combo box
     * @param styleClass CSS class applied on the shell root to activate this font size
     * @param basePx the base root font size in pixels
     */
    FontSizePreset(String displayLabel, String styleClass, int basePx) {
        this.displayLabel = displayLabel;
        this.styleClass = styleClass;
        this.basePx = basePx;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the human-readable font size name shown in the Settings combo box.
     *
     * @return display label string
     */
    public String getDisplayLabel() {
        return displayLabel;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the CSS style class applied on the shell root to activate this font size.
     *
     * @return CSS style class string
     */
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the base root font size in pixels for this preset.
     *
     * @return base font size in pixels
     */
    public int getBasePx() {
        return basePx;
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
