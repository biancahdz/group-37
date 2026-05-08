/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    SettingsController.java
 *  Author:  Sebastian Sarinana, Cortland Kimzey, Huy Nong
 *
 *  Description:
 *      Settings screen controller for appearance preferences (theme, font, font size) and support links.
 *
 *  Version: 1.0
 *  Created: 2026-05-07
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Bind UI controls to UiPreferences for theme/font settings
 *      - Apply chrome styling so labels remain legible in light/dark modes
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui_layer.theming.AppFont;
import com.group37.sentencebuilder.ui_layer.theming.AppTheme;
import com.group37.sentencebuilder.ui_layer.theming.FontSizePreset;
import com.group37.sentencebuilder.ui_layer.theming.LabelThemeRegistry;
import com.group37.sentencebuilder.ui_layer.theming.UiPreferences;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.function.Function;

/** Settings: binds theme, font, and font size to UiPreferences. Popup rows use explicit colors because popup scenes do not inherit sb-* CSS lookups from the main scene root. */
public class SettingsController implements ApplicationPage {

    private final LabelThemeRegistry labelTheme = new LabelThemeRegistry();

    private Runnable onLogout;

    @FXML
    private ComboBox<AppTheme> themeCombo;

    @FXML
    private ComboBox<AppFont> fontCombo;

    @FXML
    private ComboBox<FontSizePreset> fontSizeCombo;

    @FXML private Pane settingsPageRoot;
    @FXML private javafx.scene.control.Label settingsHeroTitle;
    @FXML private javafx.scene.control.Label settingsHeroLead;
    @FXML private javafx.scene.control.Label settingsAppearanceTitle;
    @FXML private javafx.scene.control.Label settingsAppearanceSub;
    @FXML private javafx.scene.control.Label settingsAboutTitle;
    @FXML private javafx.scene.control.Label settingsAboutSub;
    @FXML private javafx.scene.control.Label sessionSubtitle;

    @FXML
    private Hyperlink contactMailHyperlink;

    private final InvalidationListener settingsChromeRefresh = obs -> applySettingsPageChrome();

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Registers the logout callback that is invoked when the user clicks the logout button.
     *
     * @param onLogout runnable to execute on logout
     */
    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Fires the registered logout callback when the logout button is activated.
     */
    @FXML
    private void onLogoutClicked() {
        if (onLogout != null) {
            onLogout.run();
        }
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Opens the system default mail client addressed to the support inbox when the
     *      contact hyperlink is activated.
     */
    @FXML
    private void onContactMailClicked() {
        Mailto.openSupportInbox();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Populates the theme, font, and font-size combos with bidirectional bindings to
     *      UiPreferences, registers all hero and section labels for dark-mode styling, attaches
     *      themed popup cells, and registers OS color-scheme and scene listeners for auto-refresh.
     */
    @FXML
    private void initialize() {
        contactMailHyperlink.setText(Mailto.SUPPORT_EMAIL);

        UiPreferences prefs = UiPreferences.get();

        themeCombo.setItems(FXCollections.observableArrayList(AppTheme.values()));
        themeCombo.setValue(prefs.getTheme());
        themeCombo.valueProperty().addListener((obs, o, n) -> {
            if (n != null) {
                prefs.setTheme(n);
            }
        });
        prefs.themeProperty().addListener((obs, o, n) -> {
            if (n != null && themeCombo.getValue() != n) {
                themeCombo.setValue(n);
            }
        });

        fontCombo.setItems(FXCollections.observableArrayList(AppFont.values()));
        fontCombo.setValue(prefs.getFont());
        fontCombo.valueProperty().addListener((obs, o, n) -> {
            if (n != null) {
                prefs.setFont(n);
            }
        });
        prefs.fontProperty().addListener((obs, o, n) -> {
            if (n != null && fontCombo.getValue() != n) {
                fontCombo.setValue(n);
            }
        });

        fontSizeCombo.setItems(FXCollections.observableArrayList(FontSizePreset.values()));
        fontSizeCombo.setValue(prefs.getFontSize());
        fontSizeCombo.valueProperty().addListener((obs, o, n) -> {
            if (n != null) {
                prefs.setFontSize(n);
            }
        });
        prefs.fontSizeProperty().addListener((obs, o, n) -> {
            if (n != null && fontSizeCombo.getValue() != n) {
                fontSizeCombo.setValue(n);
            }
        });

        // Register each label once; apply() handles dark ↔ light switching.
        labelTheme
                .add(settingsHeroTitle,       Color.web("#ffffff"))
                .add(settingsHeroLead,        Color.web("#e4e4e7"))
                .add(settingsAppearanceTitle, Color.web("#fafafa"))
                .add(settingsAppearanceSub,   Color.web("#b4b4bc"))
                .add(settingsAboutTitle,      Color.web("#fafafa"))
                .add(settingsAboutSub,        Color.web("#b4b4bc"))
                .add(sessionSubtitle,         Color.web("#b4b4bc"));

        prefs.themeProperty().addListener((o, a, b) -> {
            attachThemedPopupCells();
            applySettingsPageChrome();
            Platform.runLater(this::applySettingsPageChrome);
        });
        try {
            Platform.getPreferences().colorSchemeProperty().addListener(settingsChromeRefresh);
        } catch (Exception ignored) {
        }
        if (settingsPageRoot != null) {
            settingsPageRoot.sceneProperty().addListener((obs, oldS, newS) -> {
                if (newS != null) {
                    Platform.runLater(this::applySettingsPageChrome);
                }
            });
        }

        for (ComboBox<?> cb : new ComboBox<?>[] { themeCombo, fontCombo, fontSizeCombo }) {
            if (cb != null) {
                cb.skinProperty().addListener((o, a, b) -> Platform.runLater(this::paintComboSubstructure));
            }
        }

        attachThemedPopupCells();
        Platform.runLater(this::applySettingsPageChrome);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Applies label theme fills and repaints the combo substructure immediately and
     *      after the next layout pulse to handle deferred skin construction.
     */
    private void applySettingsPageChrome() {
        labelTheme.apply();
        paintComboSubstructure();
        Platform.runLater(this::paintComboSubstructure);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Applies settings page chrome on enter and defers two additional passes to ensure
     *      all styled nodes are rendered correctly after layout completes.
     */
    @Override
    public void onPageEnter() {
        applySettingsPageChrome();
        Platform.runLater(this::applySettingsPageChrome);
        Platform.runLater(() -> Platform.runLater(this::applySettingsPageChrome));
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      No cleanup required when leaving the settings page.
     */
    @Override
    public void onPageLeave() {
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Clears all inline styles on each preference combo and its skin nodes, then calls
     *      applyCss() so the settings-pref-combo stylesheet rules own the appearance in both
     *      light and dark modes without inline-style interference.
     */
    private void paintComboSubstructure() {
        for (ComboBox<?> combo : new ComboBox<?>[] { themeCombo, fontCombo, fontSizeCombo }) {
            if (combo == null) {
                continue;
            }
            combo.setStyle(null);
            clearComboSkinStyles(combo);
            combo.applyCss();
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Clears inline styles from the combo box itself and its key internal skin nodes
     *      so the CSS cascade can apply palette-correct colors without interference.
     *
     * @param combo the ComboBox whose skin nodes should be cleared
     */
    private static void clearComboSkinStyles(ComboBox<?> combo) {
        combo.setStyle(null);
        for (String sel : new String[] { ".combo-box-base", ".list-cell", ".arrow-button", ".arrow" }) {
            Node n = combo.lookup(sel);
            if (n != null) {
                n.setStyle(null);
            }
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Sets themed cell factories and button cells on all preference combos so popup
     *      rows render with palette-correct colors independent of the main scene CSS root.
     */
    private void attachThemedPopupCells() {
        themeCombo.setCellFactory(lv -> themedCell(AppTheme::toString));
        themeCombo.setButtonCell(themedButtonCell(AppTheme::toString));

        fontCombo.setCellFactory(lv -> themedCell(AppFont::toString));
        fontCombo.setButtonCell(themedButtonCell(AppFont::toString));

        fontSizeCombo.setCellFactory(lv -> themedCell(FontSizePreset::toString));
        fontSizeCombo.setButtonCell(themedButtonCell(FontSizePreset::toString));
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns a ListCell that displays items using the given label function and applies
     *      popup row styling on every update and selection change.
     *
     * @param label function mapping a combo item to its display string
     * @return a themed ListCell for use as a popup row
     */
    private <T> ListCell<T> themedCell(Function<T, String> label) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                    return;
                }
                setText(label.apply(item));
                applyPopupRowStyle(this);
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                applyPopupRowStyle(this);
            }
        };
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns a ListCell used as the closed combo button cell. Clears inline styles so
     *      the settings-pref-combo CSS rules own the text color in both light and dark modes.
     *
     * @param label function mapping a combo item to its display string
     * @return a ListCell for use as the combo button cell
     */
    private <T> ListCell<T> themedButtonCell(Function<T, String> label) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                    return;
                }
                setText(label.apply(item));
                setStyle(null);
            }
        };
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Applies inline background and text-fill styles to a popup list cell based on
     *      whether it is selected or unselected, reading the active theme for unselected colors.
     *
     * @param cell the ListCell to style
     */
    private void applyPopupRowStyle(ListCell<?> cell) {
        if (cell.isEmpty()) {
            cell.setStyle(null);
            return;
        }
        if (cell.isSelected()) {
            cell.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
        } else {
            AppTheme t = UiPreferences.get().getTheme();
            cell.setStyle("-fx-background-color: "
                    + t.comboPopupRowBgHex()
                    + "; -fx-text-fill: "
                    + t.comboPopupRowTextHex()
                    + ";");
        }
    }
}
