/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    TxtFileReader.java
 *  Author:  Bianca Hernandez
 *
 *  Description:
 *      Reads and parses txt files to extract sentences and words, 
 *      then stores them in database. Tracks word frequencies, adjacent word
 *      combos, and sentence boundaries. 
 *
 *  Version: 1.0
 *  Created: 2026-03-15
 *  Last Modified: 2026-5-07
 *
 *  Responsibilities:
 *      - Parse txt files into individual sentences and words
 *      - Store words and combos into db
 *
 *  
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.data_layer;

import com.group37.sentencebuilder.data_layer.Database;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javafx.concurrent.Task;

public class TxtFileReader
{
    private String fileName;
    private File txtFile;
    private Database database = Database.getDatabase();

    /**
     * Description: 
     *      Constructs a txtfilereader with custom display name
     * 
     */
    public TxtFileReader(File file, String displayName) {
        this.fileName = displayName;
        this.txtFile = file;
    }

    /**
     * Description: 
     *      Constructs txtfilereader using the file's actual name
     * 
     */
    public TxtFileReader(File file) {
        this.fileName = file.getName();
        this.txtFile = file;
    }

    /**
     * Description: 
     *      Splits raw text into individual sentences using punctuation as delimeters
     * 
     * @param sb raw text as stringbuilder
     * @return list of parsed sentences
     */
    private List<String> getSentences(StringBuilder sb)
    {
        String regex = "(?<=[.!?][\"',]?)(?=\\s+|$)";
        
        String[] raw = sb.toString().trim().split(regex);

        List<String> sentences = new ArrayList<>();
        for (String s : raw) {
            if (!s.isBlank()) {
                sentences.add(s.trim().replaceAll("[\"',]+$", ""));
            }
        }

        return sentences;
    }

    /**
     * Description: 
     *      Adds a list of words to the db
     * 
     * @param list of words to add
     * @return true if successful,false otherwise
     */
    private boolean addWords(List<String> words) {
        if(!database.addWords(words))
            return false;
        return true;
    }

    /**
     * Description: 
     *      records the first and last word of a sentence and links to start and end
     * 
     * @param list of words in the sentence
     * @return true if successful
     */
    private boolean addSentence(List<String> words) {

        int firstID = database.getWordID(words.get(0));
        int lastID = database.getWordID(words.get(words.size() - 1));

        if(!database.addSentence(firstID, lastID))
            return false;

        database.addCombo(1, firstID);
        database.addCombo(lastID, 2);

        return true;
    }

    /**
     * Description:
     *      records adjacent word pairs into db 
     * 
     * @param list of words to extract pairs from
     * @return true if successful, false otherwise
     */
    private boolean addCombo(List<String> words)
    {
        if (words == null || words.size() < 2) return false;

        Map<String, Integer> wordIDs = database.getWordIDs(words);

        List<int[]> combos = new ArrayList<>();

        for (int i = 0; i < words.size() - 1; i++) {
            Integer firstID  = wordIDs.get(words.get(i));
            Integer secondID = wordIDs.get(words.get(i + 1));

            if (firstID != null && secondID != null) {
                combos.add(new int[]{firstID, secondID});
            }
        }

        return database.addCombos(combos);
    }

    /**
     * Description: 
     *      returns a list of adjacent word id pairs from a sentence
     * 
     * @param list of words to extract pairs from
     * @return list  of adjacent word ID pairs
     */
    private List<int[]> adjacentPairs(List<String> words) {
        if (words == null || words.size() < 2) {
            return Collections.emptyList();
        }
        Map<String, Integer> wordIDs = database.getWordIDs(words);
        List<int[]> pairs = new ArrayList<>();
        for (int i = 0; i < words.size() - 1; i++) {
            Integer a = wordIDs.get(words.get(i));
            Integer b = wordIDs.get(words.get(i + 1));
            if (a != null && b != null) {
                pairs.add(new int[]{a, b});
            }
        }
        return pairs;
    }

    /**
     * Description: 
     *      creates a background javafx task that reads the file,
     *      parses sentences, and stores all data into the db
     * 
     * @return JavaFX task that performs file import
     */
    public Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {

                StringBuilder sb = new StringBuilder();

                try (Scanner txtScanner = new Scanner(txtFile)) {
                    while (txtScanner.hasNextLine()) {
                        sb.append(txtScanner.nextLine()).append(" ");
                    }

                    List<String> sentences = getSentences(sb);

                    int numSentences = sentences.size();
                    int numWords = 0;
                    int count = 0;

                    database.connect();
                    int txtId = database.startTxtImport(fileName);
                    if (txtId <= 0) {
                        database.disconnect();
                        throw new IOException("Could not create import row for " + fileName);
                    }

                    try {
                        for (String sentence : sentences) {
                            String[] rawWords = sentence.split("\\s+");
                            List<String> words = new ArrayList<>();

                            for (String w : rawWords) {
                                String word = w.replaceAll("[^A-Za-z]", "").toLowerCase();
                                if (!word.isBlank()) {
                                    words.add(word);
                                }
                            }

                            if (words.isEmpty())
                            {
                                numSentences--;
                                continue;
                            }

                            numWords += words.size();

                            addWords(words);
                            database.addTxtWordOccurrences(txtId, words);
                            addSentence(words);
                            addCombo(words);
                            database.addTxtCombosForTxt(txtId, adjacentPairs(words));

                            updateProgress(++count, numSentences);
                        }

                        database.finishTxtImport(txtId, numSentences, numWords);
                    } finally {
                        database.disconnect();
                    }
                }

                return null;
            }
        };
    }
}
