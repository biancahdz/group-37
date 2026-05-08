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
     *      Returns the username credetial stored in this Database instance.
     * 
     * @return the username credential stored in this Database instance
     */
    public String getUsername() { return this.username; }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      Returns the password credential stored in this Database instance.
     * 
     * @return the password credential stored in this Database instance
     */
    public String getPassword() { return this.password; }
    
    /**
     * Author: Amrita Thapa
     * Description: 
     *      Returns the active connection. Used by DefaultDataLoader to execute batch SQL.
     * 
     * @return the active JDBC Connection object, or null if not connected
     */
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      Constructus a Database instance with the given credential and target database name
     * 
     * @param username the MySQL username
     * @param password the MySQL password
     * @return dbName the name of the database to connect to
     */
    public Database(String username, String password, String dbName) {
        this.username = username;
        this.password = password;
        this.dbName = dbName;
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      Builds the JDBC connection URL for the given database name using the configured host and port.
     * 
     * @param databaseName the name of the database to include in the URL
     * @return the full JDBC connection URL string
     */
    private static String jdbcUrl(String databaseName) {
        return "jdbc:mysql://" + JDBC_HOST + ":" + JDBC_PORT + "/" + databaseName
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    }

    /**
     * Author: Amrita Thapa
     * Description: 
     *      Opens a JDBC connection to the database and returns true if successful.
     * 
     * @return true if the connection was established successfully, false otherwise
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
     *      Closes the active JDBC connection and returns true if successful
     * 
     * @return true if the connection was closed successfully, false otherwise
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
     *      Returns true if the given word exists in the word table
     * 
     * @param word the word string to look up
     * @return true if the word exists in the words table, false otherwise
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
     *      Inserts a word into the words table or increments its count if it already exists.
     * 
     * @param word the word string to insert or increment
     * @return true if the operation was successful, false otherwise
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
     *      Returns the word string associated with the given word ID
     * 
     * @param wordID the ID of the word to retrieve
     * @return the word string, or null if not found
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
     *      Returns a Word object containing the ID, text, and count for the given word ID.
     * 
     * @param wordID the ID of the word to retrieve
     * @return a Word object containing the ID, text, and count, or null if not found
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
     *      Returns the ID of the given word from the words table.
     * 
     * @param word the word string to look up
     * @return the integer ID of the word, or null if not found
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
     *      Returns how many times the given word appears across all imported text files.
     * 
     * @param word the word string to look up
     * @return the occurrence count of the word, or null if not found
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
     *      Returns the occurrent count for the word with the given ID.
     * 
     * @param wordID the ID of the word to look up
     * @return the occurrence count of the word, or null if not found
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
     *      Records a sentence by its first and last word IDs, or increments the count if it already exists. 
     * 
     * @param firstWordID the ID of the first word in the sentence
     * @param lastWordID the ID of the last word in the sentence
     * @return true if the operation was successful, false otherwise
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
     *      Records that one word was followed by another, or increments the combo count if the pair already exists.
     * 
     * @param firstID the ID of the first word in the pair
     * @param nextID the ID of the word that follows
     * @return true if the operation was successful, false otherwise
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
     *      Returns how many times the second word has followed the first word.
     * 
     * @param firstID the ID of the first word in the pair
     * @param nextID the ID of the following word
     * @return the number of times the pair has occurred, or null if not found
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
     *      Returns the most frequently occuring next or previous word ID for the given word.
     * 
     * @param wordID the ID of the word to look up
     * @param first if true, returns the most frequent next word; if false, returns the most frequent previous word
     * @return the ID of the most frequent next or previous word, or null if not found
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
     *      Gets the best x word IDs from the database where the first word ID is wordID
     * 
     * @param wordID the ID of the first word in the combo
     * @param x the number of top combos to grab
     * @return a List of all the x best word ids
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
     *      Gets the best x word combinations from the database where the first word is word
     * 
     * @param word word object containing the first word in the combo
     * @param x the number of top combos to grab
     * @return A list of the top word combinations
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
     *      Gets the x best words from the database given the word ID.
     *      The best combinations of the word given the count. Gets the
     *      actual string of the word.
     * 
     * @param wordID the id of the first word in the combo
     * @param x the number of word combos to grab
     * @return A list of word strings
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
     *      Takes a string and checks the word table for any word that begins with that string.
     *      grabs the top x possible word by there count in the database.
     * 
     * @param word a string for a possible word
     * @param x the number of words to grab from the database
     * @return A list of possible words
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
     *      Adds a report to the report table, this stores the sentence generated and the
     *      algorithm used to generate it
     * 
     * @param algorithm the algorithm used
     * @param text the sentence that was generated
     * @return true if it was inserted correctly, otherwise false
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
     *      Returns the total number of text files that have been imported into the database
     * 
     * @return the total number of imported text files, or 0 if not connected
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
     *      Grabs all data in the reports table to be displayed as a list
     * 
     * @return a ObservableList of all the report table rows
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
     * Author: Huy Nong
     * Description: 
     *      Checks if a connection exists
     * 
     * @param c a database connection
     * @return true if the connection exists, false otherwise
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
     *      Creates a database object using the stored credentials
     * 
     * @return the created database object
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
     *      Checks if stored credential are valid and can create a connection
     * 
     * @return true if we can connect, false if we can't
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
     *      Checks if a connection can be made to the database using given credetials
     * 
     * @param user the username of the account
     * @param pass the password of the account
     * @return true if we can connect false if we can't
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
