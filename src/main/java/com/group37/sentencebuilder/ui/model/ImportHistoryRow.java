/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    ImportHistoryRow.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      Table row model for the original ui ImportController. Holds mock import
 *      history data as JavaFX StringProperty fields for use with TableView cell
 *      value factories.
 *
 *  Version: 1.0
 *  Created: 2026-03-22
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Store file name, import date, sentence count, word count, and status
 *      - Expose each field as a StringProperty for TableColumn binding
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** Table row model for the Import screen (mock data only). */
public class ImportHistoryRow {

    private final StringProperty fileName;
    private final StringProperty importedAt;
    private final StringProperty sentences;
    private final StringProperty words;
    private final StringProperty status;

    public ImportHistoryRow(String fileName, String importedAt, String sentences, String words, String status) {
        this.fileName = new SimpleStringProperty(fileName);
        this.importedAt = new SimpleStringProperty(importedAt);
        this.sentences = new SimpleStringProperty(sentences);
        this.words = new SimpleStringProperty(words);
        this.status = new SimpleStringProperty(status);
    }

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public String getImportedAt() {
        return importedAt.get();
    }

    public StringProperty importedAtProperty() {
        return importedAt;
    }

    public String getSentences() {
        return sentences.get();
    }

    public StringProperty sentencesProperty() {
        return sentences;
    }

    public String getWords() {
        return words.get();
    }

    public StringProperty wordsProperty() {
        return words;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }
}
