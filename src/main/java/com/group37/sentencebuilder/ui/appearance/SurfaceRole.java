/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    SurfaceRole.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Enum documenting the semantic surface roles used in the application UI.
 *      Each entry maps a role name to the CSS style classes that implement it,
 *      providing a single reference for FXML and CSS authors.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-03-27
 *
 *  Responsibilities:
 *      - Document all surface roles and their corresponding CSS class names
 *      - Provide intent descriptions for panels, cards, tiles, and buttons
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui.appearance;

/*
 * Semantic surfaces: rounded panels and buttons share theme tokens (ThemePaletteKeys) but use
 * different geometry and elevation. Use these class names in FXML/CSS so theme changes propagate consistently.
 */
public enum SurfaceRole {

    /** Main workspace panel behind page content (rounded rectangle vs canvas). */
    WORKSPACE_PANEL("content-host workspace-surface", "Inset panel · border + radius · uses workspace panel token"),

    /** Form / about cards with border and shadow. */
    CONTENT_CARD("content-card", "Standard elevated card"),

    CONTENT_CARD_ELEVATED("content-card content-card-elevated", "Stronger shadow"),

    /** Dashboard stat tiles. */
    STAT_TILE_PRIMARY("stat-tile stat-tile-primary", "Metric tile · primary accent edge"),

    STAT_TILE_VIOLET("stat-tile stat-tile-violet", "Metric tile · violet accent edge"),

    /** Home quick-link tiles (Import, Generate, …). */
    QUICK_CARD_IMPORT("quick-card quick-card-accent-import", "Tile · import accent"),

    QUICK_CARD_GENERATE("quick-card quick-card-accent-generate", "Tile · generate accent"),

    QUICK_CARD_COMPOSE("quick-card quick-card-accent-compose", "Tile · auto-complete accent"),

    QUICK_CARD_REPORTS("quick-card quick-card-accent-reports", "Tile · reports accent"),

    /** Primary filled button (standalone Button nodes). */
    BUTTON_PRIMARY("primary-button", "Filled primary"),

    BUTTON_SECONDARY("secondary-button", "Outlined secondary"),

    /** Header “Preview build” style chip. */
    HEADER_PILL("header-pill", "Pill · amber family by default"),

    /** Sidebar SB badge. */
    SIDEBAR_BADGE("sidebar-mark-badge", "Small rounded badge");

    private final String styleClasses;
    private final String intent;

    SurfaceRole(String styleClasses, String intent) {
        this.styleClasses = styleClasses;
        this.intent = intent;
    }

    public String styleClasses() {
        return styleClasses;
    }

    public String intent() {
        return intent;
    }
}
