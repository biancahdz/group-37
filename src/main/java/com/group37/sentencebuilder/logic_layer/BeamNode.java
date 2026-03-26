/**
 * File: BeamNode.java
 * Description: 
 *
 * Author: Cortland Kimzey
 * Created: 2026-03-25
 * Last Modified: 2026-03-25
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.logic_layer;

import java.util.ArrayList;
import java.util.List;

public class BeamNode {
    List<Integer> wordIDs;
    double score;

    public BeamNode(List<Integer> wordIDs, double score) {
        this.wordIDs = wordIDs;
        this.score = score;
    }

    public int getLastID() {
        return wordIDs.get(wordIDs.size() - 1);
    }
}