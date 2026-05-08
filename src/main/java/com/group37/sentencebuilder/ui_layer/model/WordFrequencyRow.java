/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    WordFrequencyRow.java
 *  Author:  Huy Nong
 *
 *  Description:
 *      JavaFX TableView row model for the Word Analytics “top words” table (rank, word, count).
 *
 *  Version: 1.0
 *  Created: 2026-03-31
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Store rank/word/count values as JavaFX properties for binding
 *      - Provide property accessors used by the analytics UI controller
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** Row for top-words table (UI / mock data). */
public class WordFrequencyRow {

    private final StringProperty rank;
    private final StringProperty word;
    private final StringProperty count;

    /**
     * Author: Huy Nong
     * Description:
     *      Constructs a table row representing one ranked word and its occurrence count.
     *
     * @param rank display rank (e.g., "1")
     * @param word token text
     * @param count formatted occurrence count
     */
    public WordFrequencyRow(String rank, String word, String count) {
        this.rank = new SimpleStringProperty(rank);
        this.word = new SimpleStringProperty(word);
        this.count = new SimpleStringProperty(count);
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
     *      Returns the JavaFX property for the displayed word text.
     *
     * @return word property
     */
    public StringProperty wordProperty() {
        return word;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the displayed count.
     *
     * @return count property
     */
    public StringProperty countProperty() {
        return count;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current word value for convenience (non-property access).
     *
     * @return word string
     */
    public String getWord() {
        return word.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current count value for convenience (non-property access).
     *
     * @return count string
     */
    public String getCount() {
        return count.get();
    }
}
