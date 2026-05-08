/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    ReportRow.java
 *  Author:  Huy Nong
 *
 *  Description:
 *      JavaFX TableView row model for a generated-sentence report entry (id, algorithm, time, preview).
 *
 *  Version: 1.0
 *  Created: 2026-03-22
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Store report metadata as JavaFX properties for binding and sorting
 *      - Provide getters and property accessors used by the Reports UI
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
     * Author: Huy Nong
     * Description:
     *      Constructs a reports table row for a generated sentence entry.
     *
     * @param id report identifier displayed in the table
     * @param algorithm generator algorithm label
     * @param generatedAt formatted generation timestamp
     * @param preview short preview of the generated sentence
     */
    public ReportRow(String id, String algorithm, String generatedAt, String preview) {
        this.id = new SimpleStringProperty(id);
        this.algorithm = new SimpleStringProperty(algorithm);
        this.generatedAt = new SimpleStringProperty(generatedAt);
        this.preview = new SimpleStringProperty(preview);
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current report id value for convenience (non-property access).
     *
     * @return id string
     */
    public String getId() {
        return id.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the report id.
     *
     * @return id property
     */
    public StringProperty idProperty() {
        return id;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current algorithm label for convenience (non-property access).
     *
     * @return algorithm string
     */
    public String getAlgorithm() {
        return algorithm.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the algorithm label.
     *
     * @return algorithm property
     */
    public StringProperty algorithmProperty() {
        return algorithm;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current generated-at label for convenience (non-property access).
     *
     * @return generated-at string
     */
    public String getGeneratedAt() {
        return generatedAt.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the generated-at label.
     *
     * @return generated-at property
     */
    public StringProperty generatedAtProperty() {
        return generatedAt;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the current preview text for convenience (non-property access).
     *
     * @return preview string
     */
    public String getPreview() {
        return preview.get();
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the JavaFX property for the preview text.
     *
     * @return preview property
     */
    public StringProperty previewProperty() {
        return preview;
    }
}
