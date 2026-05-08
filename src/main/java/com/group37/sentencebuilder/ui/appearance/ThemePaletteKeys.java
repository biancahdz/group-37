/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    ThemePaletteKeys.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Constants for the CSS custom property names defined on the scene root
 *      in theme-palettes.css. Use these when referencing palette tokens in
 *      documentation or code instead of duplicating raw string literals.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Define string constants for all CSS palette custom property names
 *      - Group tokens by category: surface, text, chrome, table, eyebrow, border, accent
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui.appearance;

/*
 * Names of CSS custom properties set on .root (scene root) in theme-palettes.css.
 * Use these when reading or documenting theme data instead of duplicating string literals.
 */
public final class ThemePaletteKeys {

    public static final String CANVAS = "sb-canvas";
    public static final String SIDEBAR = "sb-sidebar";
    public static final String HEADER = "sb-header";
    public static final String SURFACE = "sb-surface";
    public static final String SURFACE_RAISED = "sb-surface-raised";
    public static final String SURFACE_MUTED = "sb-surface-muted";
    public static final String SURFACE_HOVER = "sb-surface-hover";
    public static final String INPUT = "sb-input";
    public static final String WORKSPACE_PANEL = "sb-workspace-panel";

    public static final String TEXT = "sb-text";
    public static final String TEXT_SECONDARY = "sb-text-secondary";
    public static final String TEXT_MUTED = "sb-text-muted";
    public static final String TEXT_TERTIARY = "sb-text-tertiary";
    /* Paragraph / lead copy under page titles — stronger separation from TEXT than TEXT_MUTED. */
    public static final String TEXT_LEAD = "sb-text-lead";

    /* Sidebar tagline, header subtitle, section label, footer — tuned per theme for contrast. */
    public static final String CHROME_TAGLINE = "sb-chrome-tagline";
    public static final String CHROME_HEADER_SUB = "sb-chrome-header-sub";
    public static final String CHROME_SECTION = "sb-chrome-section";
    public static final String CHROME_FOOTER = "sb-chrome-footer";

    public static final String TABLE_HEADER_BG = "sb-table-header-bg";
    public static final String TABLE_HEADER_TEXT = "sb-table-header-text";

    public static final String EYEBROW_NEUTRAL = "sb-eyebrow-neutral";
    public static final String EYEBROW_PRIMARY = "sb-eyebrow-primary";
    public static final String EYEBROW_TEAL = "sb-eyebrow-teal";
    public static final String EYEBROW_VIOLET = "sb-eyebrow-violet";
    public static final String EYEBROW_AMBER = "sb-eyebrow-amber";

    public static final String BORDER = "sb-border";
    public static final String BORDER_SUBTLE = "sb-border-subtle";
    public static final String DIVIDER = "sb-divider";
    /* Vertical line between sidebar and main content. */
    public static final String SEPARATOR_SIDEBAR = "sb-separator-sidebar";
    /* Horizontal line under the brand block (Sentence Builder / tagline). */
    public static final String SEPARATOR_BRAND = "sb-separator-brand";
    /* Horizontal line under the top header bar. */
    public static final String SEPARATOR_HEADER = "sb-separator-header";

    public static final String ACCENT = "sb-accent";
    public static final String ACCENT_HOVER = "sb-accent-hover";
    public static final String ACCENT_SOFT = "sb-accent-soft";
    public static final String ACCENT_BORDER = "sb-accent-border";

    public static final String VIOLET = "sb-violet";
    public static final String TEAL = "sb-teal";
    public static final String AMBER = "sb-amber";
    public static final String SHADE = "sb-shade";

    private ThemePaletteKeys() {}
}
