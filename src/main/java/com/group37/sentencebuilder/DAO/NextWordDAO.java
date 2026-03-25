/*==============================================================
  File Name   : NextWordDAO.java
  Description : Data Access Object for managing word-to-next-word relationships
              and retrieving weighted random next words for sentence generation.

  Author      : Amrita Thapa
  Created On  : 2026-03-14

  Last Modified By : Amrita Thapa
  Last Modified On : 2026-03-17
  Change History   : 2026-03-14  - Implemented WordDAO methods
                     2026-03-17  - Updated comments

  Database    : SentenceBuilder
==============================================================*/
package src.main.java.com.group37.sentencebuilder.DAO;

import java.sql.*;
import java.util.*;

public class NextWordDAO {

    private Connection conn;

    public NextWordDAO(Connection conn) {
        this.conn = conn;
    }

    // Insert or update word relationship
    public void insertOrUpdateNextWord(int wordID, int nextWordID) throws SQLException {

        String sql =
        "INSERT INTO nextWord (wordID, nextWordID, count) " +
        "VALUES (?, ?, 1) " +
        "ON DUPLICATE KEY UPDATE count = count + 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, wordID);
            ps.setInt(2, nextWordID);

            ps.executeUpdate();
        }
    }

    // Get all possible next words
    public Map<Integer, Integer> getNextWords(int wordID) throws SQLException {

        Map<Integer, Integer> nextWords = new HashMap<>();

        String sql =
        "SELECT nextWordID, count FROM nextWord WHERE wordID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, wordID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int nextWordID = rs.getInt("nextWordID");
                int count = rs.getInt("count");

                nextWords.put(nextWordID, count);
            }
        }

        return nextWords;
    }

    // Pick a weighted random next word
    public int getRandomNextWord(int wordID) throws SQLException {

        Map<Integer, Integer> nextWords = getNextWords(wordID);

        if (nextWords.isEmpty())
            return -1;

        int total = 0;

        for (int count : nextWords.values()) {
            total += count;
        }

        int random = new Random().nextInt(total);

        int cumulative = 0;

        for (Map.Entry<Integer, Integer> entry : nextWords.entrySet()) {

            cumulative += entry.getValue();

            if (random < cumulative) {
                return entry.getKey();
            }
        }

        return -1;
    }
}
