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

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public BigramRow(String rank, String firstWord, String secondWord, String comboCount) {
        this.rank = new SimpleStringProperty(rank);
        this.firstWord = new SimpleStringProperty(firstWord);
        this.secondWord = new SimpleStringProperty(secondWord);
        this.comboCount = new SimpleStringProperty(comboCount);
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
    public StringProperty firstWordProperty() {
        return firstWord;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty secondWordProperty() {
        return secondWord;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty comboCountProperty() {
        return comboCount;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getFirstWord() {
        return firstWord.get();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getSecondWord() {
        return secondWord.get();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getComboCount() {
        return comboCount.get();
    }

    /**
     * Author: 
     * Description: 
     *       Short label for charts, e.g. {@code of → the}.
     * 
     * @param input description
     * @return result description
     */
    public String getPairLabel() {
        return getFirstWord() + " → " + getSecondWord();
    }
}
