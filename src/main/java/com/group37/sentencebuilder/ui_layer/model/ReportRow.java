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

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public ReportRow(String id, String algorithm, String generatedAt, String preview) {
        this.id = new SimpleStringProperty(id);
        this.algorithm = new SimpleStringProperty(algorithm);
        this.generatedAt = new SimpleStringProperty(generatedAt);
        this.preview = new SimpleStringProperty(preview);
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getId() {
        return id.get();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty idProperty() {
        return id;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getAlgorithm() {
        return algorithm.get();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty algorithmProperty() {
        return algorithm;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getGeneratedAt() {
        return generatedAt.get();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty generatedAtProperty() {
        return generatedAt;
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getPreview() {
        return preview.get();
    }

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public StringProperty previewProperty() {
        return preview;
    }
}
