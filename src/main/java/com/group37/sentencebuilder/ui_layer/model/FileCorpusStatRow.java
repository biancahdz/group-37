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

/** Per-file corpus summary row (UI / mock data). */
public class FileCorpusStatRow {

    private final StringProperty fileName;
    private final StringProperty sentences;
    private final StringProperty words;
    private final StringProperty uniqueWords;
    private final StringProperty topPairCount;
    private final StringProperty imported;

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public FileCorpusStatRow(
            String fileName,
            String sentences,
            String words,
            String uniqueWords,
            String topPairCount,
            String imported) {
        this.fileName = new SimpleStringProperty(fileName);
        this.sentences = new SimpleStringProperty(sentences);
        this.words = new SimpleStringProperty(words);
        this.uniqueWords = new SimpleStringProperty(uniqueWords);
        this.topPairCount = new SimpleStringProperty(topPairCount);
        this.imported = new SimpleStringProperty(imported);
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty fileNameProperty() {
        return fileName;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty sentencesProperty() {
        return sentences;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty wordsProperty() {
        return words;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty uniqueWordsProperty() {
        return uniqueWords;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty topPairCountProperty() {
        return topPairCount;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty importedProperty() {
        return imported;
    }
}
