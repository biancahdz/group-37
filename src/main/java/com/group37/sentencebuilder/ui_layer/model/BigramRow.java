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

package com.group37.sentencebuilder.ui_layer.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** Row for word-pair (bigram) co-occurrence table (UI / mock data). */
public class BigramRow {

    private final StringProperty rank;
    private final StringProperty firstWord;
    private final StringProperty secondWord;
    private final StringProperty comboCount;

    public BigramRow(String rank, String firstWord, String secondWord, String comboCount) {
        this.rank = new SimpleStringProperty(rank);
        this.firstWord = new SimpleStringProperty(firstWord);
        this.secondWord = new SimpleStringProperty(secondWord);
        this.comboCount = new SimpleStringProperty(comboCount);
    }

    public StringProperty rankProperty() {
        return rank;
    }

    public StringProperty firstWordProperty() {
        return firstWord;
    }

    public StringProperty secondWordProperty() {
        return secondWord;
    }

    public StringProperty comboCountProperty() {
        return comboCount;
    }

    public String getFirstWord() {
        return firstWord.get();
    }

    public String getSecondWord() {
        return secondWord.get();
    }

    public String getComboCount() {
        return comboCount.get();
    }

    /** Short label for charts, e.g. {@code of → the}. */
    public String getPairLabel() {
        return getFirstWord() + " → " + getSecondWord();
    }
}
