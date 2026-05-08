/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    MainShellController.java
 *  Author:  Sebastian Sarinana, Huy Nong
 *
 *  Description:
 *      Main application shell controller: manages sidebar navigation and loads/swaps the active page view.
 *
 *  Version: 1.0
 *  Created: 2026-03-22
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Handle navigation events and update the selected workspace state
 *      - Load FXML pages and route lifecycle callbacks (enter/leave) to controllers
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;

import com.group37.sentencebuilder.ui_layer.theming.DarkSurfaceText;
import com.group37.sentencebuilder.ui_layer.theming.UiPreferences;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Hosts sidebar navigation and swaps center content views (UI only).
 */
public class MainShellController {

    /** Scene root — initialize() sets a stable CSS id for high-specificity dark-theme text rules. */
    @FXML
    private BorderPane shellRoot;

    private Object currentController;

    private Runnable onLogout;

    @FXML
    private StackPane contentHost;

    @FXML
    private StackPane workspaceInset;

    @FXML
    private ScrollPane workspaceScroll;

    @FXML
    private Label headerTitle;

    @FXML
    private Label sidebarMarkBadge;

    @FXML
    private Label sidebarBrandLabel;

    @FXML
    private Label sidebarTagline;

    @FXML
    private Label workspaceSectionLabel;

    @FXML
    private ToggleGroup navGroup;

    @FXML
    private ToggleButton navHome;

    @FXML
    private ToggleButton navImport;

    @FXML
    private ToggleButton navGenerate;

    @FXML
    private ToggleButton navAutocomplete;

    @FXML
    private ToggleButton navReports;

    @FXML
    private ToggleButton navCorpusStats;

    @FXML
    private ToggleButton navSettings;

    @FXML
    private ToggleButton navHelp;

    private final Map<ViewKey, Parent> viewCache = new EnumMap<>(ViewKey.class);

    /** Suppresses nav toggle listeners while we sync the sidebar programmatically. */
    private boolean suppressNavToggleCallbacks;

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Sets the shell root CSS id, configures workspace scrolling, wires all nav
     *      toggle buttons, prevents deselection, registers theme/OS-scheme listeners,
     *      and shows the home view on startup.
     */
    @FXML
    private void initialize() {
        shellRoot.setId("sb-shell-root");
        contentHost.setMaxHeight(Region.USE_PREF_SIZE);
        wireWorkspaceScrolling();

        wireNav(navHome, ViewKey.HOME);
        wireNav(navImport, ViewKey.IMPORT);
        wireNav(navGenerate, ViewKey.GENERATE);
        wireNav(navAutocomplete, ViewKey.AUTOCOMPLETE);
        wireNav(navReports, ViewKey.REPORTS);
        wireNav(navCorpusStats, ViewKey.CORPUS_STATS);
        wireNav(navSettings, ViewKey.SETTINGS);
        wireNav(navHelp, ViewKey.HELP);

        preventNavDeselection();

        UiPreferences prefs = UiPreferences.get();
        prefs.themeProperty().addListener((o, a, b) -> {
            applyShellChromeInlineText();
            applyWorkspaceEyebrowInlineText();
        });
        try {
            Platform.getPreferences().colorSchemeProperty().addListener((o, a, b) -> {
                applyShellChromeInlineText();
                applyWorkspaceEyebrowInlineText();
            });
        } catch (Exception ignored) {
            // no Platform preferences
        }
        applyShellChromeInlineText();

        showView(ViewKey.HOME);

        shellRoot.sceneProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) {
                Platform.runLater(() -> {
                    applyShellChromeInlineText();
                    applyWorkspaceEyebrowInlineText();
                });
            }
        });
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Calls applyCss() on the shell root to reapply sidebar chrome tokens after a theme
     *      or scene-attach event. Sidebar nodes are styled entirely from CSS on #sb-shell-root;
     *      clearing Text fills on nav toggles from Java breaks painting until hover.
     */
    private void applyShellChromeInlineText() {
        if (shellRoot != null) {
            shellRoot.applyCss();
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Walks all section-eyebrow nodes in the content host and forces accent fills
     *      in dark mode or clears them in light mode, handling the LabeledSkin CSS
     *      cascade issue that can render eyebrow text too dark on dark palettes.
     */
    private void applyWorkspaceEyebrowInlineText() {
        if (contentHost == null) {
            return;
        }
        boolean dark = UiPreferences.get().isResolvedDarkSurface();
        for (Node n : contentHost.lookupAll(".section-eyebrow")) {
            if (!(n instanceof Labeled lab)) {
                continue;
            }
            if (!dark) {
                DarkSurfaceText.clearForcedLabeledPaint(lab);
                lab.applyCss();
                continue;
            }
            DarkSurfaceText.forceLabeledFill(lab, eyebrowColorForAccentClass(lab));
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the inline fill color for a section eyebrow label based on its accent
     *      class, maximizing contrast on dark (#000/#18181b) canvas surfaces.
     *
     * @param lab the eyebrow Labeled node whose style classes determine the fill
     * @return fill Color for the given accent class
     */
    private static Color eyebrowColorForAccentClass(Labeled lab) {
        if (lab.getStyleClass().contains("section-eyebrow-primary")) {
            return Color.web("#ffffff");
        }
        if (lab.getStyleClass().contains("section-eyebrow-teal")) {
            return Color.WHITE;
        }
        if (lab.getStyleClass().contains("section-eyebrow-violet")) {
            return Color.web("#e9d5ff");
        }
        if (lab.getStyleClass().contains("section-eyebrow-amber")) {
            return Color.web("#fde68a");
        }
        return Color.color(1, 1, 1, 0.95);
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Attaches a scroll event filter to the workspace inset so mouse-wheel and trackpad
     *      scrolling works across the full workspace area, not just over the inner content node.
     */
    private void wireWorkspaceScrolling() {
        workspaceInset.addEventFilter(ScrollEvent.SCROLL, event -> {
            double contentHeight = contentHost.getBoundsInLocal().getHeight();
            double viewportHeight = workspaceScroll.getViewportBounds().getHeight();
            double scrollableHeight = contentHeight - viewportHeight;
            if (scrollableHeight <= 0) {
                return;
            }

            double delta = -event.getDeltaY() / scrollableHeight;
            double next = Math.max(0.0, Math.min(1.0, workspaceScroll.getVvalue() + delta));
            workspaceScroll.setVvalue(next);
            event.consume();
        });
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Listens on the nav toggle group and re-selects the previous toggle when the
     *      user clicks the active button, ensuring the sidebar always has one selection.
     */
    private void preventNavDeselection() {
        navGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null || oldToggle == null) {
                return;
            }
            suppressNavToggleCallbacks = true;
            try {
                oldToggle.setSelected(true);
            } finally {
                suppressNavToggleCallbacks = false;
            }
        });
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Registers the logout callback wired by SentenceBuilderApp to return to the login scene.
     *
     * @param onLogout runnable to invoke when the user requests logout
     */
    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Fires the registered logout callback if one is present, triggering the transition
     *      back to the login scene.
     */
    public void requestLogout() {
        if (onLogout != null) {
            onLogout.run();
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Attaches the ViewKey as user data on the button and registers a selection listener
     *      that calls showView when the toggle is activated.
     *
     * @param button the sidebar ToggleButton to wire
     * @param key the ViewKey identifying which page this button navigates to
     */
    private void wireNav(ToggleButton button, ViewKey key) {
        button.setUserData(key);
        button.selectedProperty().addListener((obs, was, isNow) -> {
            if (suppressNavToggleCallbacks) {
                return;
            }
            if (Boolean.TRUE.equals(isNow)) {
                showView(key);
            }
        });
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Loads or retrieves the cached view for the given key, swaps it into the content
     *      host, calls page lifecycle callbacks, updates the header title, and syncs the
     *      sidebar selection. Used by Home quick actions and nav toggle listeners.
     *
     * @param key the ViewKey identifying the page to display
     */
    public void showView(ViewKey key) {
        if (currentController instanceof ApplicationPage oldPage)
        {
            oldPage.onPageLeave();
        }

        headerTitle.setText(titleFor(key));
        Parent node = viewCache.computeIfAbsent(key, this::loadView);
        contentHost.getChildren().setAll(node);
        StackPane.setAlignment(node, Pos.TOP_LEFT);
        if (node instanceof Region pageRoot) {
            pageRoot.setMaxHeight(Region.USE_PREF_SIZE);
        }

        Object ctrl = node.getUserData();
        if (ctrl instanceof ApplicationPage page)
        {
            page.onPageEnter();
        }

        applyWorkspaceEyebrowInlineText();
        Platform.runLater(this::applyWorkspaceEyebrowInlineText);
        /* Nav selection listener already runs applyShellChromeInlineText; do not clear brand labels again here. */

        currentController = ctrl;

        suppressNavToggleCallbacks = true;
        try {
            selectNav(key);
        } finally {
            suppressNavToggleCallbacks = false;
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Finds the toggle in the nav group whose user data matches the given key and
     *      selects it if not already selected.
     *
     * @param key the ViewKey whose corresponding nav toggle should be selected
     */
    private void selectNav(ViewKey key) {
        for (Toggle t : navGroup.getToggles()) {
            if (t.getUserData() == key) {
                if (!t.isSelected()) {
                    t.setSelected(true);
                }
                return;
            }
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Maps a ViewKey to the display string shown in the workspace header title bar.
     *
     * @param key the ViewKey to look up
     * @return header title string for the given key
     */
    private static String titleFor(ViewKey key) {
        return switch (key) {
            case HOME -> "Home";
            case IMPORT -> "Import";
            case GENERATE -> "Sentence Generator";
            case AUTOCOMPLETE -> "Auto-Complete";
            case REPORTS -> "Reports";
            case CORPUS_STATS -> "Word analytics";
            case SETTINGS -> "Settings & About";
            case HELP -> "Help";
        };
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Loads the FXML for the given key, injects dependencies into the controller
     *      (navigator for HomeController, database for DatabasePage, logout for SettingsController),
     *      and stores the controller as user data on the parent node for lifecycle dispatch.
     *
     * @param key the ViewKey identifying which FXML resource to load
     * @return the loaded Parent node with controller stored as user data
     */
    private Parent loadView(ViewKey key) {
        String resource = switch (key) {
            case HOME -> "/fxml/HomeView.fxml";
            case IMPORT -> "/fxml/ImportView.fxml";
            case GENERATE -> "/fxml/GenerateView.fxml";
            case AUTOCOMPLETE -> "/fxml/AutocompleteView.fxml";
            case REPORTS -> "/fxml/ReportsView.fxml";
            case CORPUS_STATS -> "/fxml/CorpusStatsView.fxml";
            case SETTINGS -> "/fxml/SettingsView.fxml";
            case HELP -> "/fxml/HelpView.fxml";
        };
        try {

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(resource)));
            Parent parent = loader.load();

            Object ctrl = loader.getController();

            parent.setUserData(ctrl);

            if (ctrl instanceof HomeController home)
            {
                home.setNavigator(this::showView);
            }

            if (ctrl instanceof DatabasePage dbController)
            {
                dbController.setDatabase(Database.getDatabase());
            }

            if (ctrl instanceof SettingsController settings) {
                settings.setOnLogout(this::requestLogout);
            }

            currentController = ctrl;

            return parent;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
