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

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public WordFrequencyRow(String rank, String word, String count) {
        this.rank = new SimpleStringProperty(rank);
        this.word = new SimpleStringProperty(word);
        this.count = new SimpleStringProperty(count);
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty rankProperty() {
        return rank;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty wordProperty() {
        return word;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty countProperty() {
        return count;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getWord() {
        return word.get();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getCount() {
        return count.get();
    }
}
