package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui.AppFont;
import com.group37.sentencebuilder.ui.AppTheme;
import com.group37.sentencebuilder.ui.FontSizePreset;
import com.group37.sentencebuilder.ui.UiPreferences;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.util.function.Function;

/**
 * Settings: binds theme, font, and font size to {@link UiPreferences}.
 * ComboBox popup lists use explicit row colors per {@link AppTheme} because popup scenes do not inherit
 * {@code sb-*} CSS lookups from the main scene root.
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

        prefs.themeProperty().addListener((o, a, b) -> attachThemedPopupCells());
        attachThemedPopupCells();
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
