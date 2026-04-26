/**
 * File: 
 * Description: 
 *
 * Author: 
 * Created: 
 * Last Modified: 2026-04-23
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.logic_layer.GeneratorLogic;

import com.group37.sentencebuilder.ui.LabelThemeRegistry;

import com.group37.sentencebuilder.data_layer.Database;
import java.util.List;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/** Sentence generator screen — runs corpus-backed generation algorithms. */
public class GenerateController implements ApplicationPage {

    enum Algorithm {
        MARKOV("Stochastic Markov chain"),
        GREEDY("Greedy Markov chain"),
        RANDOM("Random walk with seed"),
        BEAM("Beam Search with Scoring");

        private final String value;

        Algorithm(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Algorithm fromString(String input) {
            for (Algorithm s : Algorithm.values()) {
                if (s.value.equalsIgnoreCase(input)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Invalid status: " + input);
        }
    }

    @FXML
    private TextField startWordField;

    @FXML
    private ComboBox<String> algorithmCombo;

    @FXML
    private TextArea outputArea;

    @FXML
    private Label sectionEyebrowLabel;

    private final LabelThemeRegistry labelTheme = new LabelThemeRegistry();

    @FXML
    private void initialize() {
        algorithmCombo.getItems().setAll(
                "Stochastic Markov chain",
                "Greedy Markov chain",
                "Random walk with seed",
                "Beam Search with Scoring"
        );
        algorithmCombo.getSelectionModel().selectFirst();
        outputArea.setText(
                "Generated sentences will appear here!"
        );
        labelTheme.add(sectionEyebrowLabel, Color.WHITE);
    }

    @Override
    public void onPageEnter() {
        labelTheme.apply();
    }

    @Override
    public void onPageLeave() {
    }

    @FXML
    private void onGenerate() {
        String word = startWordField.getText() != null ? startWordField.getText().trim() : "";
        Algorithm algo = Algorithm.fromString(algorithmCombo.getSelectionModel().getSelectedItem());
        
        //check if word exists in db
        if(!word.isEmpty()) {
          Database db = Database.getDatabase();
          db.connect();

        if(!db.isWord(word)) {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
          alert.setTitle("Word not found");
          alert.setHeaderText("\"" + word + "\" is not in the database.");
          alert.setContentText("Would you like to add it?");

       Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
              db.addWord(word);
                outputArea.setText("\"" + word + "\" has been added to the database!\n\n" +
                  		  "Type a sentence containing \"" + word + "\" in the textbox above, then click \"Add Sentence to Database\".");
                db.disconnect();
                return; // stop no combos for word
             } else {
                outputArea.setText("Word not found.");
                db.disconnect();
                return;
              }
          }
          db.disconnect();
       }
        switch (algo)
        {
            case MARKOV -> outputArea.setText(GeneratorLogic.markov(word, 15));
            case RANDOM -> outputArea.setText(GeneratorLogic.random(word, 15));
            case GREEDY -> outputArea.setText(GeneratorLogic.greedy(word));
            case BEAM -> outputArea.setText(GeneratorLogic.beam(word, 15, 15));
        }
    }
    
    @FXML
    private void onAddSentence() {
        String sentence = outputArea.getText().trim();
        
        if(sentence.isBlank() || sentence.equals("Generated sentences will appear here!")) {
        outputArea.setText("Please enter a sentence in the output area first.");
        return;
    }

    String[] rawWords = sentence.split("\\s+");
    List<String> words = new ArrayList<>();
    for (String w : rawWords) {
        String cleaned = w.replaceAll("[^A-Za-z]", "").toLowerCase();
        if (!cleaned.isBlank()) words.add(cleaned);
    }

    Database db = Database.getDatabase();
    db.connect();
    db.addWords(words);
    for (int i = 0; i < words.size() - 1; i++) {
        Integer firstID = db.getWordID(words.get(i));
        Integer secondID = db.getWordID(words.get(i + 1));
        if (firstID != null && secondID != null) {
            db.addCombo(firstID, secondID);
        }
    }
    db.disconnect();
    outputArea.setText("Sentence added to the database! Words and combos are now available for generation.");
  }

}
