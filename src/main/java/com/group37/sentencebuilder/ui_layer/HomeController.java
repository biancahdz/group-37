/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    HomeController.java
 *  Author:  Sebastian Sarinana, Huy Nong, Cortland Kimzey
 *
 *  Description:
 *      Home/dashboard controller: shows corpus counts when connected and provides navigation shortcuts to key pages.
 *
 *  Version: 1.0
 *  Created: 2026-05-07
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Display high-level corpus metrics and update UI chrome based on theme
 *      - Route workspace card clicks to the main shell navigation callback
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;

import com.group37.sentencebuilder.ui_layer.theming.LabelThemeRegistry;
import com.group37.sentencebuilder.ui_layer.theming.UiPreferences;

import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

/** Dashboard / welcome screen with live metrics and workspace shortcuts. */
public class HomeController implements ApplicationPage, DatabasePage {

    private final LabelThemeRegistry labelTheme = new LabelThemeRegistry();

    private Database database;

    private Consumer<ViewKey> navigator = k -> { };

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Registers the navigation callback used by workspace card clicks to switch pages.
     *
     * @param navigator consumer that accepts a ViewKey to navigate to that page
     */
    public void setNavigator(Consumer<ViewKey> navigator) {
        this.navigator = navigator != null ? navigator : k -> { };
    }

    @FXML private Label txtCount;
    @FXML private Label sentenceCount;
    @FXML private Pane pageRoot;

    @FXML private Button cardImport;
    @FXML private Button cardGenerate;
    @FXML private Button cardAutocomplete;
    @FXML private Button cardReports;
    @FXML private Button cardCorpusStats;

    @FXML private Label titleImport;    @FXML private Label descImport;    @FXML private Label ctaImport;
    @FXML private Label titleGenerate;  @FXML private Label descGenerate;  @FXML private Label ctaGenerate;
    @FXML private Label titleAutocomplete; @FXML private Label descAutocomplete; @FXML private Label ctaAutocomplete;
    @FXML private Label titleReports;   @FXML private Label descReports;   @FXML private Label ctaReports;
    @FXML private Label titleCorpusStats; @FXML private Label descCorpusStats; @FXML private Label ctaCorpusStats;

    @FXML private Label introLead;
    @FXML private Label labelTxtSources;
    @FXML private Label labelSentences;
    @FXML private Label labelWorkspaces;
    @FXML private Label homeSectionEyebrow;
    @FXML private Label homeSectionTitle;

    private final InvalidationListener dashboardChromeRefresh = obs -> applyDashboardInlineText();

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Registers theme and OS color-scheme listeners for automatic chrome refresh, wires
     *      a scene-attach listener for deferred initial styling, and registers all card and
     *      hero labels with the label theme registry for dark/light mode switching.
     */
    @FXML
    private void initialize() {
        UiPreferences prefs = UiPreferences.get();
        prefs.themeProperty().addListener(dashboardChromeRefresh);
        try {
            Platform.getPreferences().colorSchemeProperty().addListener(dashboardChromeRefresh);
        } catch (Exception ignored) {
        }
        if (pageRoot != null) {
            pageRoot.sceneProperty().addListener((obs, oldS, newS) -> {
                if (newS != null) {
                    Platform.runLater(this::applyDashboardInlineText);
                }
            });
        }

        String titleStyle = "-fx-font-size: 26px; -fx-font-weight: bold;";
        String descStyle  = "-fx-font-size: 15px;";

        // Register each label once; apply() handles dark ↔ light switching.
        // title/desc use baseStyle so font-size is set via inline style (bypasses Button-graphic CSS boundary).
        labelTheme
                .add(introLead,          Color.WHITE)
                .add(labelTxtSources,    Color.WHITE)
                .add(labelSentences,     Color.WHITE)
                .add(homeSectionEyebrow, Color.WHITE)
                .add(homeSectionTitle,   Color.WHITE)
                .add(titleImport,        Color.WHITE, titleStyle)
                .add(titleGenerate,      Color.WHITE, titleStyle)
                .add(titleAutocomplete,  Color.WHITE, titleStyle)
                .add(titleReports,       Color.WHITE, titleStyle)
                .add(titleCorpusStats,   Color.WHITE, titleStyle)
                .add(descImport,         Color.WHITE, descStyle)
                .add(descGenerate,       Color.WHITE, descStyle)
                .add(descAutocomplete,   Color.WHITE, descStyle)
                .add(descReports,        Color.WHITE, descStyle)
                .add(descCorpusStats,    Color.WHITE, descStyle)
                .add(ctaImport,       () -> Color.web(ctaFillForAccentParent(ctaImport)))
                .add(ctaGenerate,     () -> Color.web(ctaFillForAccentParent(ctaGenerate)))
                .add(ctaAutocomplete, () -> Color.web(ctaFillForAccentParent(ctaAutocomplete)))
                .add(ctaReports,      () -> Color.web(ctaFillForAccentParent(ctaReports)))
                .add(ctaCorpusStats,  () -> Color.web(ctaFillForAccentParent(ctaCorpusStats)));

        Platform.runLater(this::applyDashboardInlineText);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Applies theme-aware inline text fills to all registered labels, stat tiles, and
     *      workspace cards, and sets card hover/press handlers appropriate for dark or light mode.
     */
    private void applyDashboardInlineText() {
        if (pageRoot == null) {
            return;
        }
        currentColorScheme(); // evaluated to keep fallback logic reachable across platforms
        boolean darkChrome = UiPreferences.get().isResolvedDarkSurface();

        labelTheme.apply();

        // Tree walk for stat-tile-label / section-eyebrow-teal nodes without fx:id.
        applyStatTileLabels(pageRoot, darkChrome);

        applyCardDarkBackground(darkChrome);

        // labelWorkspaces deferred to survive any concurrent clear from MainShellController.
        String workspacesStyle = darkChrome ? "-fx-text-fill: #ffffff;" : null;
        if (labelWorkspaces != null) labelWorkspaces.setStyle(workspacesStyle);
        Platform.runLater(() -> { if (labelWorkspaces != null) labelWorkspaces.setStyle(workspacesStyle); });
        Platform.runLater(() -> Platform.runLater(() -> { if (labelWorkspaces != null) labelWorkspaces.setStyle(workspacesStyle); }));
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Recursively walks the given node and applies white fill to stat-tile-label and
     *      section-eyebrow-teal nodes in dark mode, or clears fills in light mode.
     *
     * @param node the root node to walk
     * @param darkChrome true if the current surface palette is dark
     */
    private void applyStatTileLabels(Node node, boolean darkChrome) {
        if (node instanceof Label lab) {
            boolean isStat   = lab.getStyleClass().contains("stat-tile-label");
            boolean isEyebrow = lab.getStyleClass().contains("section-eyebrow-teal");
            if (isStat || isEyebrow) {
                lab.setStyle(darkChrome ? "-fx-text-fill: #ffffff;" : null);
            }
        }
        if (node instanceof javafx.scene.Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                applyStatTileLabels(child, darkChrome);
            }
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Sets inline background and hover/press styles on workspace card buttons for dark
     *      mode; clears all inline styles and mouse handlers in light mode so CSS takes over.
     *
     * @param darkChrome true if the current surface palette is dark
     */
    private void applyCardDarkBackground(boolean darkChrome) {
        Button[] cards = { cardImport, cardGenerate, cardAutocomplete, cardReports, cardCorpusStats };
        for (Button card : cards) {
            if (card == null) continue;
            if (!darkChrome) {
                card.setStyle(null);
                card.setOnMouseEntered(null);
                card.setOnMouseExited(null);
                card.setOnMousePressed(null);
                card.setOnMouseReleased(null);
            } else {
                card.setStyle("-fx-background-color: #1e1e24;");
                card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #26262d;"));
                card.setOnMouseExited(e  -> card.setStyle("-fx-background-color: #1e1e24;"));
                card.setOnMousePressed(e -> card.setStyle("-fx-background-color: #2c2c34;"));
                card.setOnMouseReleased(e -> card.setStyle("-fx-background-color: #26262d;"));
            }
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Walks up the scene graph from the CTA label to find the closest ancestor with a
     *      quick-card-accent-* class and returns the appropriate light fill hex for dark mode.
     *
     * @param cta the call-to-action label whose accent color is resolved from its parent card
     * @return hex fill color string for dark mode
     */
    private static String ctaFillForAccentParent(Labeled cta) {
        for (Node x = cta.getParent(); x != null; x = x.getParent()) {
            for (String c : x.getStyleClass()) {
                if (!c.startsWith("quick-card-accent-")) continue;
                return switch (c) {
                    case "quick-card-accent-import"   -> "#e0f2fe";
                    case "quick-card-accent-generate" -> "#ede9fe";
                    case "quick-card-accent-compose"  -> "#ccfbf1";
                    case "quick-card-accent-reports"  -> "#ffedd5";
                    default -> "#e4e4e7";
                };
            }
        }
        return "#e4e4e7";
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
     * Author: Huy Nong
     * Description:
     *      Workspace card click handlers; each delegates to the navigator to switch pages.
     */
    @FXML private void onQuickImport()      { navigator.accept(ViewKey.IMPORT); }
    @FXML private void onQuickGenerate()    { navigator.accept(ViewKey.GENERATE); }
    @FXML private void onQuickAutocomplete(){ navigator.accept(ViewKey.AUTOCOMPLETE); }
    @FXML private void onQuickReports()     { navigator.accept(ViewKey.REPORTS); }
    @FXML private void onQuickCorpusStats() { navigator.accept(ViewKey.CORPUS_STATS); }

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Applies dashboard chrome immediately and after layout, then queries the database
     *      for corpus counts and displays them in the stat tiles.
     */
    @Override
    public void onPageEnter() {
        applyDashboardInlineText();
        Platform.runLater(this::applyDashboardInlineText);
        if (database == null) {
            txtCount.setText("—");
            sentenceCount.setText("—");
            return;
        }
        if (!database.connect()) {
            txtCount.setText("—");
            sentenceCount.setText("—");
            database.disconnect();
            return;
        }
        try {
            txtCount.setText(String.valueOf(database.getTxtCount()));
            sentenceCount.setText(String.valueOf(database.getTxtSentenceCount()));
        } finally {
            database.disconnect();
        }
    }

    /**
     * Author: Cortland Kimzey
     * Description:
     *      No cleanup required when leaving the home page.
     */
    @Override
    public void onPageLeave() {
    }

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Stores the injected Database instance used to load corpus count metrics on page enter.
     *
     * @param database the shared database connection wrapper
     */
    @Override
    public void setDatabase(Database database) {
        this.database = database;
    }
}
