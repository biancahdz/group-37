package com.group37.sentencebuilder.ui;

/**
 * Base font size on the app shell root; most typography uses {@code em} so it scales together.
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
