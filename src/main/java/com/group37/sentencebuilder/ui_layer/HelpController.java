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
     *      Sets the support hyperlink text, registers theme and OS color-scheme listeners
     *      to reapply chrome on change, and defers an initial chrome pass to after layout.
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
     *      Opens the system default mail client addressed to the support inbox when
     *      the contact hyperlink is clicked.
     */
    @FXML
    private void onSupportMailClicked() {
        Mailto.openSupportInbox();
    }

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Reapplies help page chrome immediately and on deferred layout pulses to ensure
     *      all labels render correctly in the current light or dark theme.
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
     *      No cleanup required when leaving the help page.
     */
    @Override
    public void onPageLeave() {
    }

    /**
     * Author: Sebastian Sarinana, Cortland Kimzey, Huy Nong
     * Description:
     *      Walks the help page scene graph to style all labeled nodes, then directly
     *      applies fills to named hero and section labels with deferred retries for
     *      labels that may not yet have their skin built.
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
     *      Directly sets inline text-fill on all named hero and section labels based on
     *      the dark flag, bypassing CSS cascade issues with dark-surface palettes.
     *
     * @param dark true if the current surface palette is dark
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
     *      Recursively walks the scene graph from the given node and applies inline fill
     *      styles to every Labeled node based on its style classes and the dark flag.
     *
     * @param node the root node to walk
     * @param dark true if the current surface palette is dark
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
     *      Returns the appropriate inline style string for a help page label based on its
     *      CSS class and the dark flag; returns null to let the CSS cascade handle light mode.
     *
     * @param lab the Labeled node whose style classes determine the fill
     * @param dark true if the current surface palette is dark
     * @return inline style string or null
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
