import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/*==============================================================*/
/*  File Name   : TextFileImporter.java                         */
/*  Description : Reads a .txt file, parses it into sentences   */
/*                and words, and inserts everything into the     */
/*                SentenceBuilder MySQL database.               */
/*                                                              */
/*  HOW TO USE:                                                 */
/*  1. Make sure MySQL is running                               */
/*  2. Update DB_PASSWORD below with your MySQL root password   */
/*  3. Run the program                                          */
/*  4. When prompted, enter the full path to your .txt file     */
/*     Example: C:\Users\YourName\Desktop\pride_and_prejudice.txt*/
/*==============================================================*/

public class TextFileImporter {

    // ---- DATABASE CONFIG — update password to your MySQL root password ----
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/SentenceBuilder";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password"; // <-- change this

    public static void main(String[] args) throws Exception {

        // Ask user for the file path
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the full path to your .txt file: ");
        String filePath = scanner.nextLine().trim();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        System.out.println("Connecting to database...");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("Connected! Starting import of: " + file.getName());
            importFile(file, conn);
            System.out.println("Done! Import complete.");
        }
    }

    static void importFile(File file, Connection conn) throws Exception {
        String content = new String(Files.readAllBytes(file.toPath()));
        String[] rawSentences = content.split("[.!?]+");

        int totalWords     = 0;
        int totalSentences = 0;

        System.out.println("Total sentences found: " + rawSentences.length);

        // Insert txt record FIRST so we have a valid txtID
        int txtID = insertTxtRecord(file.getName(), conn);
        System.out.println("Created txt record with ID: " + txtID);

        for (int s = 0; s < rawSentences.length; s++) {
            if (s % 500 == 0) {
                System.out.println("Progress: " + s + " / " + rawSentences.length + " sentences processed...");
            }

            String raw = rawSentences[s].trim();
            if (raw.isEmpty()) continue;

            String[] tokens = raw.split("\\s+");
            List<String> cleanWords = new ArrayList<>();
            for (String token : tokens) {
                String clean = token.replaceAll("[^a-zA-Z'-]", "").toLowerCase().trim();
                if (!clean.isEmpty() && !clean.equals("-") && !clean.equals("'")) {
                    cleanWords.add(clean);
                }
            }

            if (cleanWords.size() < 2) continue;

            int firstWordID = -1;
            int lastWordID  = -1;
            int prevWordID  = -1;

            for (int i = 0; i < cleanWords.size(); i++) {
                String word   = cleanWords.get(i);
                int    wordID = insertOrGetWord(word, conn);

                incrementCount(wordID, conn);

                if (i == 0) {
                    incrementStartCount(wordID, conn);
                    firstWordID = wordID;
                }
                if (i == cleanWords.size() - 1) {
                    incrementEndCount(wordID, conn);
                    lastWordID = wordID;
                }
                if (prevWordID != -1) {
                    insertOrIncrementNextWord(prevWordID, wordID, conn);
                }

                prevWordID = wordID;
                totalWords++;
            }

            insertSentence(txtID, firstWordID, lastWordID, String.join(" ", cleanWords), conn);
            totalSentences++;
        }

        // Update txt record with final counts
        updateTxtRecord(txtID, totalWords, totalSentences, conn);
        System.out.println("Inserted " + totalWords + " words across " + totalSentences + " sentences.");
    }

    // Insert word if it doesn't exist, then return its ID
    static int insertOrGetWord(String word, Connection conn) throws SQLException {
        // Try to insert — if duplicate, ignore
        String insert = "INSERT IGNORE INTO word (word, count, startCount, endCount) VALUES (?, 0, 0, 0)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, word);
            ps.executeUpdate();
        }
        // Now get the ID
        String select = "SELECT wordID FROM word WHERE word = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, word);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("wordID");
        }
        throw new SQLException("Could not find or insert word: " + word);
    }

    static void incrementCount(int wordID, Connection conn) throws SQLException {
        String sql = "UPDATE word SET count = count + 1 WHERE wordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ps.executeUpdate();
        }
    }

    static void incrementStartCount(int wordID, Connection conn) throws SQLException {
        String sql = "UPDATE word SET startCount = startCount + 1 WHERE wordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ps.executeUpdate();
        }
    }

    static void incrementEndCount(int wordID, Connection conn) throws SQLException {
        String sql = "UPDATE word SET endCount = endCount + 1 WHERE wordID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ps.executeUpdate();
        }
    }

    static void insertOrIncrementNextWord(int wordID, int nextWordID, Connection conn) throws SQLException {
        String sql = "INSERT INTO nextWord (wordID, nextWordID, count) VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE count = count + 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wordID);
            ps.setInt(2, nextWordID);
            ps.executeUpdate();
        }
    }

    static void insertSentence(int txtID, int firstWordID, int lastWordID, String fullText, Connection conn) throws SQLException {
        String sql = "INSERT INTO sentence (txtID, firstWordID, lastWordID, full_text) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, txtID);
            ps.setInt(2, firstWordID);
            ps.setInt(3, lastWordID);
            ps.setString(4, fullText);
            ps.executeUpdate();
        }
    }
    static int insertTxtRecord(String fileName, Connection conn) throws SQLException {
        String sql = "INSERT INTO txt (txtName, numWords, numSentences) VALUES (?, 0, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fileName);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        throw new SQLException("Failed to insert txt record");
    }
    static void updateTxtRecord(int txtID, int numWords, int numSentences, Connection conn) throws SQLException {
        String sql = "UPDATE txt SET numWords = ?, numSentences = ? WHERE txtID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numWords);
            ps.setInt(2, numSentences);
            ps.setInt(3, txtID);
            ps.executeUpdate();
        }
    }

}
