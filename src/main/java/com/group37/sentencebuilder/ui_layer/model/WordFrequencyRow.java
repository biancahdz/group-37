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

/** Row for top-words table (UI / mock data). */
public class WordFrequencyRow {

    private final StringProperty rank;
    private final StringProperty word;
    private final StringProperty count;

    public WordFrequencyRow(String rank, String word, String count) {
        this.rank = new SimpleStringProperty(rank);
        this.word = new SimpleStringProperty(word);
        this.count = new SimpleStringProperty(count);
    }

    public StringProperty rankProperty() {
        return rank;
    }

    public StringProperty wordProperty() {
        return word;
    }

    public StringProperty countProperty() {
        return count;
    }

    public String getWord() {
        return word.get();
    }

    public String getCount() {
        return count.get();
    }
}
