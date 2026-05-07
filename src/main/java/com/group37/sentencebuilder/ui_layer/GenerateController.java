/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    GenerateController.java
 *  Author:  Huy Nong, Cortland Kimzey
 *
 *  Description:
 *      Sentence generator controller. Configures algorithm selection and triggers corpus-backed generation through {@link GeneratorLogic}.
 *
 *  Version: 1.0
 *  Created: 2026-05-07
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Collect generator inputs (start word, algorithm) and display generated output
 *      - Coordinate UI state with backend generation logic and (optionally) database availability
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.logic_layer.GeneratorLogic;

import com.group37.sentencebuilder.ui_layer.theming.LabelThemeRegistry;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/** Sentence generator screen — runs corpus-backed generation algorithms. */
public class GenerateController implements ApplicationPage {

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
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

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
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

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    @Override
    public void onPageEnter() {
        labelTheme.apply();
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    @Override
    public void onPageLeave() {
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    @FXML
    private void onGenerate() {
        String word = startWordField.getText() != null ? startWordField.getText().trim() : "";
        Algorithm algo = Algorithm.fromString(algorithmCombo.getSelectionModel().getSelectedItem());
        String text = "";
        String algorithm = "";
        
        switch (algo)
        {
            case MARKOV -> {
                text = GeneratorLogic.markov(word, 15);
                algorithm = "Stochastic Markov chain";
            }
            case RANDOM -> {
                text = GeneratorLogic.random(word, 15);
                algorithm = "Random walk with seed";
            }
            case GREEDY -> {
                text = GeneratorLogic.greedy(word);
                algorithm = "Greedy Markov chain";
            }
            case BEAM -> {
                text = GeneratorLogic.beam(word, 15, 15);
                algorithm = "Beam Search with Scoring";
            }
        }

        GeneratorLogic.addReport(algorithm, text);
        outputArea.setText(text);
    }
}
