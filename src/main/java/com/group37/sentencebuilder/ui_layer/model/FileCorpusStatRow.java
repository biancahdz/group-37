package com.group37.sentencebuilder.ui_layer.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** Per-file corpus summary row (UI / mock data). */
public class FileCorpusStatRow {

    private final StringProperty fileName;
    private final StringProperty sentences;
    private final StringProperty words;
    private final StringProperty imported;

    public FileCorpusStatRow(String fileName, String sentences, String words, String imported) {
        this.fileName = new SimpleStringProperty(fileName);
        this.sentences = new SimpleStringProperty(sentences);
        this.words = new SimpleStringProperty(words);
        this.imported = new SimpleStringProperty(imported);
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public StringProperty sentencesProperty() {
        return sentences;
    }

    public StringProperty wordsProperty() {
        return words;
    }

    public StringProperty importedProperty() {
        return imported;
    }
}
