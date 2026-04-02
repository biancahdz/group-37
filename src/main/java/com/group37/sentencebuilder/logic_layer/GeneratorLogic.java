/**
 * File: GeneratorLogic.java
 * Description: 
 *
 * Author: Cortland KImzey
 * Created: 2026-03-16
 * Last Modified: 2026-03-16
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.logic_layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.group37.sentencebuilder.logic_layer.BeamNode;

import com.group37.sentencebuilder.data_layer.Database;
import com.group37.sentencebuilder.data_layer.Word;
import com.group37.sentencebuilder.data_layer.WordCombo;

public class GeneratorLogic
{
    private static Database database = Database.getDatabase();
    private static Random rand = new Random();

    public static String greedy(String firstWord)
    {
        if (database.connect())
        {
            int currentID = 0;
            String sentence = "";

            if (firstWord.isEmpty())
            {
                currentID = database.getBestCombo(1, true);
                sentence = database.getWord(currentID);
            }
            else
            {
                currentID = database.getWordID(firstWord);
                sentence = firstWord;
            }


            for (int x = 0; x < 15; x++)
            {
                currentID = database.getBestCombo(currentID, true);
                if (currentID == 2)
                    break;
                sentence += " " + database.getWord(currentID);
            }

            database.disconnect();

            return sentence;
        }
        return "Could Not Connect to Database";
    }

    public static String random(String firstWord, int x)
    {
        if (database.connect())
        {
            int currentID = 0;
            String sentence = "";
            List<Integer> bestWords = new ArrayList<>();

            if (firstWord.isEmpty())
            {
                bestWords = database.getXBest(1, x);
                int randNum = rand.nextInt(bestWords.size());
                currentID = bestWords.get(randNum);
                sentence = database.getWord(currentID);
            }
            else
            {
                currentID = database.getWordID(firstWord);
                sentence = firstWord;
            }
            
            do
            {
                bestWords = database.getXBest(currentID, x);
                int randNum = rand.nextInt(bestWords.size());
                currentID = bestWords.get(randNum);
                if (currentID == 2)
                    break;
                sentence += " " + database.getWord(currentID);
            } while (currentID != 2);

            database.disconnect();

            return sentence;
        }
        return "Could Not Connect to Database";
    }

    public static String markov(String firstWord, int x)
    {
        if (database.connect())
        {
            int currentID = 0;
            String sentence = "";
            List<WordCombo> bestCombos = new ArrayList<>();

            if (firstWord.isEmpty())
            {
                bestCombos = database.getXBestCombos(new Word(1, "<START>"), x);
                int index = GeneratorLogic.probIndex(bestCombos);
                currentID = bestCombos.get(index).getNextID();
                sentence += database.getWord(currentID);
            }
            else
            {
                currentID = database.getWordID(firstWord);
                sentence += firstWord;
            }
            
            do
            {
                bestCombos = database.getXBestCombos(new Word(currentID), x);
                int index = GeneratorLogic.probIndex(bestCombos);
                currentID = bestCombos.get(index).getNextID();
                if (currentID == 2)
                    break;
                sentence += " " + database.getWord(currentID);
            } while (currentID != 2);

            database.disconnect();

            return sentence;
        }
        return "Could Not Connect to Database";
    }

    public static String beam(String firstWord, int n, int k)
    {
        if (!database.connect())
            return "Could Not Connect to Database";

        List<BeamNode> beam = new ArrayList<>();

        int startID;

        if (firstWord.isEmpty())
        {
            startID = 1; // <START>
        }
        else
        {
            startID = database.getWordID(firstWord);
        }

        // Initialize beam
        List<Integer> startSeq = new ArrayList<>();
        startSeq.add(startID);
        beam.add(new BeamNode(startSeq, 0.0));

        // Iterate up to max length n
        for (int step = 0; step < n; step++)
        {
            List<BeamNode> candidates = new ArrayList<>();

            for (BeamNode node : beam)
            {
                int currentID = node.getLastID();

                // If END token, keep as-is
                if (currentID == 2)
                {
                    candidates.add(node);
                    continue;
                }

                List<WordCombo> bestCombos =
                    database.getXBestCombos(new Word(currentID), k);

                int totalCount = 0;
                for (WordCombo combo : bestCombos)
                    totalCount += combo.getComboCount();

                for (WordCombo combo : bestCombos)
                {
                    int nextID = combo.getNextID();

                    // Enforce minimum length (e.g., 8 words, excluding <START>)
                    int currentLength = node.wordIDs.size();

                    if (nextID == 2 && currentLength < 9) // 1 = <START>, so 9 = 8 real words
                    {
                        continue; // skip early END
                    }

                    double prob = (double) combo.getComboCount() / totalCount;
                    double logProb = Math.log(prob);

                    List<Integer> newSeq = new ArrayList<>(node.wordIDs);
                    newSeq.add(nextID);

                    double newScore = node.score + logProb;

                    candidates.add(new BeamNode(newSeq, newScore));
                }
            }

            // Sort by score (highest first)
            candidates.sort((a, b) -> Double.compare(b.score, a.score));

            // Keep top k
            beam = candidates.subList(0, Math.min(k, candidates.size()));
        }

        // Pick best final sequence
        BeamNode best = beam.get(0);

        // Convert to sentence
        StringBuilder sentence = new StringBuilder();

        for (int id : best.wordIDs)
        {
            if (id == 1 || id == 2) continue; // skip <START>/<END>

            if (sentence.length() > 0)
                sentence.append(" ");

            sentence.append(database.getWord(id));
        }

        database.disconnect();
        return sentence.toString();
    }

    private static int probIndex(List<WordCombo> bestCombos)
    {
        int totalCount = 0;
        for (WordCombo combo : bestCombos)
        {
            totalCount += combo.getComboCount();
        }

        int r = (int) (Math.random() * totalCount);

        int cumulative = 0;
        for (int i = 0; i < bestCombos.size(); i++) {
            cumulative += bestCombos.get(i).getComboCount();
            if (r < cumulative) {
                return i;
            }
        }

        return bestCombos.size() - 1;
    }
}