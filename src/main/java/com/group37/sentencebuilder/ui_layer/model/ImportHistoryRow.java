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

/** Table row model for the Import screen (mock data only). */
public class ImportHistoryRow {

    private final StringProperty fileName;
    private final StringProperty importedAt;
    private final StringProperty sentences;
    private final StringProperty words;
    private final StringProperty status;

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public ImportHistoryRow(String fileName, String importedAt, int sentences, int words, String status) {
        this.fileName = new SimpleStringProperty(fileName);
        this.importedAt = new SimpleStringProperty(importedAt);
        this.sentences = new SimpleStringProperty(String.valueOf(sentences));
        this.words = new SimpleStringProperty(String.valueOf(words));
        this.status = new SimpleStringProperty(status);
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getFileName() {
        return fileName.get();
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
    public String getImportedAt() {
        return importedAt.get();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty importedAtProperty() {
        return importedAt;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getSentences() {
        return sentences.get();
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
    public String getWords() {
        return words.get();
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
    public String getStatus() {
        return status.get();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty statusProperty() {
        return status;
    }
}
