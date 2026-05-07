/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    word.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      Object to hold the data of a word from the database.
 *
 *  Version: 1.0
 *  Created: 2026-03-25
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Holds the ID of the word stored in the database
 *      - Holds a string of the actual word itself
 *      - Holds the count of how many times the word has been seen
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.data_layer;

public class Word
{
    private int wordID;
    private String word;
    private int count;

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      A constructor to create the word object with all info stored.
     *      Stores the wordID, word, count from the database.
     * 
     * @param wordID the ID for the word in the database
     * @param word the actual word from the database
     * @param count the number of times this word has been seen
     */
    public Word(int wordID, String word, int count)
    {
        this.wordID = wordID;
        this.word = word;
        this.count = count;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      A constructor to create a word object without the count stored.
     * 
     * @param wordID the ID for the word in the database
     * @param word the actual word from the database
     */
    public Word(int wordID, String word)
    {
        this.wordID = wordID;
        this.word = word;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      A constructor to create a word object only storing the actual word string.
     * 
     * @param word the actual word from the database
     */
    public Word(String word)
    {
        this.word = word;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      A constructor to create a word object only storing the word ID.
     * 
     * @param wordID the ID for the word in the database
     */
    public Word(int wordID)
    {
        this.wordID = wordID;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      A constructor to create a empty word object.
     */
    public Word() {}

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Gets the word ID from the object
     * 
     * @return the word ID stored in the object
     */
    public int getWordID()
    {
        return wordID;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Changes or stores the word ID of the object
     * 
     * @param wordID the word ID to be stored
     */
    public void setWordID(int wordID)
    {
        this.wordID = wordID;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Gets the word string from the object
     * 
     * @return the word stored in the object
     */
    public String getWord()
    {
        return word;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Changes or stores the word of the object
     * 
     * @param word the word to be stored
     */
    public void setWord(String word)
    {
        this.word = word;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Gets the word count from the object
     * 
     * @return the word count of the word
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      Changes or stores the word count of the object
     * 
     * @param count the word count to be stored
     */
    public void setCount(int count)
    {
        this.count = count;
    }
}