/*==============================================================
  File Name   : WordDAO.java
  Description : Data Access Object for the word table.
                Provides methods to:
                    - Insert a new word if it dosen't exist
                    - Increment total, start, and end counts
                    - Retrieve words by ID or prefix
                    - Check if a word can start or end a sentence

  Author      : Amrita Thapa
  Created On  : 2026-03-13

  Last Modified By : Amrita Thapa
  Last Modified On : 2026-03-13
  Change History   : 2026-03-13  - Implemented WordDAO methods

  Database    : SentenceBuilder
==============================================================*/

package src.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WordDAO {

    private Connection conn;

    // Constructor requires an active DB connection
    public WordDAO(Connection conn) {
        this.conn = conn;
    }

    // Insert a word if it doesn't exist, then return its ID
    public int insertOrGetWord(String word) throws SQLException {
        // Insert new word with counts initialized to 0
        String insertSQL = "INSERT IGNORE INTO word (word, count, startCount, endCount) VALUES (?, 0, 0, 0)";
        try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
            ps.setString(1, word);
            ps.executeUpdate();
        }

        // Get the wordID
        String selectSQL = "SELECT wordID FROM word WHERE word = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
            ps.setString(1, word);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("wordID");
            }
        }
        throw new SQLException("Failed to insert or retrieve word: " + word);
    }

    // Increment the total occurrence count of a word
    public void incrementCount(int wordID) throws SQLException {
        String sql = "UPDATE word SET count = count + 1 WHERE wordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ps.executeUpdate();
        }
    }

    // Increment the start-of-sentence count
    public void incrementStartCount(int wordID) throws SQLException {
        String sql = "UPDATE word SET startCount = startCount + 1 WHERE wordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ps.executeUpdate();
        }
    }

    // Increment the end-of-sentence count
    public void incrementEndCount(int wordID) throws SQLException {
        String sql = "UPDATE word SET endCount = endCount + 1 WHERE wordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ps.executeUpdate();
        }
    }

    // Retrieve a word by ID
    public String getWordByID(int wordID) throws SQLException {
        String sql = "SELECT word FROM word WHERE wordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("word");
            }
        }
        return null;
    }

    // Retrieve all words starting with a certain prefix (for auto-complete)
    public List<String> getWordsByPrefix(String prefix) throws SQLException {
        List<String> words = new ArrayList<>();
        String sql = "SELECT word FROM word WHERE word LIKE ? LIMIT 50";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                words.add(rs.getString("word"));
            }
        }
        return words;
    }

    // Check if a word can start a sentence
    public boolean canStartSentence(int wordID) throws SQLException {
        String sql = "SELECT startCount FROM word WHERE wordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("startCount") > 0;
            }
        }
        return false;
    }

    // Check if a word can end a sentence
    public boolean canEndSentence(int wordID) throws SQLException {
        String sql = "SELECT endCount FROM word WHERE wordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("endCount") > 0;
            }
        }
        return false;
    }
}

