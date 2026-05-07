/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    GeneratorLogic.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      This holds all the algorithms used to generate sentences
 *
 *  Version: 1.0
 *  Created: 2026-03-16
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Hold the logic used to generate a sentence
 *      - Return a sentence using a specific algorithm
 * ------------------------------------------------------------
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


    /**
     * Author: Cortland Kimzey
     * Description: 
     *      This is the Greedy Markov Chain algorithm, it takes the current word in the sentence and check the database for 
     *      the word combinations using the current word and always grabs the next word with the highest count of combinations
     * 
     * @param firstWord the first word used to create the sentence and if empty we will choose the best word from the database
     * @return sentence generate with words and word combinations from the database using the greedy markov chain algorithm
     */
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


    /**
     * Author: Cortland Kimzey
     * Description: 
     *      This is the Random walk from seed algorithm, it uses the current word (the seed) and grabs the top x word
     *      combinations from the database. It then randomly grabs a word from the top x options and uses it as the next word
     * 
     * @param firstWord the first word used to create the sentence and if empty if choose on of x words that could start a sentence
     * @param x the number of words to grab from the database to randomly choose from
     * @return the random sentence generated using the random walk from seed algorithm
     */
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

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      This is the Stochastic Markov Chain Algorithm. It uses a probability map of the top x word combinations
     *      and randomly selects a the next word. This continues until it randomly chooses a combination that ends the sentence.
     * 
     * @param firstWord the first word used to create the sentence and if empty if choose one of x words that could start a sentence
     * @param x the number of words to grab from the database to randomly choose from
     * @return the random sentence generated using the Stochastic Markov Chain Algorithm
     */
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

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      This algorithm was created vibe coding with copilot. This is the beam search algorithm, at each step of the sentence
     *      the algorithm explores sentences of up to n depth. It grabs the k most likely word combinations based on the combination count
     *      It then keeps k of the top sentences of the highest combo count. The algorithm ends with the highest Scoring
     *      completed sentence.
     * 
     * @param firstWord the first word used to create the sentence and if empty the algorithm runs from the start word classifier.
     * @param n the depth of each sentence to keep track of
     * @param k the number of sentences to keep track of
     * @return the sentence generated using the beam search algorithm
     */
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

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      This creates a probability map using the individual count of each combo in the list and the
     *      total count of all individual counts summed. It then used the probability map to randomly choose
     *      the index of a word in the list.
     * 
     * @param bestCombos a list of word combinations from the database
     * @return the index of the randomly chosen word.
     */
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

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      This is used to add to the reports table in the database. It is given the algorithm used to create the sentence
     *      and the sentence that was generated. It adds it to the table with the time created.
     * 
     * @param algorithm the name of the algorithm used to generate the sentence
     * @param text the sentence generated using the algorithm
     * @return true if the report was added, otherwise false
     */
    public static boolean addReport(String algorithm, String text)
    {
        if (database.connect())
        {
            database.setReport(algorithm, text);

            database.disconnect();

            return true;
        }
        return false;
    }
}