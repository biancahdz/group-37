/*==============================================================
  File Name   : GeneratedSentenceDAO.java                 
  Description : Data Access Object for managing generated sentences, 
                including inserting new sentences, checking for duplicates, 
                and retrieving stored sentences with metadata.
                                                              
  Author      : Amrita Thapa                                  
  Created On  : 2026-03-17                                    
                                                              
  Last Modified By : Amrita Thapa                             
  Last Modified On : 2026-03-17                               
  Change History   : 2026-03-17  - Created DAO class and implemented 
                                   insert, existence check, and retrieval methods                  
                                                              
  Database    : SentenceBuilder                               
==============================================================*/
package src.sentencegen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneratedSentenceDAO {

    private Connection conn;

    public GeneratedSentenceDAO(Connection conn) {
        this.conn = conn;
    }

    // Insert a new generated sentence and return the genID
    public int insertGeneratedSentence(String sentence, String algorithm) throws SQLException {
        String sql = "INSERT INTO generatedSentence (full_text, algorithm) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sentence);
            ps.setString(2, algorithm);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        throw new SQLException("Failed to insert generated sentence");
    }

    // Check if a sentence already exists
    public boolean sentenceExists(String sentence) throws SQLException {
        String sql = "SELECT genID FROM generatedSentence WHERE full_text = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sentence);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // Get all generated sentences
    public List<String> getAllGeneratedSentences() throws SQLException {
        List<String> sentences = new ArrayList<>();
        String sql = "SELECT full_text, algorithm, generatedAt FROM generatedSentence ORDER BY generatedAt DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String sentence = rs.getString("full_text");
                String algorithm = rs.getString("algorithm");
                Timestamp ts = rs.getTimestamp("generatedAt");
                sentences.add(sentence + " (" + algorithm + " at " + ts + ")");
            }
        }
        return sentences;
    }
}
