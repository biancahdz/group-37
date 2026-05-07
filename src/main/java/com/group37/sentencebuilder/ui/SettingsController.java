/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    SettingsController.java
 *  Author:  Huy Nong, Sebastian Sarinana
 *
 *  Description:
 *      Original settings page controller. Binds theme, font, and font size
 *      combo boxes to UiPreferences and applies themed popup row colors for
 *      ComboBox lists that do not inherit main scene CSS.
 *
 *  Version: 1.0
 *  Created: 2026-03-22
 *  Last Modified: 2026-03-27
 *
 *  Responsibilities:
 *      - Bind theme, font, and font size combos to UiPreferences
 *      - Apply per-theme popup row background and text colors in ComboBox lists
 *      - Listen to theme changes and refresh popup cell factories
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.util.function.Function;

/*
 * Settings: binds theme, font, and font size to UiPreferences — one place drives the whole shell.
 * ComboBox popup lists use explicit row colors per AppTheme because popup scenes do not inherit
 * sb-* CSS lookups from the main scene root.
 */
public class SettingsController {

    @FXML
    private ComboBox<AppTheme> themeCombo;

    @FXML
    private ComboBox<AppFont> fontCombo;

    @FXML
    private ComboBox<FontSizePreset> fontSizeCombo;

    @FXML
    private void initialize() {
        UiPreferences prefs = UiPreferences.get();

        // Populate combo with all theme options and set the current active theme
        themeCombo.setItems(FXCollections.observableArrayList(AppTheme.values()));
        themeCombo.setValue(prefs.getTheme());
        // User picks a theme → push into UiPreferences so the whole shell updates
        themeCombo.valueProperty().addListener((obs, o, n) -> {
            if (n != null) {
                prefs.setTheme(n);
            }
        });
        // Theme changed externally (e.g. another controller) → keep this combo in sync
        prefs.themeProperty().addListener((obs, o, n) -> {
            if (n != null && themeCombo.getValue() != n) {
                themeCombo.setValue(n);
            }
        });

        // Same bidirectional pattern for font family
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

        // Same bidirectional pattern for font size preset
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

        // Re-apply popup row colors whenever the theme changes, then apply once immediately at startup
        prefs.themeProperty().addListener((o, a, b) -> attachThemedPopupCells());
        attachThemedPopupCells();
    }

    // Replace all three combo cell factories so their popup rows use colors matching the active theme
    private void attachThemedPopupCells() {
        themeCombo.setCellFactory(lv -> themedCell(AppTheme::toString));
        themeCombo.setButtonCell(themedButtonCell(AppTheme::toString));

        fontCombo.setCellFactory(lv -> themedCell(AppFont::toString));
        fontCombo.setButtonCell(themedButtonCell(AppFont::toString));

        fontSizeCombo.setCellFactory(lv -> themedCell(FontSizePreset::toString));
        fontSizeCombo.setButtonCell(themedButtonCell(FontSizePreset::toString));
    }

    // Drop-down list cell: applies theme-matched background/text color and re-applies on selection change
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

            // Called when the row gains or loses the selection highlight — refresh colors to match
            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                applyPopupRowStyle(this);
            }
        };
    }

    // The closed button cell only shows the selected label text — no row color needed here
    private <T> ListCell<T> themedButtonCell(Function<T, String> label) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(label.apply(item));
                }
            }
        };
    }

    /*
     * Popup rows live in a separate JavaFX scene and don't inherit CSS variables from the main shell,
     * so we apply inline styles using per-theme hex values from AppTheme instead of CSS lookups.
     */
    private void applyPopupRowStyle(ListCell<?> cell) {
        if (cell.isEmpty()) {
            cell.setStyle(null);
            return;
        }
        if (cell.isSelected()) {
            // Selected row always uses high-contrast black/white regardless of theme
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
