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

/** Table row model for the Reports screen (mock data only). */
public class ReportRow {

    private final StringProperty id;
    private final StringProperty algorithm;
    private final StringProperty generatedAt;
    private final StringProperty preview;

    public ReportRow(String id, String algorithm, String generatedAt, String preview) {
        this.id = new SimpleStringProperty(id);
        this.algorithm = new SimpleStringProperty(algorithm);
        this.generatedAt = new SimpleStringProperty(generatedAt);
        this.preview = new SimpleStringProperty(preview);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getAlgorithm() {
        return algorithm.get();
    }

    public StringProperty algorithmProperty() {
        return algorithm;
    }

    public String getGeneratedAt() {
        return generatedAt.get();
    }

    public StringProperty generatedAtProperty() {
        return generatedAt;
    }

    public String getPreview() {
        return preview.get();
    }

    public StringProperty previewProperty() {
        return preview;
    }
}
