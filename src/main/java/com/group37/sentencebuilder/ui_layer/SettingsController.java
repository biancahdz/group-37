package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui.AppFont;
import com.group37.sentencebuilder.ui.AppTheme;
import com.group37.sentencebuilder.ui.FontSizePreset;
import com.group37.sentencebuilder.ui.DarkSurfaceText;
import com.group37.sentencebuilder.ui.UiPreferences;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Labeled;
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

    private Runnable onLogout;

    @FXML
    private ComboBox<AppTheme> themeCombo;

    @FXML
    private ComboBox<AppFont> fontCombo;

    @FXML
    private ComboBox<FontSizePreset> fontSizeCombo;

    @FXML
    private Pane settingsPageRoot;

    @FXML
    private Hyperlink contactMailHyperlink;

    private final InvalidationListener settingsChromeRefresh = obs -> applySettingsPageChrome();

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    @FXML
    private void onLogoutClicked() {
        if (onLogout != null) {
            onLogout.run();
        }
    }

    @FXML
    private void onContactMailClicked() {
        Mailto.openSupportInbox();
    }

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

        prefs.themeProperty().addListener((o, a, b) -> {
            attachThemedPopupCells();
            applySettingsPageChrome();
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
        applySettingsInlineText();
        paintComboSubstructure();
        Platform.runLater(this::paintComboSubstructure);
    }

    /**
     * Mirrors {@link HomeController} / shell chrome: Modena can ignore stylesheet fills for Labels on the
     * dark palette; inline {@code -fx-text-fill} keeps PROJECT, headings, and about text readable.
     */
    private void applySettingsInlineText() {
        if (settingsPageRoot == null) {
            return;
        }
        boolean darkChrome = UiPreferences.get().isResolvedDarkSurface();

        for (Node n : settingsPageRoot.lookupAll(".section-eyebrow")) {
            if (!(n instanceof Labeled lab)) {
                continue;
            }
            if (!darkChrome) {
                lab.setTextFill(null);
                lab.setStyle(null);
                continue;
            }
            if (lab.getStyleClass().contains("section-eyebrow-primary")) {
                DarkSurfaceText.forceLabeledFill(lab, Color.WHITE);
            } else {
                DarkSurfaceText.forceLabeledFill(lab, Color.color(1, 1, 1, 0.95));
            }
        }

        applyLabeledClass(darkChrome, ".section-heading", "#ffffff");
        applyLabeledClass(darkChrome, ".section-lead", "#e4e4e7");
        applyLabeledClass(darkChrome, ".card-title", "#fafafa");
        applyLabeledClass(darkChrome, ".about-title", "#fafafa");
        applyLabeledClass(darkChrome, ".about-body", "#b4b4bc");
    }

    private void applyLabeledClass(boolean darkChrome, String cssClass, String fillWhenDark) {
        for (Node n : settingsPageRoot.lookupAll(cssClass)) {
            if (!(n instanceof Labeled lab)) {
                continue;
            }
            if (!darkChrome) {
                lab.setStyle(null);
            } else {
                lab.setStyle("-fx-text-fill: " + fillWhenDark + ";");
            }
        }
    }

    /**
     * Modena often keeps a light {@code .combo-box-base} even when root tokens are dark. Inline styles on
     * skin nodes (after lookup) match our {@code #sb-shell-root.sb-dark-ui} combo rules.
     */
    private void paintComboSubstructure() {
        boolean dark = UiPreferences.get().isResolvedDarkSurface();
        for (ComboBox<?> combo : new ComboBox<?>[] { themeCombo, fontCombo, fontSizeCombo }) {
            if (combo == null) {
                continue;
            }
            if (!dark) {
                combo.setStyle(null);
                clearComboSkinStyles(combo);
                continue;
            }
            combo.setStyle("-fx-background-color: transparent;");
            Node base = combo.lookup(".combo-box-base");
            if (base != null) {
                base.setStyle(
                        "-fx-background-color: #27272a; -fx-background-radius: 10; -fx-border-radius: 10; "
                                + "-fx-border-width: 1; -fx-border-color: #3f3f46;");
            }
            /* Button cell text is styled in themedButtonCell — do not set .list-cell here or it fights updateItem. */
            Node arrowBtn = combo.lookup(".arrow-button");
            if (arrowBtn != null) {
                arrowBtn.setStyle(
                        "-fx-background-color: #3f3f46; -fx-border-color: #52525b; "
                                + "-fx-border-width: 0 0 0 1;");
            }
            Node arrow = combo.lookup(".arrow");
            if (arrow != null) {
                arrow.setStyle("-fx-background-color: #e4e4e7;");
            }
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

    @Override
    public void onPageEnter() {
        applySettingsPageChrome();
    }

    @Override
    public void onPageLeave() {
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
     * Closed combo value. Only set {@code -fx-text-fill} on dark surfaces — no cell background here
     * (that reintroduces the white arrow block). Base/arrow are painted in {@link #paintComboSubstructure}.
     * Popup rows use {@link #themedCell} + {@link #applyPopupRowStyle}.
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
                if (UiPreferences.get().isResolvedDarkSurface()) {
                    setStyle("-fx-text-fill: #fafafa; -fx-opacity: 1;");
                } else {
                    setStyle(null);
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
