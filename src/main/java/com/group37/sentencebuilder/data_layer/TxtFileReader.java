/**
 * File: TxtFileReader.java
 * Description: Reads a .txt file, parses it into sentences and words,
 *              and imports the data into the SentenceBuilder database.
 *
 * Author:
 * Created: 2026-03-15
 * Last Modified: 2026-04-11
 *
 * Version: 1.0
 */
package com.group37.sentencebuilder.data_layer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.concurrent.Task;

public class TxtFileReader {

    private final String fileName;
    private final File txtFile;
    private final Database database = Database.getDatabase();

    public TxtFileReader(File file, String displayName) {
        this.fileName = displayName;
        this.txtFile = file;
    }

    public TxtFileReader(File file) {
        this.fileName = file.getName();
        this.txtFile = file;
    }

    /**
     * Splits a block of text into individual sentences using punctuation delimiters.
     *
     * @param sb the full text content
     * @return a list of cleaned sentence strings
     */
    private List<String> getSentences(StringBuilder sb) {
        String[] raw = sb.toString().trim().split("(?<=[.!?][\"',]?)(?=\\s+|$)");
        List<String> sentences = new ArrayList<>();
        for (String s : raw) {
            if (!s.isBlank()) {
                sentences.add(s.trim().replaceAll("[\"',]+$", ""));
            }
        }
        return sentences;
    }

    /**
     * Cleans and filters a raw word array into lowercase alpha-only tokens.
     *
     * @param rawWords array of raw word strings from splitting a sentence
     * @return list of cleaned, non-blank words
     */
    private List<String> cleanWords(String[] rawWords) {
        List<String> words = new ArrayList<>();
        for (String w : rawWords) {
            String cleaned = w.replaceAll("[^A-Za-z]", "").toLowerCase();
            if (!cleaned.isBlank()) {
                words.add(cleaned);
            }
        }
        return words;
    }

    private boolean addWords(List<String> words) {
        return database.addWords(words);
    }

    private boolean addSentence(List<String> words) {
        int firstID = database.getWordID(words.get(0));
        int lastID = database.getWordID(words.get(words.size() - 1));
        if (!database.addSentence(firstID, lastID)) return false;
        database.addCombo(1, firstID);
        database.addCombo(lastID, 2);
        return true;
    }

    private boolean addCombo(List<String> words) {
        if (words == null || words.size() < 2) return false;
        Map<String, Integer> wordIDs = database.getWordIDs(words);
        List<int[]> combos = new ArrayList<>();
        for (int i = 0; i < words.size() - 1; i++) {
            Integer firstID = wordIDs.get(words.get(i));
            Integer secondID = wordIDs.get(words.get(i + 1));
            if (firstID != null && secondID != null) {
                combos.add(new int[]{firstID, secondID});
            }
        }
        return database.addCombos(combos);
    }

    /**
     * Builds a list of adjacent word ID pairs for combo tracking.
     *
     * @param words list of cleaned words in a sentence
     * @return list of [wordA_id, wordB_id] pairs
     */
    private List<int[]> adjacentPairs(List<String> words) {
        if (words == null || words.size() < 2) return Collections.emptyList();
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
     * Creates a background Task that reads the file and imports all
     * sentences, words, and word combos into the database.
     *
     * @return a JavaFX Task that performs the import
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
                }

                List<String> sentences = getSentences(sb);
                int numSentences = sentences.size();
                int numWords = 0;
                int count = 0;

                database.connect();
                int txtId = database.startTxtImport(fileName);

                if (txtId <= 0) {
                    database.disconnect();
                    throw new IOException("Could not create import row for: " + fileName);
                }

                try {
                    for (String sentence : sentences) {
                        List<String> words = cleanWords(sentence.split("\\s+"));

                        if (words.isEmpty()) {
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

                return null;
            }
        };
    }
}
