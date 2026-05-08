/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    AutocompleteController.java
 *  Author:  Huy Nong, Cortland Kimzey, Bianca Hernandez
 *
 *  Description:
 *      Autocomplete screen controller. Suggests words from the corpus based on the current prefix and allows adding words.
 *
 *  Version: 1.0
 *  Created: 2026-03-21
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Query and display prefix-matched word suggestions from the database
 *      - Provide UI actions for updating the suggestions list and adding new words
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;

import com.group37.sentencebuilder.ui_layer.theming.LabelThemeRegistry;

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

    /**
     * Author: Huy Nong
     * Description:
     *      Attaches a text-change listener to the prefix field to trigger live suggestion
     *      refresh, and registers the eyebrow label for dark-mode styling.
     */
    @FXML
    private void initialize() {
        prefixField.textProperty().addListener((obs, o, n) -> refreshSuggestions(n));
        labelTheme.add(sectionEyebrowLabel, Color.WHITE);
    }

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Queries the database on a background thread for words matching the current prefix
     *      and populates the suggestion chip pane with clickable word labels.
     *
     * @param raw the current text field value used to derive the active prefix
     */
    private void refreshSuggestions(String raw) {

        suggestionsPane.getChildren().clear();

        String safeRaw = (raw == null) ? "" : raw;
        String trimmed = safeRaw.trim().toLowerCase();

        boolean stringEmpty = trimmed.isEmpty();
        boolean endsWithSpace = safeRaw.endsWith(" ");
        String[] parts = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");

        String currentWord = "";

        if (parts.length > 0) {
            currentWord = parts[parts.length - 1];
        }

        String finalCurrentWord = currentWord;

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

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Connects to the database, applies theme-aware label styling, and triggers
     *      an initial suggestion refresh for the current prefix field value.
     */
    @Override
    public void onPageEnter() {
        labelTheme.apply();
        database.connect();
        refreshSuggestions(prefixField.getText());
    }

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Disconnects from the database when the autocomplete page is hidden.
     */
    @Override
    public void onPageLeave()
    {
        database.disconnect();
    }

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Stores the injected Database instance used for word queries and autocomplete lookups.
     *
     * @param database the shared database connection wrapper
     */
    @Override
    public void setDatabase(Database database)
    {
        this.database = database;
    }
    
    /**
     * Author: Bianca Hernandez
     * Description:
     *      Extracts the last word from the prefix field and adds it to the database,
     *      then refreshes suggestions and shows a confirmation hint.
     */
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
