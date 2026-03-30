/**
 * File: Database.java
 * Description: Creates and maintains the connection to the database.
 *
 * Author: 
 * Created: 2026-03-15
 * Last Modified: 2026-03-16
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.data_layer;

import com.group37.sentencebuilder.data_layer.SentenceData;
import com.group37.sentencebuilder.data_layer.TxtData;
import com.group37.sentencebuilder.data_layer.Word;
import com.group37.sentencebuilder.data_layer.WordCombo;

import com.group37.sentencebuilder.ui_layer.model.ImportHistoryRow;

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

import java.io.IOException;
import java.util.Objects;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;

public class Database
{
    private static final String CONFIG_FILE = "data/db_config.txt";
    private static final String URL = "jdbc:mysql://localhost:3306/SentenceBuilder?useSSL=false&serverTimezone=UTC";
    private Connection conn = null;
    private String username = null;
    private String password = null;
    private String dbName = "SentenceBuilder";

    public Database(String username, String password, String dbName) {
        this.username = username;
        this.password = password;
        this.dbName = dbName;
    }

    public boolean connect() {
        try {
            this.conn = DriverManager.getConnection(URL, username, password);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

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

    public SentenceData getSentence(int sentenceID)
    {
        String sql = "SELECT firstWordID lastWordID sentenceCount FROM sentence WHERE sentenceID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sentenceID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new SentenceData(
                        rs.getInt("firstWordID"),
                        rs.getInt("lastWordID"),
                        rs.getInt("sentenceCount")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public int getTxtCount() {
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

    public int getTxtSentenceCount() {
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

    public TxtData getTxt(String FileName) {
        String sql = "SELECT txtID numSentences FROM txt WHERE txtName = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, FileName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TxtData(
                        rs.getInt("txtID"),
                        FileName,
                        rs.getInt("numSentences")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public static Database getDatabase() {
        String user = "root";
        String pass = "hold";

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("data/db_config.txt")))) {
            user = reader.readLine();
            pass = reader.readLine();

        } catch (IOException e) {
            //e.printStackTrace();
        }
        return new Database(user, pass, "SentenceBuilder");
    }

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

        try (Connection connection = DriverManager.getConnection(URL, user, pass)) {
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean canConfigConnect(String user, String pass)
    {
        try (Connection connection = DriverManager.getConnection(URL, user, pass)) {
            connection.close();
        } catch (SQLException e) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            writer.write(user);
            writer.newLine();
            writer.write(pass);
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}