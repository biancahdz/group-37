/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    ReportRow.java
 *  Author:  Sebastian Sarinana
 *
 *  Description:
 *      Table row model for the original ui ReportsController. Holds mock
 *      generation report data as JavaFX StringProperty fields for use with
 *      TableView cell value factories.
 *
 *  Version: 1.0
 *  Created: 2026-03-22
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Store report ID, algorithm name, generation timestamp, and sentence preview
 *      - Expose each field as a StringProperty for TableColumn binding
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui.model;

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
