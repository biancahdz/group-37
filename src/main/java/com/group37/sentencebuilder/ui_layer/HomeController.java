package com.group37.sentencebuilder.ui_layer;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

/** Dashboard / welcome screen (placeholder content). */
public class HomeController {

    private Consumer<ViewKey> navigator = k -> { };

    public void setNavigator(Consumer<ViewKey> navigator) {
        this.navigator = navigator != null ? navigator : k -> { };
    }

    @FXML
    private void onQuickImport(MouseEvent e) {
        navigator.accept(ViewKey.IMPORT);
    }

    @FXML
    private void onQuickGenerate(MouseEvent e) {
        navigator.accept(ViewKey.GENERATE);
    }

    @FXML
    private void onQuickAutocomplete(MouseEvent e) {
        navigator.accept(ViewKey.AUTOCOMPLETE);
    }

    @FXML
    private void onQuickReports(MouseEvent e) {
        navigator.accept(ViewKey.REPORTS);
    }
}
