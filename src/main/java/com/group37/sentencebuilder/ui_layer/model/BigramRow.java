/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    BigramRow.java
 *  Author:  Huy Nong
 *
 *  Description:
 *      JavaFX TableView row model for the Word Analytics “top bigrams” table
 *      (rank, first word, second word, count).
 *
 *  Version: 1.0
 *  Created: 2026-05-06
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Store bigram values as JavaFX properties for binding
 *      - Provide a display-friendly label for charts and tables
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** Row for word-pair (bigram) co-occurrence table (UI / mock data). */
public class BigramRow {

    private final StringProperty rank;
    private final StringProperty firstWord;
    private final StringProperty secondWord;
    private final StringProperty comboCount;

    /**
     * Author: Huy Nong
     * Description:
     *      Constructs a table row representing one ranked bigram and its occurrence count.
     *
     * @param rank display rank (e.g., "1")
     * @param firstWord first token in the pair
     * @param secondWord second token in the pair
     * @param comboCount formatted pair count
     */
    public BigramRow(String rank, String firstWord, String secondWord, String comboCount) {
        this.rank = new SimpleStringProperty(rank);
        this.firstWord = new SimpleStringProperty(firstWord);
        this.secondWord = new SimpleStringProperty(secondWord);
        this.comboCount = new SimpleStringProperty(comboCount);
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the displayed rank.
     *
     * @return rank property
     */
    public StringProperty rankProperty() {
        return rank;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the first word in the pair.
     *
     * @return first-word property
     */
    public StringProperty firstWordProperty() {
        return firstWord;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the second word in the pair.
     *
     * @return second-word property
     */
    public StringProperty secondWordProperty() {
        return secondWord;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the displayed count of the pair.
     *
     * @return combo-count property
     */
    public StringProperty comboCountProperty() {
        return comboCount;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current first-word value for convenience (non-property access).
     *
     * @return first word string
     */
    public String getFirstWord() {
        return firstWord.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current second-word value for convenience (non-property access).
     *
     * @return second word string
     */
    public String getSecondWord() {
        return secondWord.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current pair-count value for convenience (non-property access).
     *
     * @return combo count string
     */
    public String getComboCount() {
        return comboCount.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns a short label for charts, e.g. "of → the".
     *
     * @return pair label
     */
    public String getPairLabel() {
        return getFirstWord() + " → " + getSecondWord();
    }
}
