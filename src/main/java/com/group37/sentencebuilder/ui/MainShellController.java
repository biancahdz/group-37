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

    private final Map<ViewKey, Parent> viewCache = new EnumMap<>(ViewKey.class);

    @FXML
    private void initialize() {
        /* Let page roots size to their natural height so the shell ScrollPane can scroll vertically. */
        contentHost.setMaxHeight(Region.USE_PREF_SIZE);

        wireNav(navHome, ViewKey.HOME);
        wireNav(navImport, ViewKey.IMPORT);
        wireNav(navGenerate, ViewKey.GENERATE);
        wireNav(navAutocomplete, ViewKey.AUTOCOMPLETE);
        wireNav(navReports, ViewKey.REPORTS);
        wireNav(navSettings, ViewKey.SETTINGS);

        navGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) {
                oldT.setSelected(true);
            }
        });

        selectNav(ViewKey.HOME);
    }

    private void wireNav(ToggleButton button, ViewKey key) {
        button.setUserData(key);
        button.selectedProperty().addListener((obs, was, isNow) -> {
            if (Boolean.TRUE.equals(isNow)) {
                showView(key);
            }
        });
    }

    /** Used by Home quick actions to change the main view. */
    public void showView(ViewKey key) {
        headerTitle.setText(titleFor(key));
        Parent node = viewCache.computeIfAbsent(key, this::loadView);
        contentHost.getChildren().setAll(node);
        if (node instanceof Region pageRoot) {
            pageRoot.setMaxHeight(Region.USE_PREF_SIZE);
        }
        selectNav(key);
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
            case SETTINGS -> "Settings & About";
        };
    }

    private Parent loadView(ViewKey key) {
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
            if (ctrl instanceof HomeController home) {
                home.setNavigator(this::showView);
            }
            return parent;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
