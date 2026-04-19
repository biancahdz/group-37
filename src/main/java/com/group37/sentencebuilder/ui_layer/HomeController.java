/**
 * File: HomeController.java
 * Description: 
 *
 * Author: 
 * Created: 
 * Last Modified: 2026-03-26
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;

import com.group37.sentencebuilder.ui.DarkSurfaceText;
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

    private Database database;

    private Consumer<ViewKey> navigator = k -> { };

    public void setNavigator(Consumer<ViewKey> navigator) {
        this.navigator = navigator != null ? navigator : k -> { };
    }

    @FXML
    private Label txtCount;

    @FXML
    private Label sentenceCount;

    @FXML
    private Pane pageRoot;

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

    @FXML
    private Label homeSectionEyebrow;

    @FXML
    private Label homeSectionTitle;

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
        Platform.runLater(this::applyDashboardInlineText);
    }

    /**
     * Same idea as {@code MainShellController#applyShellChromeInlineText}: on the dark palette,
     * LabeledSkin sometimes ignores {@code #sb-shell-root.theme-default} text fills for dashboard nodes.
     */
    private void applyDashboardInlineText() {
        if (pageRoot == null) {
            return;
        }
        boolean darkChrome = UiPreferences.get().isResolvedDarkSurface();

        Color titleCol = darkChrome ? Color.WHITE : null;
        Color descCol  = darkChrome ? Color.WHITE : null;
        Label[] titles = { titleImport, titleGenerate, titleAutocomplete, titleReports, titleCorpusStats };
        Label[] descs  = { descImport,  descGenerate,  descAutocomplete,  descReports,  descCorpusStats };
        Label[] ctas   = { ctaImport,   ctaGenerate,   ctaAutocomplete,   ctaReports,   ctaCorpusStats };
        for (Label l : titles) { if (l == null) continue; if (darkChrome) DarkSurfaceText.forceLabeledFill(l, titleCol); else DarkSurfaceText.clearForcedLabeledPaint(l); }
        for (Label l : descs)  { if (l == null) continue; if (darkChrome) DarkSurfaceText.forceLabeledFill(l, descCol);  else DarkSurfaceText.clearForcedLabeledPaint(l); }
        for (int i = 0; i < ctas.length; i++) {
            Label l = ctas[i];
            Button card = new Button[]{ cardImport, cardGenerate, cardAutocomplete, cardReports, cardCorpusStats }[i];
            if (l == null) continue;
            if (darkChrome) DarkSurfaceText.forceLabeledFill(l, Color.web(ctaFillForAccentParent(card)));
            else DarkSurfaceText.clearForcedLabeledPaint(l);
        }

        for (Node n : pageRoot.lookupAll(".quick-card-cta")) {
            if (!(n instanceof Labeled lab)) {
                continue;
            }
            if (!darkChrome) {
                lab.setStyle(null);
                continue;
            }
            String fill = ctaFillForAccentParent(lab);
            lab.setStyle("-fx-text-fill: " + fill + ";");
        }

        Label[] staticLabels = { labelTxtSources, labelSentences, introLead };
        for (Label l : staticLabels) {
            if (l == null) continue;
            l.setStyle(darkChrome ? "-fx-text-fill: #ffffff;" : null);
        }

        // Walk the subtree directly (no CSS-lookup dependency) to style any stat-tile-label
        // or section-eyebrow-teal that @FXML injection / lookupAll may have missed.
        applyStatTileLabels(pageRoot, darkChrome);

        applyCardDarkBackground(darkChrome);
        applyHomeHeroLabels(darkChrome);

        // WORKSPACES label: apply last and defer twice so it survives any deferred clears
        // from MainShellController's applyWorkspaceEyebrowInlineText.
        String workspacesStyle = darkChrome ? "-fx-text-fill: #ffffff;" : null;
        if (labelWorkspaces != null) labelWorkspaces.setStyle(workspacesStyle);
        Platform.runLater(() -> { if (labelWorkspaces != null) labelWorkspaces.setStyle(workspacesStyle); });
        Platform.runLater(() -> Platform.runLater(() -> { if (labelWorkspaces != null) labelWorkspaces.setStyle(workspacesStyle); }));
    }

    /**
     * Recursively walks {@code node}'s subtree and applies inline white fill to every
     * stat-tile-label and section-eyebrow-teal (e.g. "WORKSPACES") found, regardless of
     * whether they carry an fx:id.
     */
    private void applyStatTileLabels(Node node, boolean darkChrome) {
        if (node instanceof Label lab) {
            boolean isStat = lab.getStyleClass().contains("stat-tile-label");
            boolean isEyebrow = lab.getStyleClass().contains("section-eyebrow-teal");
            if (isStat || isEyebrow) {
                if (darkChrome) {
                    lab.setStyle("-fx-text-fill: #ffffff;");
                } else {
                    lab.setStyle(null);
                }
            }
        }
        if (node instanceof javafx.scene.Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                applyStatTileLabels(child, darkChrome);
            }
        }
    }

    private void styleCardLabels(Node node, boolean darkChrome) {
        if (node == null) return;
        if (node instanceof Labeled lab) {
            if (lab.getStyleClass().contains("quick-card-title")) {
                if (darkChrome) DarkSurfaceText.forceLabeledFill(lab, javafx.scene.paint.Color.WHITE);
                else DarkSurfaceText.clearForcedLabeledPaint(lab);
            } else if (lab.getStyleClass().contains("quick-card-desc")) {
                if (darkChrome) DarkSurfaceText.forceLabeledFill(lab, javafx.scene.paint.Color.web("#e4e4e7"));
                else DarkSurfaceText.clearForcedLabeledPaint(lab);
            } else if (lab.getStyleClass().contains("quick-card-cta")) {
                if (darkChrome) DarkSurfaceText.forceLabeledFill(lab, javafx.scene.paint.Color.web(ctaFillForAccentParent(lab)));
                else DarkSurfaceText.clearForcedLabeledPaint(lab);
            }
        }
        if (node instanceof javafx.scene.Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                styleCardLabels(child, darkChrome);
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

    /** Eyebrow + hero titles: {@link Label#setTextFill} reaches LabeledSkin reliably; CSS alone can lose to Modena. */
    private void applyHomeHeroLabels(boolean darkChrome) {
        if (homeSectionEyebrow != null) {
            if (!darkChrome) {
                homeSectionEyebrow.setTextFill(null);
                homeSectionEyebrow.setStyle(null);
            } else {
                DarkSurfaceText.forceLabeledFill(homeSectionEyebrow, Color.WHITE);
            }
        }
        if (homeSectionTitle != null) {
            if (!darkChrome) {
                homeSectionTitle.setTextFill(null);
                homeSectionTitle.setStyle(null);
            } else {
                homeSectionTitle.setTextFill(Color.WHITE);
                homeSectionTitle.setStyle("-fx-opacity: 1;");
            }
        }
    }

    private static String ctaFillForAccentParent(Labeled cta) {
        for (Node x = cta.getParent(); x != null; x = x.getParent()) {
            for (String c : x.getStyleClass()) {
                if (!c.startsWith("quick-card-accent-")) {
                    continue;
                }
                return switch (c) {
                    case "quick-card-accent-import" -> "#e0f2fe";
                    case "quick-card-accent-generate" -> "#ede9fe";
                    case "quick-card-accent-compose" -> "#ccfbf1";
                    case "quick-card-accent-reports" -> "#ffedd5";
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

    @FXML
    private void onQuickImport() {
        navigator.accept(ViewKey.IMPORT);
    }

    @FXML
    private void onQuickGenerate() {
        navigator.accept(ViewKey.GENERATE);
    }

    @FXML
    private void onQuickAutocomplete() {
        navigator.accept(ViewKey.AUTOCOMPLETE);
    }

    @FXML
    private void onQuickReports() {
        navigator.accept(ViewKey.REPORTS);
    }

    @FXML
    private void onQuickCorpusStats() {
        navigator.accept(ViewKey.CORPUS_STATS);
    }

    @Override
    public void onPageEnter()
    {
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
    public void onPageLeave()
    {
    }

    @Override
    public void setDatabase(Database database)
    {
        this.database = database;
    }
}
