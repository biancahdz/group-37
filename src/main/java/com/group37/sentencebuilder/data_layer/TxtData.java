/**
 * File: TxtData.java
 * Description: Holds txt data for processing.
 *
 * Author: Cortland Kimzey
 * Created: 2026-03-15
 * Last Modified: 2026-03-16
 *
 * Version: 1.0
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