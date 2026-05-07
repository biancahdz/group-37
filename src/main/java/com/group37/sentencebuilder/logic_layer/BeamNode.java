/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    BeamNode.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      The object was vibe coded with copilot.
 *      It holds the partial sentences used in the beam search algorithm.
 *      Stores the list of word IDs and the total log-probability score of the sequence.
 *      
 *
 *  Version: 1.0
 *  Created: 2026-03-25
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Holds a list of words IDs for the beam search algorithm
 *      - Tracks the log-probability score of the partial sentence
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.logic_layer;

import java.util.ArrayList;
import java.util.List;

public class BeamNode {
    List<Integer> wordIDs;
    double score;

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Creates a partial sentence node for the beam seach algorithm. It keeps a list of the
     *      word IDs in the sentence as well as the log-probability score of the sequence.
     * 
     * @param wordIDs the list of word Ids used to create the partial sentence
     * @param score the total log-probability score of the sequence
     */
    public BeamNode(List<Integer> wordIDs, double score) {
        this.wordIDs = wordIDs;
        this.score = score;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Grabs the last word in the stored partial sentence.
     * 
     * @return the last word ID in the word list
     */
    public int getLastID() {
        return wordIDs.get(wordIDs.size() - 1);
    }
}