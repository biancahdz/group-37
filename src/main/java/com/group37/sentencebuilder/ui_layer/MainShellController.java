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

import com.group37.sentencebuilder.ui.DarkSurfaceText;
import com.group37.sentencebuilder.ui.UiPreferences;

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

    /** Scene root — {@link #initialize()} sets a stable CSS id for high-specificity dark-theme text rules. */
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
    private Label sidebarTagline;

    @FXML
    private Label workspaceSectionLabel;

    @FXML
    private Label shellFooterLabel;

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
        navGroup.selectedToggleProperty().addListener((o, a, b) -> applyShellChromeInlineText());
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
     * Modena can paint ToggleButton / Label text darker than our CSS on dark palettes. Inline
     * {@code -fx-text-fill} wins over user-agent styles so inactive nav + chrome labels stay readable.
     */
    private void applyShellChromeInlineText() {
        boolean dark = UiPreferences.get().isResolvedDarkSurface();

        ToggleButton[] nav = {
                navHome, navImport, navGenerate, navAutocomplete, navReports, navCorpusStats, navSettings
        };
        if (!dark) {
            for (ToggleButton b : nav) {
                b.setTextFill(null);
                b.setStyle(null);
            }
            if (sidebarTagline != null) {
                sidebarTagline.setTextFill(null);
                sidebarTagline.setStyle(null);
            }
            if (workspaceSectionLabel != null) {
                workspaceSectionLabel.setTextFill(null);
                workspaceSectionLabel.setStyle(null);
            }
            if (shellFooterLabel != null) {
                shellFooterLabel.setTextFill(null);
                shellFooterLabel.setStyle(null);
            }
            return;
        }

        for (ToggleButton b : nav) {
            if (b.isSelected()) {
                DarkSurfaceText.forceLabeledFill(b, Color.web("#bfdbfe"));
            } else {
                DarkSurfaceText.forceLabeledFill(b, Color.color(1, 1, 1, 0.92));
            }
        }
        if (sidebarTagline != null) {
            DarkSurfaceText.forceLabeledFill(sidebarTagline, Color.color(1, 1, 1, 0.92));
        }
        if (workspaceSectionLabel != null) {
            DarkSurfaceText.forceLabeledFill(workspaceSectionLabel, Color.color(1, 1, 1, 0.92));
        }
        if (shellFooterLabel != null) {
            DarkSurfaceText.forceLabeledFill(shellFooterLabel, Color.color(1, 1, 1, 0.92));
        }
    }

    /**
     * Same issue as sidebar chrome: on dark palettes, stylesheet fills for page section eyebrows
     * (DATA IN, GENERATE, …) can render too dark. {@link Labeled#setTextFill} reaches LabeledSkin
     * more reliably than {@code -fx-text-fill} alone (see {@link HomeController#applyHomeHeroLabels}).
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
                lab.setTextFill(null);
                lab.setStyle(null);
                continue;
            }
            DarkSurfaceText.forceLabeledFill(lab, eyebrowColorForAccentClass(lab));
        }
    }

    /** Maximum contrast on #000 / #18181b; primary eyebrow is white so small-caps stay readable. */
    private static Color eyebrowColorForAccentClass(Labeled lab) {
        if (lab.getStyleClass().contains("section-eyebrow-primary")) {
            return Color.web("#ffffff");
        }
        if (lab.getStyleClass().contains("section-eyebrow-teal")) {
            return Color.web("#99f6e4");
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
     * Make mouse-wheel / trackpad scrolling work across the full workspace area,
     * not just when the cursor is over the inner content node.
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
     * A {@link ToggleGroup} clears the selected toggle when the user clicks it again.
     * Keep one workspace always selected so the sidebar highlight never disappears.
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
