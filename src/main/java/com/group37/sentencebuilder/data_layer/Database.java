/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    Database.java
 *  Author:  Cortland Kimzey, Amrita Thapa, Bianca Hernandez, Huy Nong
 *  Designed By: Cortland Kimzey
 *
 *  Description:
 *      Creates and maintains the connection to the database.
 *
 *  Version: 1.0
 *  Created: 2026-03-15
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Connect and disconnect to the database
 *      - execute queries on the database
 *      - Check if we can connect to the database
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.data_layer;

import com.group37.sentencebuilder.ui_layer.model.ImportHistoryRow;
import com.group37.sentencebuilder.ui_layer.model.ReportRow;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;

public class Database
{
    private static final String CONFIG_FILE = "data/db_config.txt";
    /** Single MySQL database used by the app (matches {@code SentenceBuilderDatabase.sql}). */
    public static final String DEFAULT_DATABASE_NAME = "SentenceBuilder";
    private static final String JDBC_HOST = "localhost";
    private static final int JDBC_PORT = 3306;

    private Connection conn = null;
    private String username = null;
    private String password = null;
    private String dbName = DEFAULT_DATABASE_NAME;

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @return result description
     */
    public String getUsername() { return this.username; }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @return result description
     */
    public String getPassword() { return this.password; }
    
    /**
     * Author: Amrita Thapa
     * Description: 
     *      Returns the active connection. Used by DefaultDataLoader to execute batch SQL.
     * 
     * @param input description
     * @return result description
     */
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public Database(String username, String password, String dbName) {
        this.username = username;
        this.password = password;
        this.dbName = dbName;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    private static String jdbcUrl(String databaseName) {
        return "jdbc:mysql://" + JDBC_HOST + ":" + JDBC_PORT + "/" + databaseName
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public boolean connect() {
        try {
            this.conn = DriverManager.getConnection(jdbcUrl(dbName), username, password);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public boolean disconnect() {
        if (this.conn != null) {
            try {
                this.conn.close();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public boolean isWord(String word) {
        String sql = "SELECT EXISTS (SELECT 1 FROM words WHERE word = ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, word);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public boolean addWord(String word) {
        String sql = "INSERT INTO words (word, wordCount) VALUES (?, 1) " +
                     "ON DUPLICATE KEY UPDATE wordCount = wordCount + 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, word);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      Inserts a list of words into the database in a single batch query.
     *      If a word already exists its count is incremented
     * 
     * @param words list of words to insert
     * @return true if successful, false otherwise
     */
    public boolean addWords(List<String> words)
    {
        if (words == null || words.isEmpty()) return false;

        StringBuilder sql = new StringBuilder(
            "INSERT INTO words (word, wordCount) VALUES "
        );

        for (int i = 0; i < words.size(); i++) {
            sql.append("(?, 1)");
            if (i < words.size() - 1) {
                sql.append(", ");
            }
        }

        sql.append(" ON DUPLICATE KEY UPDATE wordCount = wordCount + 1");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < words.size(); i++) {
                stmt.setString(i + 1, words.get(i));
            }

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public String getWord(int wordID) {
        String sql = "SELECT word FROM words WHERE wordID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, wordID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String word = rs.getString("word");
                    return word;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public Word getWordObj(int wordID)
    {
        String sql = "SELECT word wordCount FROM words WHERE wordID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, wordID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Word(wordID, rs.getString("word"), rs.getInt("wordCount"));
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public Integer getWordID(String word) {
        String sql = "SELECT wordID FROM words WHERE word = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, word);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("wordID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      Retrives word IDs for a list of words in a single query
     * 
     * @param words list of words to look up
     * @return map of word to its database ID 
     */
    public Map<String, Integer> getWordIDs(List<String> words)
    {
        Map<String, Integer> result = new HashMap<>();
        if (words == null || words.isEmpty()) return result;

        List<String> unique = words.stream().distinct().toList();

        StringBuilder sql = new StringBuilder(
            "SELECT word, wordID FROM words WHERE word IN ("
        );

        for (int i = 0; i < unique.size(); i++) {
            sql.append("?");
            if (i < unique.size() - 1) sql.append(", ");
        }
        sql.append(")");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < unique.size(); i++) {
                stmt.setString(i + 1, unique.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("word"), rs.getInt("wordID"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public Integer getWordCount(String word) {
        String sql = "SELECT wordCount FROM words WHERE word = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, word);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("wordCount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public Integer getWordCount(int wordID) {
        String sql = "SELECT wordCount FROM words WHERE wordID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, wordID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("wordCount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public boolean addSentence(int firstWordID, int lastWordID)
    {
        String sql = "INSERT INTO sentence (firstWordID, lastWordID, sentenceCount) VALUES (?, ?, 1) " +
                     "ON DUPLICATE KEY UPDATE sentenceCount = sentenceCount + 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, firstWordID);
            stmt.setInt(2, lastWordID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public boolean addCombo(int firstID, int nextID) {
        String sql = "INSERT INTO nextWord (wordID, nextWordID, comboCount) VALUES (?, ?, 1) " +
                     "ON DUPLICATE KEY UPDATE comboCount = comboCount + 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, firstID);
            stmt.setInt(2, nextID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Bianca Hernandez
     * Description: Inserts multiple adjacent word pairs into the database.
     *           in a single batch query. Duplicate pairs increment the combo count 
     *      
     * 
     * @param combos list of word ID pairs to insert
     * @return true if successful, false otherwise
     */
    public boolean addCombos(List<int[]> combos)
    {
        if (combos == null || combos.isEmpty()) return false;

        Map<String, Integer> counts = new HashMap<>();
        for (int[] pair : combos) {
            String key = pair[0] + "," + pair[1];
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }

        StringBuilder sql = new StringBuilder(
            "INSERT INTO nextWord (wordID, nextWordID, comboCount) VALUES "
        );

        int size = counts.size();
        int i = 0;
        for (int ignored = 0; ignored < size; ignored++) {
            sql.append("(?, ?, ?)");
            if (i++ < size - 1) sql.append(", ");
        }

        sql.append(" ON DUPLICATE KEY UPDATE comboCount = comboCount + VALUES(comboCount)");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                String[] parts = entry.getKey().split(",");
                stmt.setInt(index++, Integer.parseInt(parts[0]));
                stmt.setInt(index++, Integer.parseInt(parts[1]));
                stmt.setInt(index++, entry.getValue());
            }

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public Integer getComboCount(int firstID, int nextID) {
        String sql = "SELECT comboCount FROM nextWord WHERE wordID = ? AND nextWordID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, firstID);
            stmt.setInt(2, nextID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("comboCount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public Integer getBestCombo(int wordID, boolean first) {
        String sql = null;
        if(first)
            sql = "SELECT nextWordID FROM nextWord WHERE wordID = ? ORDER BY comboCount DESC Limit 1";
        else
            sql = "SELECT wordID FROM nextWord WHERE nextWordID = ? ORDER BY comboCount DESC Limit 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, wordID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if(first)
                        return rs.getInt("nextWordID");
                    else
                        return rs.getInt("wordID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public List<Integer> getXBest(int wordID, int x) {
        String sql = "SELECT nextWordID FROM nextWord WHERE wordID = ? ORDER BY comboCount DESC Limit ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, wordID);
            stmt.setInt(2, x);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Integer> results = new ArrayList<>();

                while (rs.next()) {
                    results.add(rs.getInt("nextWordID"));
                }

                return results;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public List<WordCombo> getXBestCombos(Word word, int x) {
        String sql = "SELECT nextWordID, comboCount FROM nextWord WHERE wordID = ? ORDER BY comboCount DESC Limit ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, word.getWordID());
            stmt.setInt(2, x);

            try (ResultSet rs = stmt.executeQuery()) {
                List<WordCombo> results = new ArrayList<>();

                while (rs.next()) {
                    results.add(new WordCombo(word, new Word(rs.getInt("nextWordID")), rs.getInt("comboCount")));
                }

                return results;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public List<String> getXBestWords(int wordID, int x) {
        String sql = 
            "SELECT w.word " +
            "FROM nextWord nw " +
            "JOIN words w ON nw.nextWordID = w.wordID " +
            "WHERE nw.wordID = ? " +
            "ORDER BY nw.comboCount DESC " +
            "LIMIT ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, wordID);
            stmt.setInt(2, x);

            try (ResultSet rs = stmt.executeQuery()) {
                List<String> results = new ArrayList<>();

                while (rs.next()) {
                    results.add(rs.getString("word"));
                }

                return results;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public List<String> autoComplete(String word, int x) {
        String sql =   "SELECT word FROM words WHERE word LIKE CONCAT(?, '%') ORDER BY wordCount DESC LIMIT ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, word);
            stmt.setInt(2, x);

            try (ResultSet rs = stmt.executeQuery()) {
                List<String> results = new ArrayList<>();

                while (rs.next()) {
                    results.add(rs.getString("word"));
                }

                return results;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *     Inserts a txt file record into the database by file name
     * 
     * @param FileName 
     * @return true if succesful, false otherwise
     */
    public boolean setTxt(String FileName) {
        String sql = "INSERT INTO txt (txtName) VALUES (?) ";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, FileName);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      Inserts a txt file record into the db with sentence and word counts
     * 
     * @param FileName
     * @param numSentences
     * @param numWords
     * @return true if succesful, false otherwise
     */
    public boolean setTxt(String FileName, int numSentences, int numWords) {
        String sql = "INSERT INTO txt (txtName, numSentences, numWords) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, FileName);
            stmt.setInt(2, numSentences);
            stmt.setInt(3, numWords);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public boolean setReport(String algorithm, String text) {
        String sql = "INSERT INTO reports (algorithmName, sentence) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, algorithm);
            stmt.setString(2, text);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      Creates a {@code txt} row at the start of import so per-file analytics can attach {@code txtID}.
     *      Call {@link #finishTxtImport(int, int, int)} after processing all sentences.
     * 
     * @param fileName the name of the file being imported
     * @return the generated txtID, or -1 if the insert failed
     */
    public int startTxtImport(String fileName) {
        if (!hasConnection(conn)) {
            return -1;
        }
        String sql = "INSERT INTO txt (txtName, numSentences, numWords) VALUES (?, 0, 0)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fileName);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      updates the txt row with final sentence and word counts after import is complete 
     * 
     * @param txtId the ID of the txt record to update
     * @param numSentences total number of sentences processed
     * @param numWords total number of words processed
     * @return true if succesful, false otherwise
     */
    public boolean finishTxtImport(int txtId, int numSentences, int numWords) {
        if (!hasConnection(conn) || txtId <= 0) {
            return false;
        }
        String sql = "UPDATE txt SET numSentences = ?, numWords = ? WHERE txtID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numSentences);
            stmt.setInt(2, numWords);
            stmt.setInt(3, txtId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      Adds token counts for one sentence into {@code txt_word} (after global {@link #addWords}).
     * 
     * @param txtId the id of the txt record
     * @param words list of words in the sentence 
     * @return true if succesful, else false
     */
    public boolean addTxtWordOccurrences(int txtId, List<String> words) {
        if (!hasConnection(conn) || txtId <= 0 || words == null || words.isEmpty()) {
            return true;
        }
        Map<String, Integer> wordIDs = getWordIDs(words);
        Map<Integer, Integer> delta = new HashMap<>();
        for (String w : words) {
            Integer id = wordIDs.get(w);
            if (id != null) {
                delta.merge(id, 1, Integer::sum);
            }
        }
        if (delta.isEmpty()) {
            return true;
        }
        String sql = "INSERT INTO txt_word (txtID, wordID, occurrenceCount) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE occurrenceCount = occurrenceCount + VALUES(occurrenceCount)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> e : delta.entrySet()) {
                stmt.setInt(1, txtId);
                stmt.setInt(2, e.getKey());
                stmt.setInt(3, e.getValue());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      Adds adjacent-word pair counts for one sentence into {@code txt_nextword}.
     * 
     * @param txtId the ID of the txt record
     * @param combos list of adjacent word ID pairs
     * @return true if succesful, else false 
     */
    public boolean addTxtCombosForTxt(int txtId, List<int[]> combos) {
        if (!hasConnection(conn) || txtId <= 0 || combos == null || combos.isEmpty()) {
            return true;
        }
        Map<String, Integer> counts = new HashMap<>();
        for (int[] pair : combos) {
            String key = pair[0] + "," + pair[1];
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }

        StringBuilder sql = new StringBuilder(
                "INSERT INTO txt_nextword (txtID, wordID, nextWordID, comboCount) VALUES ");
        int size = counts.size();
        int i = 0;
        for (int ignored = 0; ignored < size; ignored++) {
            sql.append("(?, ?, ?, ?)");
            if (i++ < size - 1) {
                sql.append(", ");
            }
        }
        sql.append(" ON DUPLICATE KEY UPDATE comboCount = comboCount + VALUES(comboCount)");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                String[] parts = entry.getKey().split(",");
                stmt.setInt(index++, txtId);
                stmt.setInt(index++, Integer.parseInt(parts[0]));
                stmt.setInt(index++, Integer.parseInt(parts[1]));
                stmt.setInt(index++, entry.getValue());
            }
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public int getTxtCount() {
        if (conn == null) {
            return 0;
        }
        String sql = "SELECT COUNT(txtID) AS count FROM txt";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      returns the total number across all imported txt files
     * 
     * 
     * @return total sentence count, or 0 if not connected
     */
    public int getTxtSentenceCount() {
        if (conn == null) {
            return 0;
        }
        String sql = "SELECT SUM(numSentences) AS total FROM txt";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      Retrives the full import history as an observable list for display in UI
     * 
     * 
     * @return ObservableList of ImportHistoryRow objects, or null if query fails 
     */
    public ObservableList<ImportHistoryRow> getTxtHistory()
    {
        String sql = "SELECT txtID, txtName, numSentences, numWords, dateAdded FROM txt";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                ObservableList<ImportHistoryRow> rows = FXCollections.observableArrayList();

                while (rs.next()) {
                    rows.add(new ImportHistoryRow(rs.getString("txtName"), rs.getString("dateAdded"), rs.getInt("numSentences"), rs.getInt("numWords"), "Complete"));
                }

                return rows;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public ObservableList<ReportRow> getReports()
    {
        String sql = "SELECT reportID, algorithmName, dateAdded, sentence FROM reports";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                ObservableList<ReportRow> rows = FXCollections.observableArrayList();

                while (rs.next()) {
                    rows.add(new ReportRow(String.valueOf(rs.getInt("reportID")), rs.getString("algorithmName"), rs.getString("dateAdded"), rs.getString("sentence")));
                }

                return rows;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- Word analytics (corpus stats UI) ---------------------------------

    public record TxtFileSummary(int txtID, String txtName, int numSentences, int numWords, java.sql.Timestamp importedAt) {}

    public record TopWordEntry(String word, int count) {}

    public record TopBigramEntry(String firstWord, String secondWord, int count) {}

    public record CorpusAggregate(long uniqueWordTypes, long totalTokens, long topBigramCount) {}

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    private static boolean hasConnection(Connection c) {
        try {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Author: Huy Nong
     * Description: 
     *      Imported files for scope dropdown and per-file table, newest first.
     * 
     * @param input description
     * @return result description
     */
    public List<TxtFileSummary> listTxtFileSummaries() {
        List<TxtFileSummary> out = new ArrayList<>();
        if (!hasConnection(conn)) {
            return out;
        }
        String sql = "SELECT txtID, txtName, numSentences, numWords, dateAdded FROM txt ORDER BY dateAdded DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int s = rs.getInt("numSentences");
                if (rs.wasNull()) {
                    s = 0;
                }
                int w = rs.getInt("numWords");
                if (rs.wasNull()) {
                    w = 0;
                }
                out.add(new TxtFileSummary(
                        rs.getInt("txtID"),
                        rs.getString("txtName"),
                        s,
                        w,
                        rs.getTimestamp("dateAdded")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Author: Huy Nong
     * Description: 
     *      Most frequent word types (excluding generator sentinel tokens).
     * 
     * @param input description
     * @return result description
     */
    public List<TopWordEntry> fetchTopWords(int limit) {
        List<TopWordEntry> out = new ArrayList<>();
        if (!hasConnection(conn) || limit <= 0) {
            return out;
        }
        String sql = "SELECT word, wordCount FROM words WHERE word NOT IN ('<START>', '<END>') "
                + "ORDER BY wordCount DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    out.add(new TopWordEntry(rs.getString("word"), rs.getInt("wordCount")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Author: Huy Nong
     * Description: 
     *      Strongest next-word transitions (excluding pairs involving sentinel tokens).
     * 
     * @param input description
     * @return result description
     */
    public List<TopBigramEntry> fetchTopBigrams(int limit) {
        List<TopBigramEntry> out = new ArrayList<>();
        if (!hasConnection(conn) || limit <= 0) {
            return out;
        }
        String sql = "SELECT w1.word AS w1, w2.word AS w2, nw.comboCount AS cc "
                + "FROM nextWord nw "
                + "JOIN words w1 ON nw.wordID = w1.wordID "
                + "JOIN words w2 ON nw.nextWordID = w2.wordID "
                + "WHERE w1.word NOT IN ('<START>', '<END>') AND w2.word NOT IN ('<START>', '<END>') "
                + "ORDER BY nw.comboCount DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    out.add(new TopBigramEntry(rs.getString("w1"), rs.getString("w2"), rs.getInt("cc")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Author: Huy Nong
     * Description: 
     *      Corpus-wide headline metrics for the analytics chips.
     * 
     * @param input description
     * @return result description
     */
    public CorpusAggregate fetchCorpusAggregate() {
        if (!hasConnection(conn)) {
            return new CorpusAggregate(0, 0, 0);
        }
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM words WHERE word NOT IN ('<START>', '<END>')) AS u, "
                + "(SELECT COALESCE(SUM(wordCount), 0) FROM words WHERE word NOT IN ('<START>', '<END>')) AS t, "
                + "(SELECT COALESCE(MAX(nw.comboCount), 0) FROM nextWord nw "
                + "JOIN words w1 ON nw.wordID = w1.wordID "
                + "JOIN words w2 ON nw.nextWordID = w2.wordID "
                + "WHERE w1.word NOT IN ('<START>', '<END>') AND w2.word NOT IN ('<START>', '<END>')) AS m";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new CorpusAggregate(rs.getLong("u"), rs.getLong("t"), rs.getLong("m"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new CorpusAggregate(0, 0, 0);
    }

    /**
     * Author: Huy Nong
     * Description: 
     *      Per-file top words (requires {@code txt_word}; populated on import).
     * 
     * @param input description
     * @return result description
     */
    public List<TopWordEntry> fetchTopWordsForTxt(int txtId, int limit) {
        List<TopWordEntry> out = new ArrayList<>();
        if (!hasConnection(conn) || txtId <= 0 || limit <= 0) {
            return out;
        }
        String sql = "SELECT w.word, tw.occurrenceCount AS cc FROM txt_word tw "
                + "JOIN words w ON tw.wordID = w.wordID "
                + "WHERE tw.txtID = ? AND w.word NOT IN ('<START>', '<END>') "
                + "ORDER BY tw.occurrenceCount DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, txtId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    out.add(new TopWordEntry(rs.getString("word"), rs.getInt("cc")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Author: Huy Nong
     * Description: 
     *      Per-file top bigrams (requires {@code txt_nextword}; populated on import).
     * 
     * @param input description
     * @return result description
     */
    public List<TopBigramEntry> fetchTopBigramsForTxt(int txtId, int limit) {
        List<TopBigramEntry> out = new ArrayList<>();
        if (!hasConnection(conn) || txtId <= 0 || limit <= 0) {
            return out;
        }
        String sql = "SELECT w1.word AS w1, w2.word AS w2, tn.comboCount AS cc FROM txt_nextword tn "
                + "JOIN words w1 ON tn.wordID = w1.wordID "
                + "JOIN words w2 ON tn.nextWordID = w2.wordID "
                + "WHERE tn.txtID = ? AND w1.word NOT IN ('<START>', '<END>') "
                + "AND w2.word NOT IN ('<START>', '<END>') "
                + "ORDER BY tn.comboCount DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, txtId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    out.add(new TopBigramEntry(rs.getString("w1"), rs.getString("w2"), rs.getInt("cc")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Author: Huy Nong
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public CorpusAggregate fetchCorpusAggregateForTxt(int txtId) {
        if (!hasConnection(conn) || txtId <= 0) {
            return new CorpusAggregate(0, 0, 0);
        }
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM txt_word tw JOIN words w ON tw.wordID = w.wordID "
                + "WHERE tw.txtID = ? AND w.word NOT IN ('<START>', '<END>')) AS u, "
                + "(SELECT COALESCE(SUM(tw.occurrenceCount), 0) FROM txt_word tw JOIN words w ON tw.wordID = w.wordID "
                + "WHERE tw.txtID = ? AND w.word NOT IN ('<START>', '<END>')) AS t, "
                + "(SELECT COALESCE(MAX(tn.comboCount), 0) FROM txt_nextword tn "
                + "JOIN words w1 ON tn.wordID = w1.wordID JOIN words w2 ON tn.nextWordID = w2.wordID "
                + "WHERE tn.txtID = ? AND w1.word NOT IN ('<START>', '<END>') "
                + "AND w2.word NOT IN ('<START>', '<END>')) AS m";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, txtId);
            stmt.setInt(2, txtId);
            stmt.setInt(3, txtId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CorpusAggregate(rs.getLong("u"), rs.getLong("t"), rs.getLong("m"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new CorpusAggregate(0, 0, 0);
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public void dumpDatabase() throws Exception
    {
        String[] command = {
            "mysqldump",
            "-u", username,
            "-p" + password,
            dbName
        };

        Process process = Runtime.getRuntime().exec(command);
        
        try (InputStream is = process.getInputStream();
            FileOutputStream fos = new FileOutputStream("data/SQLDatabase.sql")) {

            byte[] buffer = new byte[4096];
            int read;

            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        }

        try (InputStream es = process.getErrorStream()) {
            byte[] errBuffer = es.readAllBytes();
            if (errBuffer.length > 0) {
                System.err.println("mysqldump error: " + new String(errBuffer));
            }
        }

        process.waitFor();
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public static Database getDatabase() {
        String user = "root";
        String pass = "hold";

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("data/db_config.txt")))) {
            user = reader.readLine();
            pass = reader.readLine();

        } catch (IOException e) {
            //e.printStackTrace();
        }
        return new Database(user, pass, DEFAULT_DATABASE_NAME);
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public static boolean canConnect()
    {
        String user;
        String pass;

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("data/db_config.txt")))) {
            user = reader.readLine();
            pass = reader.readLine();
        } catch (IOException e) {
            return false;
        }

        try (Connection connection = DriverManager.getConnection(jdbcUrl(DEFAULT_DATABASE_NAME), user, pass)) {
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    public static boolean canConfigConnect(String user, String pass)
    {
        try (Connection connection = DriverManager.getConnection(jdbcUrl(DEFAULT_DATABASE_NAME), user, pass)) {
            connection.close();
        } catch (SQLException e) {
            return false;
        }

        // Persisting credentials is best-effort; successful DB auth should still allow login.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            writer.write(user);
            writer.newLine();
            writer.write(pass);
        } catch (IOException ignored) {
        }

        return true;
    }
}
