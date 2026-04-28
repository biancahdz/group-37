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
        
        switch (algo)
        {
            case MARKOV -> outputArea.setText(GeneratorLogic.markov(word, 15));
            case RANDOM -> outputArea.setText(GeneratorLogic.random(word, 15));
            case GREEDY -> outputArea.setText(GeneratorLogic.greedy(word));
            case BEAM -> outputArea.setText(GeneratorLogic.beam(word, 15, 15));
        }
    }
   

}
