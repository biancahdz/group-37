package com.group37.sentencebuilder.ui.appearance;

/**
 * <strong>Documentation anchor only — not used at runtime.</strong>
 * <p>
 * The app does not load themes from a central “catalog object.” Appearance is driven by:
 * {@link com.group37.sentencebuilder.ui.AppTheme}, {@link com.group37.sentencebuilder.ui.AppFont},
 * {@link com.group37.sentencebuilder.ui.FontSizePreset}, {@link com.group37.sentencebuilder.ui.UiPreferences},
 * and CSS under {@code src/main/resources/css/}. This empty class exists so the package has a single
 * discoverable entry in the IDE and this Javadoc can point engineers to those types — similar to a
 * README index, not a data structure.
 * </p>
 * <ul>
 *   <li><strong>Palette variable names</strong> — {@link ThemePaletteKeys}</li>
 *   <li><strong>Typography / surface roles</strong> — {@link TypographyRole}, {@link SurfaceRole}</li>
 * </ul>
 */
public final class AppearanceCatalog {

    private AppearanceCatalog() {}
}
