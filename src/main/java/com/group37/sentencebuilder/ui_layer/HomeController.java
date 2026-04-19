package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;

import com.group37.sentencebuilder.ui.LabelThemeRegistry;
import com.group37.sentencebuilder.ui.UiPreferences;

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

        // Register each label once; apply() handles dark ↔ light switching.
        labelTheme
                .add(introLead,          Color.WHITE)
                .add(labelTxtSources,    Color.WHITE)
                .add(labelSentences,     Color.WHITE)
                .add(homeSectionEyebrow, Color.WHITE)
                .add(homeSectionTitle,   Color.WHITE)
                .add(titleImport,        Color.WHITE)
                .add(titleGenerate,      Color.WHITE)
                .add(titleAutocomplete,  Color.WHITE)
                .add(titleReports,       Color.WHITE)
                .add(titleCorpusStats,   Color.WHITE)
                .add(descImport,         Color.WHITE)
                .add(descGenerate,       Color.WHITE)
                .add(descAutocomplete,   Color.WHITE)
                .add(descReports,        Color.WHITE)
                .add(descCorpusStats,    Color.WHITE)
                .add(ctaImport,       () -> Color.web(ctaFillForAccentParent(ctaImport)))
                .add(ctaGenerate,     () -> Color.web(ctaFillForAccentParent(ctaGenerate)))
                .add(ctaAutocomplete, () -> Color.web(ctaFillForAccentParent(ctaAutocomplete)))
                .add(ctaReports,      () -> Color.web(ctaFillForAccentParent(ctaReports)))
                .add(ctaCorpusStats,  () -> Color.web(ctaFillForAccentParent(ctaCorpusStats)));

        Platform.runLater(this::applyDashboardInlineText);
    }

    private void applyDashboardInlineText() {
        if (pageRoot == null) {
            return;
        }
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

    private static ColorScheme currentColorScheme() {
        try {
            return Platform.getPreferences().getColorScheme();
        } catch (Exception e) {
            return ColorScheme.LIGHT;
        }
    }

    @FXML private void onQuickImport()      { navigator.accept(ViewKey.IMPORT); }
    @FXML private void onQuickGenerate()    { navigator.accept(ViewKey.GENERATE); }
    @FXML private void onQuickAutocomplete(){ navigator.accept(ViewKey.AUTOCOMPLETE); }
    @FXML private void onQuickReports()     { navigator.accept(ViewKey.REPORTS); }
    @FXML private void onQuickCorpusStats() { navigator.accept(ViewKey.CORPUS_STATS); }

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

    @Override
    public void onPageLeave() {
    }

    @Override
    public void setDatabase(Database database) {
        this.database = database;
    }
}
