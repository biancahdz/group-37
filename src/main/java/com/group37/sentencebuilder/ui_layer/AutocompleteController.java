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

import com.group37.sentencebuilder.data_layer.Database;

import com.group37.sentencebuilder.ui.LabelThemeRegistry;

import com.group37.sentencebuilder.ui_layer.ApplicationPage;
import com.group37.sentencebuilder.ui_layer.DatabasePage;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;

/** Auto-complete screen — suggestion chips from corpus word data. */
public class AutocompleteController implements ApplicationPage, DatabasePage {

    private final LabelThemeRegistry labelTheme = new LabelThemeRegistry();

    private Database database;

    @FXML
    private TextField prefixField;

    @FXML
    private FlowPane suggestionsPane;

    @FXML
    private Label hintLabel;

    @FXML
    private Label sectionEyebrowLabel;
   
    @FXML
    private Button addWordButton;

    @FXML
    private void initialize() {
        prefixField.textProperty().addListener((obs, o, n) -> refreshSuggestions(n));
        labelTheme.add(sectionEyebrowLabel, Color.WHITE);
    }

    private void refreshSuggestions(String raw) {

        suggestionsPane.getChildren().clear();

        String safeRaw = (raw == null) ? "" : raw;
        String trimmed = safeRaw.trim().toLowerCase();

        boolean stringEmpty = trimmed.isEmpty();
        boolean endsWithSpace = safeRaw.endsWith(" ");
        String[] parts = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");

        String currentWord = "";
        String previousWord = "";

        if (parts.length > 0) {
            currentWord = parts[parts.length - 1];
        }
        if (parts.length > 1) {
            previousWord = parts[parts.length - 2];
        }

        String finalCurrentWord = currentWord;
        String finalPreviousWord = previousWord;

        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                List<String> words = new ArrayList<>();

                if (stringEmpty) {
                    words = database.getXBestWords(1, 15);
                } else if (endsWithSpace) {
                    if (!finalCurrentWord.isEmpty() && database.isWord(finalCurrentWord)) {
                        int wordId = database.getWordID(finalCurrentWord);
                        words = database.getXBestWords(wordId, 15);
                    }
                } else {
                    if (!finalCurrentWord.isEmpty()) {
                        words = database.autoComplete(finalCurrentWord, 15);
                    }
                }

                return words;
            }
        };

        task.setOnSucceeded(e -> {
            List<String> words = task.getValue();
            suggestionsPane.getChildren().clear();

            for (String w : words) {
                Label chip = new Label(w);
                chip.getStyleClass().add("suggestion-chip");

                chip.setOnMouseClicked(ev -> {
                    String newText;

                    if (safeRaw.isEmpty()) {
                        newText = w + " ";
                    } 
                    else if (safeRaw.endsWith(" ")) {
                        newText = safeRaw + w + " ";
                    } 
                    else {
                        int lastSpace = safeRaw.lastIndexOf(" ");

                        if (lastSpace == -1) {
                            newText = w + " ";
                        } else {
                            newText = safeRaw.substring(0, lastSpace + 1) + w + " ";
                        }
                    }

                    prefixField.setText(newText);
                    prefixField.positionCaret(newText.length()); // move cursor to end
                });

                suggestionsPane.getChildren().add(chip);
            }

            hintLabel.setText(
                finalCurrentWord.isEmpty()
                    ? "Start typing - Suggestions filter as you go."
                    : (endsWithSpace
                        ? "Next word suggestions"
                        : "Autocompleting \"" + finalCurrentWord + "\"")
            );
        });

        task.setOnFailed(e -> {
            hintLabel.setText("Failed to load suggestions.");
            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void onPageEnter() {
        labelTheme.apply();
        database.connect();
        refreshSuggestions(prefixField.getText());
    }

    @Override
    public void onPageLeave()
    {
        database.disconnect();
    }

    @Override
    public void setDatabase(Database database)
    {
        this.database = database;
    }
    
    @FXML 
    private void onAddWord() {
         String[] parts = prefixField.getText().trim().toLowerCase().split("\\s+");
         if (parts.length == 0) return;
         String word = parts[parts.length - 1];

         if(!word.isBlank()) {
           database.addWord(word);
	   refreshSuggestions(prefixField.getText());
	   Platform.runLater(() ->
              hintLabel.setText("\"" + word + "\" has been added to the database!"));
            }
       } 
}
