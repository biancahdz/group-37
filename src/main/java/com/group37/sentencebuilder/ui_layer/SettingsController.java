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
 *      - Bind UI controls to {@link UiPreferences} for theme/font settings
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

/**
 * Settings: binds theme, font, and font size to {@link UiPreferences}.
 * ComboBox popup lists use explicit row colors per {@link AppTheme} because popup scenes do not inherit
 * {@code sb-*} CSS lookups from the main scene root.
 */
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
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
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
    private void onLogoutClicked() {
        if (onLogout != null) {
            onLogout.run();
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
    @FXML
    private void onContactMailClicked() {
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

    private void applySettingsPageChrome() {
        labelTheme.apply();
        paintComboSubstructure();
        Platform.runLater(this::paintComboSubstructure);
    }

    @Override
    public void onPageEnter() {
        applySettingsPageChrome();
        Platform.runLater(this::applySettingsPageChrome);
        Platform.runLater(() -> Platform.runLater(this::applySettingsPageChrome));
    }

    @Override
    public void onPageLeave() {
    }

    /**
     * Settings preference combos use {@code settings-pref-combo} + CSS only: light field and dark text on
     * {@code sb-dark-ui} (readable, stable across theme toggles). Do not paint inline dark chrome here —
     * that fought stylesheets and caused muddy initial text vs. white-on-light after System ↔ Invert.
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

    private static void clearComboSkinStyles(ComboBox<?> combo) {
        combo.setStyle(null);
        for (String sel : new String[] { ".combo-box-base", ".list-cell", ".arrow-button", ".arrow" }) {
            Node n = combo.lookup(sel);
            if (n != null) {
                n.setStyle(null);
            }
        }
    }

    private void attachThemedPopupCells() {
        themeCombo.setCellFactory(lv -> themedCell(AppTheme::toString));
        themeCombo.setButtonCell(themedButtonCell(AppTheme::toString));

        fontCombo.setCellFactory(lv -> themedCell(AppFont::toString));
        fontCombo.setButtonCell(themedButtonCell(AppFont::toString));

        fontSizeCombo.setCellFactory(lv -> themedCell(FontSizePreset::toString));
        fontSizeCombo.setButtonCell(themedButtonCell(FontSizePreset::toString));
    }

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
     * Closed combo value: no inline styles — {@code settings-pref-combo} rules in {@code app-theme.css}
     * own text on both {@code sb-light-ui} and {@code sb-dark-ui}. Popup rows use {@link #themedCell} +
     * {@link #applyPopupRowStyle}.
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
