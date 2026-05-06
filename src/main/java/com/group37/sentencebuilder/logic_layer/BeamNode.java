/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    BeamNode.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      <description>
 *
 *  Version: 1.0
 *  Created: 2026-03-25
 *  Last Modified: 2026-03-25
 *
 *  Responsibilities:
 *      - <responsibilities 1>
 *      - <responsibilities 2>
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
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public BeamNode(List<Integer> wordIDs, double score) {
        this.wordIDs = wordIDs;
        this.score = score;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public int getLastID() {
        return wordIDs.get(wordIDs.size() - 1);
    }
}