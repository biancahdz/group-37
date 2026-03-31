/**
 * File: 
 * Description: 
 *
 * Author: 
 * Created: 
 * Last Modified: 2026-03-27
 *
 * Version: 1.0
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

    public ImportHistoryRow(String fileName, String importedAt, int sentences, int words, String status) {
        this.fileName = new SimpleStringProperty(fileName);
        this.importedAt = new SimpleStringProperty(importedAt);
        this.sentences = new SimpleStringProperty(String.valueOf(sentences));
        this.words = new SimpleStringProperty(String.valueOf(words));
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
