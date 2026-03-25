/*==============================================================
  File Name   : TxtFile.java
  Description : Handles database operations for txt table
                (tracks imported text files and metadata)

  Author      : Amrita Thapa
  Created On  : 2026-03-17

  Last Modified By : Amrita Thapa
  Last Modified On : 2026-03-17
  Change History   : 2026-03-17 - Implemented TxtFileDAO methods

  Database    : SentenceBuilder
==============================================================*/

package src.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TxtFileDAO {

    private Connection conn;

    public TxtFileDAO(Connection conn) {
        this.conn = conn;
    }


    // Insert a new text file record
    // Returns generated txtID
    public int insertTxtFile(String txtName, int importance,
                             int numWords, int numSentences) throws SQLException {

        String sql = "INSERT INTO txt (txtName, importance, numWords, numSentences) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, txtName);
            ps.setInt(2, importance);
            ps.setInt(3, numWords);
            ps.setInt(4, numSentences);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException("Failed to insert txt file record.");
    }


    // Get txt file name by ID
    public String getTxtNameByID(int txtID) throws SQLException {
        String sql = "SELECT txtName FROM txt WHERE txtID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, txtID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("txtName");
            }
        }

        return null;
    }


    // Get all txt files
    public List<String> getAllTxtFiles() throws SQLException {
        List<String> files = new ArrayList<>();
        String sql = "SELECT txtName FROM txt ORDER BY importedAt DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                files.add(rs.getString("txtName"));
            }
        }

        return files;
    }


    // Get file statistics
    public String getTxtStats(int txtID) throws SQLException {
        String sql = "SELECT numWords, numSentences, importedAt FROM txt WHERE txtID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, txtID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int words = rs.getInt("numWords");
                int sentences = rs.getInt("numSentences");
                Timestamp time = rs.getTimestamp("importedAt");

                return "Words: " + words +
                       ", Sentences: " + sentences +
                       ", Imported: " + time;
            }
        }

        return "No stats found.";
    }


    // Delete txt file record
    public void deleteTxtFile(int txtID) throws SQLException {
        String sql = "DELETE FROM txt WHERE txtID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, txtID);
            ps.executeUpdate();
        }
    }

    // Update word and sentence counts after import completes
    public void updateTxtFile(int txtID, int numWords, int numSentences) throws SQLException {
        String sql = "UPDATE txt SET numWords = ?, numSentences = ? WHERE txtID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numWords);
            ps.setInt(2, numSentences);
            ps.setInt(3, txtID);
            ps.executeUpdate();
        }
    }
}
