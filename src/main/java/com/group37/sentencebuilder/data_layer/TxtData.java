/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    TxtData.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      Holds txt data for processing.
 *
 *  Version: 1.0
 *  Created: 2026-03-15
 *  Last Modified: 2026-03-16
 *
 *  Responsibilities:
 *      - <responsibilities 1>
 *      - <responsibilities 2>
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.data_layer;

public class TxtData
{
    public final int txtID;
    public final String FileName;
    public final int numSentences;

    public TxtData(int txtID, String FileName, int numSentences) {
        this.txtID = txtID;
        this.FileName = FileName;
        this.numSentences = numSentences;
    }
}