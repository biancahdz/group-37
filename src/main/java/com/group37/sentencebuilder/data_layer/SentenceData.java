/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    SentenceData.java
 *  Author:  
 *
 *  Description:
 *      Holds Sentence data for processing.
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

public class SentenceData
{
    public final int firstWordID;
    public final int lastWordID;
    public final int sentenceCount;

    public SentenceData(int firstWordID, int lastWordID, int sentenceCount) {
        this.firstWordID = firstWordID;
        this.lastWordID = lastWordID;
        this.sentenceCount = sentenceCount;
    }
}