/*==============================================================
  File Name   : SentenceDAO.java
  Description : Handles database operations for the sentence
                table in the SentenceBuilder database

  Author      : Amrita Thapa
  Created On  : 2026-03-17

  Last Modified By : Amrita Thapa
  Last Modified On : 2026-03-17
  Change History   : 2026-03-17 - Implemented SentenceDAO methods

  Database    : SentenceBuilder
==============================================================*/
package src.main.java.com.group37.sentencebuilder.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SentenceDAO {

    private Connection conn;

    public SentenceDAO(Connection conn) {
        this.conn = conn;
    }

    // Insert a sentence and return its ID
    public int insertSentence(int txtID, int firstWordID, int lastWordID, String fullText) throws SQLException {
        String sql = "INSERT INTO sentence (txtID, firstWordID, lastWordID, full_text) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, txtID);
            ps.setInt(2, firstWordID);
            ps.setInt(3, lastWordID);
            ps.setString(4, fullText);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException("Failed to insert sentence");
    }

    // Check if sentence already exists (for duplicate detection)
    public boolean sentenceExists(String text) throws SQLException {
        String sql = "SELECT 1 FROM sentence WHERE full_text = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, text);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // Get all sentences from a txt file
    public List<String> getSentencesByTxtID(int txtID) throws SQLException {
        List<String> sentences = new ArrayList<>();
        String sql = "SELECT full_text FROM sentence WHERE txtID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, txtID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sentences.add(rs.getString("full_text"));
            }
        }

        return sentences;
    }
}
