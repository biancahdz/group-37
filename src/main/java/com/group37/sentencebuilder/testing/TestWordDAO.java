package src.main.java.com.group37.sentencebuilder.testing;

import java.sql.*;

import src.main.java.com.group37.sentencebuilder.DAO.WordDAO;

public class TestWordDAO {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/SentenceBuilder";
        String user = "root";
        String pass = "epic"; // your MySQL password

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            WordDAO wordDAO = new WordDAO(conn);

            // Insert a word or get its ID
            int id = wordDAO.insertOrGetWord("hello");
            System.out.println("Word ID for 'hello': " + id);

            // Increment counts
            wordDAO.incrementCount(id);
            wordDAO.incrementStartCount(id);
            wordDAO.incrementEndCount(id);

            // Check start/end of sentence
            System.out.println("Can start sentence? " + wordDAO.canStartSentence(id));
            System.out.println("Can end sentence? " + wordDAO.canEndSentence(id));

            // Get word by ID
            System.out.println("Word for ID " + id + ": " + wordDAO.getWordByID(id));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}