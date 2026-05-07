/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    UiPreferences.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Singleton managing active theme, font stack, and font size; any change reapplies CSS classes on the shell root.
 *
 *  Version: 1.0
 *  Created: 2026-03-27
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Expose theme, font, and font size as JavaFX properties and reapply shell styles on change
 *      - Track OS color scheme changes to resolve SYSTEM and INVERT_SYSTEM themes automatically
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.theming;

import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Single place for theme, font, and base font size. Updating these properties reapplies style classes
 * on the app shell so the whole UI picks up changes from one set of CSS custom properties on the root.
 */
public final class UiPreferences {

    private static final UiPreferences INSTANCE = new UiPreferences();

    private final ObjectProperty<AppTheme> theme =
            new SimpleObjectProperty<>(this, "theme", AppTheme.DEFAULT_THEME);
    private final ObjectProperty<AppFont> font =
            new SimpleObjectProperty<>(this, "font", AppFont.DEFAULT_FONT);
    private final ObjectProperty<FontSizePreset> fontSize =
            new SimpleObjectProperty<>(this, "fontSize", FontSizePreset.DEFAULT_SIZE);

    private Node shellRoot;

    private boolean colorSchemeListenerInstalled;

    private UiPreferences() {
        theme.addListener((obs, o, n) -> applyShellStyles());
        font.addListener((obs, o, n) -> applyShellStyles());
        fontSize.addListener((obs, o, n) -> applyShellStyles());
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the singleton UiPreferences instance shared across the application.
     *
     * @return the singleton instance
     */
    public static UiPreferences get() {
        return INSTANCE;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Attaches the main shell root node so preference changes can apply CSS style classes.
     *      Also installs the OS color-scheme listener and triggers an immediate style pass.
     *
     * @param shellRoot the scene root node of the main application shell
     */
    public void attachShell(Node shellRoot) {
        this.shellRoot = shellRoot;
        ensureColorSchemeListener();
        applyShellStyles();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Registers the OS color-scheme listener exactly once. Only re-applies shell styles
     *      when the active theme is SYSTEM or INVERT_SYSTEM, since other themes are not
     *      affected by the OS appearance setting.
     */
    private void ensureColorSchemeListener() {
        if (colorSchemeListenerInstalled) {
            return;
        }
        colorSchemeListenerInstalled = true;
        try {
            Platform.getPreferences().colorSchemeProperty().addListener((obs, o, n) -> {
                AppTheme t = getTheme();
                if (t == AppTheme.SYSTEM || t == AppTheme.INVERT_SYSTEM) {
                    applyShellStyles();
                }
            });
        } catch (Exception ignored) {
            // Unsupported platform or JavaFX build without Platform preferences
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the current OS color scheme from JavaFX platform preferences,
     *      falling back to LIGHT if the platform does not support it.
     *
     * @return current ColorScheme, never null
     */
    private static ColorScheme currentColorScheme() {
        try {
            return Platform.getPreferences().getColorScheme();
        } catch (Exception e) {
            return ColorScheme.LIGHT;
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the current active theme.
     *
     * @return the active AppTheme
     */
    public AppTheme getTheme() {
        return theme.get();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Sets the active theme, triggering a shell style refresh.
     *
     * @param value the AppTheme to apply
     */
    public void setTheme(AppTheme value) {
        theme.set(value);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the JavaFX property for the active theme, allowing external listeners.
     *
     * @return theme ObjectProperty
     */
    public ObjectProperty<AppTheme> themeProperty() {
        return theme;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the current active font stack.
     *
     * @return the active AppFont
     */
    public AppFont getFont() {
        return font.get();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Sets the active font stack, triggering a shell style refresh.
     *
     * @param value the AppFont to apply
     */
    public void setFont(AppFont value) {
        font.set(value);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the JavaFX property for the active font stack, allowing external listeners.
     *
     * @return font ObjectProperty
     */
    public ObjectProperty<AppFont> fontProperty() {
        return font;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the current active font size preset.
     *
     * @return the active FontSizePreset
     */
    public FontSizePreset getFontSize() {
        return fontSize.get();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Sets the active font size preset, triggering a shell style refresh.
     *
     * @param value the FontSizePreset to apply
     */
    public void setFontSize(FontSizePreset value) {
        fontSize.set(value);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the JavaFX property for the active font size preset, allowing external listeners.
     *
     * @return fontSize ObjectProperty
     */
    public ObjectProperty<FontSizePreset> fontSizeProperty() {
        return fontSize;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Removes all previously applied managed CSS classes from the shell root, then re-adds
     *      the resolved palette, font stack, font metrics, and font size classes. Also adds
     *      sb-dark-ui or sb-light-ui so CSS and controllers can detect surface brightness.
     */
    private void applyShellStyles() {
        if (shellRoot == null || getTheme() == null || getFont() == null || getFontSize() == null) {
            return;
        }
        Set<String> toRemove = new HashSet<>();
        for (String c : shellRoot.getStyleClass()) {
            if (isManagedUiClass(c)) {
                toRemove.add(c);
            }
        }
        shellRoot.getStyleClass().removeAll(toRemove);

        String paletteClass = getTheme().resolvedPaletteStyleClass(currentColorScheme());
        shellRoot.getStyleClass().addAll(
                paletteClass,
                getFont().getStyleClass(),
                getFont().getMetricsStyleClass(),
                getFontSize().getStyleClass());
        shellRoot.getStyleClass().add(isDarkSurfacePalette(paletteClass) ? "sb-dark-ui" : "sb-light-ui");
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the CSS palette class currently applied, resolving SYSTEM / INVERT_SYSTEM
     *      against the OS color scheme at call time.
     *
     * @return resolved CSS palette class string
     */
    public String resolvedPaletteClass() {
        return getTheme().resolvedPaletteStyleClass(currentColorScheme());
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns true when the active palette uses a dark canvas surface, meaning Labeled
     *      nodes may need forced inline text fills via DarkSurfaceText. Reads from the shell's
     *      sb-dark-ui / sb-light-ui style classes when available so the result stays in sync
     *      with the CSS after applyShellStyles() runs.
     *
     * @return true if the current surface is dark, false otherwise
     */
    public boolean isResolvedDarkSurface() {
        if (shellRoot != null) {
            if (shellRoot.getStyleClass().contains("sb-dark-ui")) {
                return true;
            }
            if (shellRoot.getStyleClass().contains("sb-light-ui")) {
                return false;
            }
        }
        return isDarkSurfacePalette(getTheme().resolvedPaletteStyleClass(currentColorScheme()));
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns true if the given palette class corresponds to a dark canvas/surface theme.
     *      Drives sb-dark-ui for shared contrast rules without repeating every theme block.
     *
     * @param paletteClass the resolved CSS palette class string
     * @return true if the palette is dark-surfaced
     */
    private static boolean isDarkSurfacePalette(String paletteClass) {
        if (paletteClass == null) {
            return false;
        }
        return switch (paletteClass) {
            case "theme-default",
                 "theme-christmas",
                 "theme-rainbow",
                 "theme-luna",
                 "theme-neon-noir",
                 "theme-deep-ink",
                 "theme-invert-system" -> true;
            default -> false;
        };
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns true if the given CSS class is managed by UiPreferences and should be
     *      removed before re-applying the current preference classes.
     *
     * @param c the CSS class string to check
     * @return true if this class is managed by UiPreferences
     */
    private static boolean isManagedUiClass(String c) {
        return c.startsWith("theme-")
                || c.startsWith("font-stack-")
                || c.startsWith("font-metrics-")
                || c.startsWith("fs-")
                || "sb-dark-ui".equals(c)
                || "sb-light-ui".equals(c);
    }
}
