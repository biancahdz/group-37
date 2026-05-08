/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    FileCorpusStatRow.java
 *  Author:  Huy Nong
 *
 *  Description:
 *      JavaFX TableView row model representing per-file corpus summary metrics
 *      (sentences, tokens, unique words, top pair count, import time).
 *
 *  Version: 1.0
 *  Created: 2026-05-06
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Store per-file summary metrics as JavaFX properties for binding
 *      - Provide property accessors used by the analytics UI controller
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
     * Author: Huy Nong
     * Description:
     *      Constructs a per-file summary row for the analytics file table.
     *
     * @param fileName file name / label
     * @param sentences formatted sentence count
     * @param words formatted token count
     * @param uniqueWords formatted unique-word count (or placeholder)
     * @param topPairCount formatted top-bigram count (or placeholder)
     * @param imported formatted import timestamp
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
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the file name.
     *
     * @return file-name property
     */
    public StringProperty fileNameProperty() {
        return fileName;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the formatted sentence count.
     *
     * @return sentences property
     */
    public StringProperty sentencesProperty() {
        return sentences;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the formatted token count.
     *
     * @return words property
     */
    public StringProperty wordsProperty() {
        return words;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the formatted unique-word count.
     *
     * @return unique-words property
     */
    public StringProperty uniqueWordsProperty() {
        return uniqueWords;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the formatted top-pair count.
     *
     * @return top-pair-count property
     */
    public StringProperty topPairCountProperty() {
        return topPairCount;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the import timestamp string.
     *
     * @return imported-at property
     */
    public StringProperty importedProperty() {
        return imported;
    }
}
