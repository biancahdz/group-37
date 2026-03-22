package com.group37.sentencebuilder.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/** Sentence generator screen (output is placeholder text). */
public class GenerateController {

    @FXML
    private TextField startWordField;

    @FXML
    private ComboBox<String> algorithmCombo;

    @FXML
    private TextArea outputArea;

    @FXML
    private void initialize() {
        algorithmCombo.getItems().setAll(
                "Markov chain (bigram)",
                "Weighted next-word",
                "Random walk with seed",
                "Template-guided (future)"
        );
        algorithmCombo.getSelectionModel().selectFirst();
        outputArea.setText(
                "Generated sentences will appear here after the backend is connected.\n\n"
                        + "Example placeholder:\n"
                        + "“The morning light folded quietly across the page while words "
                        + "found their own rhythm.”"
        );
    }

    @FXML
    private void onGenerate() {
        String word = startWordField.getText() != null ? startWordField.getText().trim() : "";
        String algo = algorithmCombo.getSelectionModel().getSelectedItem();
        outputArea.setText(
                "(UI stub) Would generate using algorithm: " + algo + "\n"
                        + "Starting hint: " + (word.isEmpty() ? "(none)" : word) + "\n\n"
                        + "Hook this button to your generator service when ready."
        );
    }
}
