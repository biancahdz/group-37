/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    ImportHistoryRow.java
 *  Author:  Huy Nong
 *
 *  Description:
 *      JavaFX TableView row model for the Import history table (file name, import time, counts, status).
 *
 *  Version: 1.0
 *  Created: 2026-05-06
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Store import history values as JavaFX properties for binding
 *      - Provide getters and property accessors used by the import UI
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** Table row model for the Import screen (mock data only). */
public class ImportHistoryRow {

    private final StringProperty fileName;
    private final StringProperty importedAt;
    private final StringProperty sentences;
    private final StringProperty words;
    private final StringProperty status;

    /**
     * Author: Huy Nong
     * Description:
     *      Constructs a table row representing one import event and its summary counts.
     *
     * @param fileName file name displayed in the table
     * @param importedAt formatted import timestamp
     * @param sentences sentence count for the import
     * @param words token count for the import
     * @param status import status label
     */
    public ImportHistoryRow(String fileName, String importedAt, int sentences, int words, String status) {
        this.fileName = new SimpleStringProperty(fileName);
        this.importedAt = new SimpleStringProperty(importedAt);
        this.sentences = new SimpleStringProperty(String.valueOf(sentences));
        this.words = new SimpleStringProperty(String.valueOf(words));
        this.status = new SimpleStringProperty(status);
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current file name value for convenience (non-property access).
     *
     * @return file name string
     */
    public String getFileName() {
        return fileName.get();
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
     *      Returns the current import timestamp label for convenience (non-property access).
     *
     * @return imported-at string
     */
    public String getImportedAt() {
        return importedAt.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the import timestamp label.
     *
     * @return imported-at property
     */
    public StringProperty importedAtProperty() {
        return importedAt;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current sentence count label for convenience (non-property access).
     *
     * @return sentences string
     */
    public String getSentences() {
        return sentences.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the sentence count label.
     *
     * @return sentences property
     */
    public StringProperty sentencesProperty() {
        return sentences;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current token count label for convenience (non-property access).
     *
     * @return words string
     */
    public String getWords() {
        return words.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the token count label.
     *
     * @return words property
     */
    public StringProperty wordsProperty() {
        return words;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current status label for convenience (non-property access).
     *
     * @return status string
     */
    public String getStatus() {
        return status.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the status label.
     *
     * @return status property
     */
    public StringProperty statusProperty() {
        return status;
    }
}
