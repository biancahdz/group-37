/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    HelpController.java
 *  Author:  Sebastian Sarinana, Cortland Kimzey, Huy Nong
 *
 *  Description:
 *      Controller for the static Help page; applies theme-aware label styling and opens the support email link.
 *
 *  Version: 1.0
 *  Created: 2026-05-07
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Apply help-page chrome styling for readability in light/dark modes
 *      - Handle the “Contact support” mailto action
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui_layer.theming.UiPreferences;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;

/**
 * Static help / documentation view. Applies dark-surface text styling on page enter,
 * mirroring the approach used by HomeController and SettingsController.
 */
public class HelpController implements ApplicationPage {

    @FXML private VBox helpPageRoot;
    @FXML private javafx.scene.control.Label helpEyebrow;
    @FXML private javafx.scene.control.Label helpHeroTitle;
    @FXML private javafx.scene.control.Label helpHeroLead;
    @FXML private javafx.scene.control.Label subNavigation;
    @FXML private javafx.scene.control.Label subFirstLaunch;
    @FXML private javafx.scene.control.Label subHome;
    @FXML private javafx.scene.control.Label subImport;
    @FXML private javafx.scene.control.Label subGenerator;
    @FXML private javafx.scene.control.Label subAutocomplete;
    @FXML private javafx.scene.control.Label subReports;
    @FXML private javafx.scene.control.Label subWordAnalytics;
    @FXML private javafx.scene.control.Label subSettings;
    @FXML private javafx.scene.control.Label subTipBody;
    @FXML private javafx.scene.control.Label subTipQuestions;
    @FXML private Hyperlink supportMailHyperlink;

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Initializes the Help page chrome and wires theme listeners so label styling stays readable.
     */
    @FXML
    private void initialize() {
        supportMailHyperlink.setText(Mailto.SUPPORT_EMAIL);

        UiPreferences prefs = UiPreferences.get();
        prefs.themeProperty().addListener(obs -> applyHelpChrome());
        try {
            Platform.getPreferences().colorSchemeProperty().addListener(obs -> applyHelpChrome());
        } catch (Exception ignored) {
        }
        Node sceneTarget = helpPageRoot != null ? helpPageRoot : helpHeroTitle;
        if (sceneTarget != null) {
            sceneTarget.sceneProperty().addListener((obs, oldS, newS) -> {
                if (newS != null) Platform.runLater(this::applyHelpChrome);
            });
        }
        Platform.runLater(this::applyHelpChrome);
    }

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Opens the support email action from the help page.
     */
    @FXML
    private void onSupportMailClicked() {
        Mailto.openSupportInbox();
    }

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Called when the Help page becomes active; refreshes chrome styling immediately and on the next UI pulses.
     */
    @Override
    public void onPageEnter() {
        applyHelpChrome();
        Platform.runLater(this::applyHelpChrome);
        Platform.runLater(() -> Platform.runLater(this::applyHelpChrome));
    }

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Called when leaving the Help page. No cleanup required for this view.
     */
    @Override
    public void onPageLeave() {
    }

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Applies dark-surface text styling across the help page so static documentation remains readable.
     */
    private void applyHelpChrome() {
        boolean dark = UiPreferences.get().isResolvedDarkSurface();

        // helpPageRoot injection can silently fail when fx:id is on the FXML root element.
        // Fall back to climbing the scene graph from a reliably-injected child label.
        Node root = helpPageRoot;
        if (root == null && helpHeroTitle != null) {
            javafx.scene.Parent p = helpHeroTitle.getParent(); // intro-panel VBox
            if (p != null) root = p.getParent();               // page root VBox
        }
        if (root != null) walkHelpLabels(root, dark);

        applyDirectHeroLabels(dark);
        Platform.runLater(() -> applyDirectHeroLabels(dark));
        Platform.runLater(() -> Platform.runLater(() -> applyDirectHeroLabels(dark)));
    }

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Applies text fill directly to the hero/section labels that are injected via FXML ids.
     *
     * @param dark true when dark-surface styling should be enforced
     */
    private void applyDirectHeroLabels(boolean dark) {
        String w = dark ? "-fx-text-fill: #ffffff;" : null;
        javafx.scene.control.Label[] all = {
            helpEyebrow, helpHeroTitle, helpHeroLead,
            subNavigation, subFirstLaunch, subHome, subImport, subGenerator,
            subAutocomplete, subReports, subWordAnalytics, subSettings,
            subTipBody, subTipQuestions
        };
        for (javafx.scene.control.Label l : all) {
            if (l != null) l.setStyle(w);
        }
    }

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Walks the scene graph and applies resolved label styling to all labeled nodes.
     *
     * @param node starting node to traverse
     * @param dark true when dark-surface styling should be enforced
     */
    private static void walkHelpLabels(Node node, boolean dark) {
        if (node instanceof Labeled lab) {
            lab.setStyle(resolveHelpLabelStyle(lab, dark));
        }
        if (node instanceof javafx.scene.Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                walkHelpLabels(child, dark);
            }
        }
    }

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Determines a text style override for Help-page labels when dark mode is active.
     *
     * @param lab label node being styled
     * @param dark true when dark-surface styling should be enforced
     * @return an inline style string, or null to leave styling unchanged
     */
    private static String resolveHelpLabelStyle(Labeled lab, boolean dark) {
        if (!dark) return null;
        java.util.List<String> cls = lab.getStyleClass();
        if (cls.contains("section-heading"))   return "-fx-text-fill: #ffffff;";
        if (cls.contains("section-lead"))      return "-fx-text-fill: #ffffff;";
        if (cls.contains("section-eyebrow"))   return "-fx-text-fill: #ffffff;";
        if (cls.contains("card-title"))        return "-fx-text-fill: #ffffff;";
        if (cls.contains("card-subtitle"))     return "-fx-text-fill: #ffffff;";
        return null;
    }
}
