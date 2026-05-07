/**
 
------------------------------------------------------------
Project: Sentence Builder
File:    AutocompleteController.java
Author:  Huy Nong
Description:
Controller for the Autocomplete page; renders and updates mock suggestion chips based on user input.
Version: 1.0
Created: 2026-03-22
Last Modified: 2026-05-07
Responsibilities:
Initialize autocomplete UI controls and listeners
Render suggestion chips for the current prefix
------------------------------------------------------------*/

package com.group37.sentencebuilder.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.util.List;
import java.util.Locale;

/** Auto-complete screen with mock suggestion chips. */
public class AutocompleteController {

    private static final List<String> SUGGESTION_POOL = List.of(
            "the", "and", "that", "this", "with", "from", "their", "would", "there", "could",
            "through", "thought", "thread", "threshold", "thrive"
    );

    @FXML
    private TextField prefixField;

    @FXML
    private FlowPane suggestionsPane;

    @FXML
    private Label hintLabel;

    @FXML
    private void initialize() {
        prefixField.textProperty().addListener((obs, oldValue, newValue) -> refreshSuggestions(newValue));
        refreshSuggestions(prefixField.getText());
    }

    private void refreshSuggestions(String raw) {
        suggestionsPane.getChildren().clear();
        String trimmed = raw == null ? "" : raw.trim();
        String prefix = trimmed.toLowerCase(Locale.ROOT);

        for (String suggestion : SUGGESTION_POOL) {
            if (prefix.isEmpty() || suggestion.startsWith(prefix)) {
                Label chip = new Label(suggestion);
                chip.getStyleClass().add("suggestion-chip");
                chip.setOnMouseClicked(e -> prefixField.setText(suggestion + " "));
                suggestionsPane.getChildren().add(chip);
            }
        }
        hintLabel.setText(prefix.isEmpty()
                ? "Start typing — mock suggestions filter as you go."
                : "Showing mock matches for \"" + trimmed + "\".");
    }
}
