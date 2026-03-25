package src.main.java.com.group37.sentencebuilder.testing;

import java.sql.*;

import src.main.java.com.group37.sentencebuilder.DAO.*;

public class TestSentenceDAO {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/SentenceBuilder";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "epic";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            SentenceDAO sentenceDAO = new SentenceDAO(conn);

            // Insert a sentence
            int txtID = 1;       // existing txt record
            int firstWordID = 1; // existing word
            int lastWordID = 2;  // existing word
            String text = "hello world";

            int sentenceID = sentenceDAO.insertSentence(txtID, firstWordID, lastWordID, text);
            System.out.println("Inserted sentence ID: " + sentenceID);

            // Check duplicate
            boolean exists = sentenceDAO.sentenceExists(text);
            System.out.println("Sentence exists? " + exists);

            // Get sentences for file
            System.out.println("Sentences in file:");
            for (String s : sentenceDAO.getSentencesByTxtID(txtID)) {
                System.out.println(" - " + s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
