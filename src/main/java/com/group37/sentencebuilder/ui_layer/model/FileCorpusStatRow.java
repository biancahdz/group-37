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

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public StringProperty sentencesProperty() {
        return sentences;
    }

    public StringProperty wordsProperty() {
        return words;
    }

    public StringProperty uniqueWordsProperty() {
        return uniqueWords;
    }

    public StringProperty topPairCountProperty() {
        return topPairCount;
    }

    public StringProperty importedProperty() {
        return imported;
    }
}
