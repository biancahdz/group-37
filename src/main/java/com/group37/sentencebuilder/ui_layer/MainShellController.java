/**
 * File: MainShellController.java
 * Description: 
 *
 * Author: 
 * Created: 
 * Last Modified: 2026-03-27
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private Object currentController;

    private Runnable onLogout;

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
    private ToggleButton navCorpusStats;

    @FXML
    private ToggleButton navSettings;

    @FXML
    private ToggleButton navHelp;

    private final Map<ViewKey, Parent> viewCache = new EnumMap<>(ViewKey.class);

    /** Suppresses nav toggle listeners while we sync the sidebar programmatically. */
    private boolean suppressNavToggleCallbacks;

    @FXML
    private void initialize() {
        contentHost.setMaxHeight(Region.USE_PREF_SIZE);

        wireNav(navHome, ViewKey.HOME);
        wireNav(navImport, ViewKey.IMPORT);
        wireNav(navGenerate, ViewKey.GENERATE);
        wireNav(navAutocomplete, ViewKey.AUTOCOMPLETE);
        wireNav(navReports, ViewKey.REPORTS);
        wireNav(navCorpusStats, ViewKey.CORPUS_STATS);
        wireNav(navSettings, ViewKey.SETTINGS);
        wireNav(navHelp, ViewKey.HELP);

        showView(ViewKey.HOME);
    }

    /** Wired from {@link com.group37.sentencebuilder.SentenceBuilderApp} to return to the login scene. */
    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    public void requestLogout() {
        if (onLogout != null) {
            onLogout.run();
        }
    }

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

    /** Used by Home quick actions to change the main view. */
    public void showView(ViewKey key) {
        if (currentController instanceof ApplicationPage oldPage)
        {
            oldPage.onPageLeave();
        }

        headerTitle.setText(titleFor(key));
        Parent node = viewCache.computeIfAbsent(key, this::loadView);
        contentHost.getChildren().setAll(node);
        if (node instanceof Region pageRoot) {
            pageRoot.setMaxHeight(Region.USE_PREF_SIZE);
        }

        Object ctrl = node.getUserData();
        if (ctrl instanceof ApplicationPage page)
        {
            page.onPageEnter();
        }

        currentController = ctrl;

        suppressNavToggleCallbacks = true;
        try {
            selectNav(key);
        } finally {
            suppressNavToggleCallbacks = false;
        }
    }

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
