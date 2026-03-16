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

package data_layer;

import data_layer.Database;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class TxtFileReader
{
    private String fileName;
    private File txtFile;
    private Database database = Database.getDatabase();

    public TxtFileReader(String fileName) {
        this.fileName = fileName;
        this.txtFile = new File(fileName);
    }

    private List<String> getSentences(StringBuilder sb) {
        // Handles ., ?, ! followed by space or end of input
        String regex = "(?<=[.!?])(?=\\s+|$)";
        String[] raw = sb.toString().trim().split(regex);

        List<String> sentences = new ArrayList<>();
        for (String s : raw) {
            if (!s.isBlank()) {
                sentences.add(s.trim());
            }
        }

        return sentences;
    }

    private boolean addWords(List<String> words) {
        for (String word : words) {
            if(!database.addWord(word))
                return false;
        }
        return true;
    }

    private boolean addSentence(List<String> words) {
        if(!database.addSentence(database.getWordID(words.get(0)),database.getWordID(words.get(words.size() - 1))))
            return false;

        return true;
    }

    private boolean addCombo(List<String> words) {
        for (int i = 0; i < words.size() - 1; i++) {
            int firstID  = database.getWordID(words.get(i));
            int secondID = database.getWordID(words.get(i + 1));

            database.addCombo(firstID, secondID);
        }
        return true;
    }

    public boolean processTxt() {
        StringBuilder sb = new StringBuilder();

        try (Scanner txtScanner = new Scanner(txtFile)) {
            while (txtScanner.hasNextLine())
            {
                sb.append(txtScanner.nextLine()).append(" ");
            }

            List<String> sentences = getSentences(sb);

            database.connect();
            int i = 0;
            for (String sentence : sentences) {
                String[] rawWords = sentence.split("\\s+");
                List<String> words = new ArrayList<>();

                for (String w : rawWords) {
                    String word = w.replaceAll("[^A-Za-z]", "").toLowerCase();
                    if (!word.isBlank()) {
                        words.add(word);
                    }
                }

                addWords(words);
                addSentence(words);
                addCombo(words);
                System.out.println("IT: " + i++);
            }
            database.disconnect();

            System.out.println("Txt Processed");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}