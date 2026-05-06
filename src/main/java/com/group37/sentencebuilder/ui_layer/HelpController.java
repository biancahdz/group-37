/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    .java
 *  Author:  
 *
 *  Description:
 *      <description>
 *
 *  Version: 1.0
 *  Created: 
 *  Last Modified: 
 *
 *  Responsibilities:
 *      - <responsibilities 1>
 *      - <responsibilities 2>
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui.UiPreferences;

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
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
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
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    @FXML
    private void onSupportMailClicked() {
        Mailto.openSupportInbox();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    @Override
    public void onPageEnter() {
        applyHelpChrome();
        Platform.runLater(this::applyHelpChrome);
        Platform.runLater(() -> Platform.runLater(this::applyHelpChrome));
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    @Override
    public void onPageLeave() {
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
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
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
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
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
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
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
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
