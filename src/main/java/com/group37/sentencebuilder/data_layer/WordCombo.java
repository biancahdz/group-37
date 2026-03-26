/**
 * File: word.java
 * Description: Object to hold the data of a word object.
 *
 * Author: Cortland Kimzey
 * Created: 2026-03-25
 * Last Modified: 2026-03-25
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.data_layer;

import com.group37.sentencebuilder.data_layer.Word;

public class WordCombo
{
    private Word firstWord;
    private Word nextWord;
    private int comboCount;

    public WordCombo(Word firstWord, Word nextWord, int comboCount)
    {
        this.firstWord = firstWord;
        this.nextWord = nextWord;
        this.comboCount = comboCount;
    }

    public int getComboCount()
    {
        return comboCount;
    }

    public Word getNext()
    {
        return nextWord;
    }

    public int getNextID()
    {
        return nextWord.getWordID();
    }
}