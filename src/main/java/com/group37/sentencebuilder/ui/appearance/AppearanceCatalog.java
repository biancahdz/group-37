/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    AppearanceCatalog.java
 *  Author:  Huy Nong
 *
 *  Description:
 *      Documentation anchor for the appearance package. Not used at runtime.
 *      Points developers to the relevant classes that make up the theming system:
 *      AppTheme, AppFont, FontSizePreset, UiPreferences, and the CSS files.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-04-19
 *
 *  Responsibilities:
 *      - Serve as a discoverable entry point for the appearance package in the IDE
 *      - Link to ThemePaletteKeys, TypographyRole, and SurfaceRole via Javadoc
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui.appearance;

/*
 * Documentation anchor only — not used at runtime.
 * The app does not load themes from a central “catalog object.” Appearance is driven by:
 * AppTheme, AppFont, FontSizePreset, UiPreferences, and CSS under src/main/resources/css/.
 * This empty class exists so the package has a single discoverable entry in the IDE.
 * - Palette variable names: ThemePaletteKeys
 * - Typography / surface roles: TypographyRole, SurfaceRole
 */
public final class AppearanceCatalog {

    private AppearanceCatalog() {}
}
