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

public class Word
{
    private int wordID;
    private String word;
    private int count;

    public Word(int wordID, String word, int count)
    {
        this.wordID = wordID;
        this.word = word;
        this.count = count;
    }

    public Word(int wordID, String word)
    {
        this.wordID = wordID;
        this.word = word;
    }

    public Word(String word)
    {
        this.word = word;
    }

    public Word(int wordID)
    {
        this.wordID = wordID;
    }

    public Word() {}

    public int getWordID()
    {
        return wordID;
    }

    public void setWordID(int wordID)
    {
        this.wordID = wordID;
    }

    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
}