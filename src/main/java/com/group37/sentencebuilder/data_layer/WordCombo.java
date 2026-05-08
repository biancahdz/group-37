/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    WordCombo.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      Object to hold the data of a word combo from the database.
 *
 * Version: 1.0
 * Created: 2026-03-25
 * Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Store the first word of the combination as a word object
 *      - Store the second word of the combination as a word object
 *      - Stores the number of times the combination has been seen from the database
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.data_layer;

import com.group37.sentencebuilder.data_layer.Word;

public class WordCombo
{
    private Word firstWord;
    private Word nextWord;
    private int comboCount;

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      A constructor to create the WordCombo object with all info stored.
     *      Stores the firstWord, nextWord, and comboCount from the database.
     * 
     * @param firstWord the first word word object of the combination
     * @param nextWord the second word word object of the combination
     * @param comboCount the number of times this combo has been seen
     */
    public WordCombo(Word firstWord, Word nextWord, int comboCount)
    {
        this.firstWord = firstWord;
        this.nextWord = nextWord;
        this.comboCount = comboCount;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Gets the comboCount from the object
     * 
     * @return the comboCount of the combination
     */
    public int getComboCount()
    {
        return comboCount;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Gets the nextWord object from this object
     * 
     * @return the nextWord word object
     */
    public Word getNext()
    {
        return nextWord;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Gets the ID of the next word from the nextWord word object
     * 
     * @return the word ID of the nextWord word object
     */
    public int getNextID()
    {
        return nextWord.getWordID();
    }
}