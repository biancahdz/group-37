/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    MainShellController.java
 *  Author:  Huy Nong, Sebastian Sarinana
 *
 *  Description:
 *      Original UI stub for the main shell. Hosts sidebar navigation and swaps
 *      center content views. Superseded by the ui_layer version which adds
 *      theming, dark surface text handling, and logout support.
 *
 *  Version: 1.0
 *  Created: 2026-03-22
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Wire sidebar toggle buttons to content view swapping
 *      - Cache loaded FXML views to avoid reloading on navigation
 *      - Keep one nav toggle always selected to maintain sidebar highlight
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Hosts sidebar navigation and swaps center content views (UI only).
 */
public class MainShellController {

    @FXML
    private StackPane contentHost;

    @FXML
    private Label headerTitle;

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
    private ToggleButton navSettings;

    // Each view is loaded once and reused on subsequent navigation — avoids re-running FXML initialize
    private final Map<ViewKey, Parent> viewCache = new EnumMap<>(ViewKey.class);

    @FXML
    private void initialize() {
        /* Let page roots size to their natural height so the shell ScrollPane can scroll vertically. */
        contentHost.setMaxHeight(Region.USE_PREF_SIZE);

        // Register a listener + ViewKey tag on each sidebar button
        wireNav(navHome, ViewKey.HOME);
        wireNav(navImport, ViewKey.IMPORT);
        wireNav(navGenerate, ViewKey.GENERATE);
        wireNav(navAutocomplete, ViewKey.AUTOCOMPLETE);
        wireNav(navReports, ViewKey.REPORTS);
        wireNav(navSettings, ViewKey.SETTINGS);

        // Prevent the user from deselecting all nav buttons — one must always stay active
        navGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) {
                oldT.setSelected(true);
            }
        });

        // Start on the Home page
        selectNav(ViewKey.HOME);
    }

    // Store the ViewKey in userData so selectNav can match it back without a separate lookup map
    private void wireNav(ToggleButton button, ViewKey key) {
        button.setUserData(key);
        button.selectedProperty().addListener((obs, was, isNow) -> {
            if (Boolean.TRUE.equals(isNow)) {
                showView(key);
            }
        });
    }

    /* Used by Home quick-action buttons to navigate to a different page programmatically. */
    public void showView(ViewKey key) {
        headerTitle.setText(titleFor(key));
        // Load from FXML on first visit; return the cached node on every subsequent visit
        Parent node = viewCache.computeIfAbsent(key, this::loadView);
        contentHost.getChildren().setAll(node);
        // Keep the page root at its preferred height so the outer ScrollPane can scroll it
        if (node instanceof Region pageRoot) {
            pageRoot.setMaxHeight(Region.USE_PREF_SIZE);
        }
        // Sync the sidebar highlight with the new active page
        selectNav(key);
    }

    // Walk the ToggleGroup to find the matching button and select it without triggering an extra showView call
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

    // Maps each ViewKey to the string shown in the top header bar
    private static String titleFor(ViewKey key) {
        return switch (key) {
            case HOME -> "Home";
            case IMPORT -> "Import";
            case GENERATE -> "Sentence Generator";
            case AUTOCOMPLETE -> "Auto-Complete";
            case REPORTS -> "Reports";
            case SETTINGS -> "Settings & About";
        };
    }

    private Parent loadView(ViewKey key) {
        // Resolve the FXML resource path for the requested page
        String resource = switch (key) {
            case HOME -> "/fxml/HomeView.fxml";
            case IMPORT -> "/fxml/ImportView.fxml";
            case GENERATE -> "/fxml/GenerateView.fxml";
            case AUTOCOMPLETE -> "/fxml/AutocompleteView.fxml";
            case REPORTS -> "/fxml/ReportsView.fxml";
            case SETTINGS -> "/fxml/SettingsView.fxml";
        };
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(resource)));
            Parent parent = loader.load();
            Object ctrl = loader.getController();
            // Give HomeController a reference to this shell so its quick-action buttons can navigate
            if (ctrl instanceof HomeController home) {
                home.setNavigator(this::showView);
            }
            return parent;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
