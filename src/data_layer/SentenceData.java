/**
 * File: SentenceData.java
 * Description: Holds Sentence data for processing.
 *
 * Author: Cortland Kimzey
 * Created: 2026-03-15
 * Last Modified: 2026-03-16
 *
 * Version: 1.0
 */

package data_layer;

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