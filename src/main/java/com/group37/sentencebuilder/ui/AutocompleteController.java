package com.group37.sentencebuilder.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

/** Auto-complete screen with mock suggestion chips. */
public class AutocompleteController {

    @FXML
    private TextField prefixField;

    @FXML
    private FlowPane suggestionsPane;

    @FXML
    private Label hintLabel;

    @FXML
    private void initialize() {
        prefixField.textProperty().addListener((obs, o, n) -> refreshSuggestions(n));
        refreshSuggestions(prefixField.getText());
    }

    private void refreshSuggestions(String raw) {
        suggestionsPane.getChildren().clear();
        String prefix = raw == null ? "" : raw.trim().toLowerCase();
        String[] pool = {"the", "and", "that", "this", "with", "from", "their", "would", "there", "could",
                "through", "thought", "thread", "threshold", "thrive"};
        for (String w : pool) {
            if (prefix.isEmpty() || w.startsWith(prefix)) {
                Label chip = new Label(w);
                chip.getStyleClass().add("suggestion-chip");
                chip.setOnMouseClicked(e -> prefixField.setText(w + " "));
                suggestionsPane.getChildren().add(chip);
            }
        }
        hintLabel.setText(prefix.isEmpty()
                ? "Start typing — mock suggestions filter as you go."
                : "Showing mock matches for \"" + raw.trim() + "\".");
    }
}
