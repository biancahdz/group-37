/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    FontSizePreset.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Enum defining the base font size presets for the application shell root.
 *      Most typography scales in em units so the entire UI adjusts together
 *      when a preset is selected.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Define Small, Medium, Large, and Extra Large font size options
 *      - Provide CSS style class names applied on the shell root
 *      - Provide display labels shown in the Settings font size combo box
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui;

/* Base font size on the app shell root; most typography uses em so it scales together. */
public enum FontSizePreset {
    SMALL("Small", "fs-small", 11),
    MEDIUM("Medium", "fs-medium", 13),
    LARGE("Large", "fs-large", 15),
    EXTRA_LARGE("Extra large", "fs-xl", 17);

    public static final FontSizePreset DEFAULT_SIZE = MEDIUM;

    private final String displayLabel;
    private final String styleClass;
    private final int basePx;

    FontSizePreset(String displayLabel, String styleClass, int basePx) {
        this.displayLabel = displayLabel;
        this.styleClass = styleClass;
        this.basePx = basePx;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public int getBasePx() {
        return basePx;
    }

    @Override
    public String toString() {
        return displayLabel;
    }
}
