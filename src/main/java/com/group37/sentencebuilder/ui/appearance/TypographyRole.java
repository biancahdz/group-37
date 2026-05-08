/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    TypographyRole.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Enum documenting the text hierarchy roles used across the application UI.
 *      Each entry maps a semantic role to the CSS style classes that implement it
 *      and includes an intent description for developers.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-03-27
 *
 *  Responsibilities:
 *      - Document all typography roles and their CSS style class names
 *      - Provide intent descriptions covering size, weight, and color role
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui.appearance;

/*
 * Maps the original UI’s text hierarchy to style classes in app-theme.css.
 * Each role has a distinct size/weight/color intent; ratios are expressed in em on the shell root.
 */
public enum TypographyRole {

    /** Small all-caps section label (e.g. OVERVIEW, WORKSPACE). */
    EYEBROW("section-eyebrow", "Caps label · tertiary color · ~0.77em"),

    /** Optional accent tint on eyebrow (OVERVIEW on Home). */
    EYEBROW_ACCENT_PRIMARY("section-eyebrow section-eyebrow-primary", "Eyebrow using primary accent color"),

    /** Page title (e.g. Dashboard). */
    PAGE_TITLE("section-heading section-heading-hero", "Hero title · boldest · ~2.6em"),

    /** Standard section heading (smaller than hero). */
    SECTION_HEADING("section-heading", "Section title · ~2em · primary text color"),

    /* Intro paragraph under the title — uses ThemePaletteKeys.TEXT_LEAD for contrast vs title. */
    SECTION_LEAD("section-lead", "Lead body · relaxed line rhythm · lead color token"),

    /** Card / panel title inside a content block. */
    CARD_TITLE("card-title", "In-card heading · ~1.15em bold"),

    /** Supporting line under card title. */
    CARD_SUBTITLE("card-subtitle", "Secondary emphasis"),

    /** Muted explanatory text. */
    CARD_SUBTITLE_MUTED("card-subtitle card-subtitle-muted", "Tertiary body"),

    /** App shell header title (top bar). */
    HEADER_TITLE("header-title", "Shell page title"),

    /** App shell header subtitle. */
    HEADER_SUB("header-sub", "Muted meta under shell title"),

    /** Sidebar app name. */
    SIDEBAR_BRAND("sidebar-brand", "Brand wordmark"),

    /** Sidebar tagline under brand. */
    SIDEBAR_TAGLINE("sidebar-tagline", "Muted one-liner"),

    /** Large metric number on dashboard. */
    STAT_VALUE("stat-tile-value", "Display numeral · ~2em"),

    /** Small caps label under stats. */
    STAT_LABEL("stat-tile-label", "Caption under metrics"),

    /** Feature card title. */
    QUICK_CARD_TITLE("quick-card-title", "Tile title · ~1.3em bold"),

    /** Feature card body. */
    QUICK_CARD_DESC("quick-card-desc", "Tile description · body weight"),

    /** Pill / CTA control on cards (styled as rounded button in CSS). */
    QUICK_CARD_CTA("quick-card-cta", "Rounded CTA chip");

    private final String styleClasses;
    private final String intent;

    TypographyRole(String styleClasses, String intent) {
        this.styleClasses = styleClasses;
        this.intent = intent;
    }

    /* Space-separated JavaFX styleClass names to apply to a Labeled node. */
    public String styleClasses() {
        return styleClasses;
    }

    /* Human-readable description of hierarchy and contrast role. */
    public String intent() {
        return intent;
    }
}
