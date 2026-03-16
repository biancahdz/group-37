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

package data_layer;

import data_layer.SentenceData;
import data_layer.TxtData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database
{

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
            //System.out.println("Connected successfully!");
            return true;
        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public boolean disconnect() {
        if (this.conn != null) {
            try {
                this.conn.close();
                //System.out.println("Disconnected successfully!");
                return true;
            } catch (SQLException e) {
                //e.printStackTrace();
                return false;
            }
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

    public static Database getDatabase() {
        return new Database("root", "your password", "SentenceBuilder");
    }
}