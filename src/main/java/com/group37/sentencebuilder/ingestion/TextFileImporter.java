package src.main.java.com.group37.sentencebuilder.ingestion;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

import src.main.java.com.group37.sentencebuilder.DAO.*;

/*==============================================================*/
/*  File Name   : TextFileImporter.java                         */
/*  Description : Reads a .txt file, parses it into sentences   */
/*                and words, and inserts everything into the     */
/*                SentenceBuilder MySQL database.               */
/*                                                              */
/*  HOW TO USE:                                                 */
/*  1. Make sure MySQL is running                               */
/*  2. Update DB_PASSWORD below with your MySQL root password   */
/*  3. Run the program                                          */
/*  4. When prompted, enter the full path to your .txt file     */
/*     Example: C:\Users\YourName\Desktop\pride_and_prejudice.txt*/
/*==============================================================*/

public class TextFileImporter {

    public static int importFile(File file, Connection conn) throws Exception {
        WordDAO     wordDAO     = new WordDAO(conn);
        NextWordDAO nextWordDAO = new NextWordDAO(conn);
        SentenceDAO sentenceDAO = new SentenceDAO(conn);
        TxtFileDAO  txtFileDAO  = new TxtFileDAO(conn);

        String content = new String(Files.readAllBytes(file.toPath()));
        String[] rawSentences = content.split("[.!?]+");

        int totalWords     = 0;
        int totalSentences = 0;

        System.out.println("Total sentences found: " + rawSentences.length);

        // Insert txt record FIRST so we have a valid txtID
        int txtID = txtFileDAO.insertTxtFile(file.getName(), 1, 0, 0);
        System.out.println("Created txt record with ID: " + txtID);

        for (int s = 0; s < rawSentences.length; s++) {
            if (s % 500 == 0) {
                System.out.println("Progress: " + s + " / " + rawSentences.length + " sentences processed...");
            }

            String raw = rawSentences[s].trim();
            if (raw.isEmpty()) continue;

            String[] tokens = raw.split("\\s+");
            List<String> cleanWords = new ArrayList<>();
            for (String token : tokens) {
                String clean = token.replaceAll("[^a-zA-Z'-]", "").toLowerCase().trim();
                if (!clean.isEmpty() && !clean.equals("-") && !clean.equals("'")) {
                    cleanWords.add(clean);
                }
            }

            if (cleanWords.size() < 2) continue;

            int firstWordID = -1;
            int lastWordID  = -1;
            int prevWordID  = -1;

            for (int i = 0; i < cleanWords.size(); i++) {
                String word   = cleanWords.get(i);
                int    wordID = wordDAO.insertOrGetWord(word);

                wordDAO.incrementCount(wordID);

                if (i == 0) {
                    wordDAO.incrementStartCount(wordID);
                    firstWordID = wordID;
                }
                if (i == cleanWords.size() - 1) {
                    wordDAO.incrementEndCount(wordID);
                    lastWordID = wordID;
                }
                if (prevWordID != -1) {
                    nextWordDAO.insertOrUpdateNextWord(prevWordID, wordID);
                }

                prevWordID = wordID;
                totalWords++;
            }

            sentenceDAO.insertSentence(txtID, firstWordID, lastWordID, String.join(" ", cleanWords));
            totalSentences++;
        }

        // Update txt record with final counts
        txtFileDAO.updateTxtFile(txtID, totalWords, totalSentences);
        System.out.println("Inserted " + totalWords + " words across " + totalSentences + " sentences.");

        return txtID;
    }
}
