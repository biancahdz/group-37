/**
 * File: TxtFileReader.java
 * Description: 
 *
 * Author: 
 * Created: 2026-03-15
 * Last Modified: 2026-03-16
 *
 * Version: 1.0
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

    public TxtFileReader(String fileName) {
        this.fileName = fileName;
        this.txtFile = new File(fileName);
    }

    public TxtFileReader(File file) {
        this.fileName = file.getName();
        this.txtFile = file;
    }

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

    private boolean addWords(List<String> words) {
        if(!database.addWords(words))
            return false;
        return true;
    }

    private boolean addSentence(List<String> words) {

        int firstID = database.getWordID(words.get(0));
        int lastID = database.getWordID(words.get(words.size() - 1));

        if(!database.addSentence(firstID, lastID))
            return false;

        database.addCombo(1, firstID);
        database.addCombo(lastID, 2);

        return true;
    }

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