package src.main.java.com.group37.sentencebuilder.testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import src.main.java.com.group37.sentencebuilder.DAO.GeneratedSentenceDAO;

public class TestGeneratedSentenceDAO {

    // ---- DATABASE CONFIG: update password if needed ----
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/SentenceBuilder";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "epic"; // <-- change to your MySQL root password

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            GeneratedSentenceDAO dao = new GeneratedSentenceDAO(conn);

            // Insert some generated sentences
            int id1 = dao.insertGeneratedSentence("hello world", "random");
            int id2 = dao.insertGeneratedSentence("this is a test", "weighted");

            System.out.println("Inserted generated sentence IDs: " + id1 + ", " + id2);

            // Check for duplicates
            boolean exists1 = dao.sentenceExists("hello world");
            boolean exists2 = dao.sentenceExists("goodbye world");
            System.out.println("'hello world' exists? " + exists1);
            System.out.println("'goodbye world' exists? " + exists2);

            // Print all generated sentences
            System.out.println("All generated sentences:");
            List<String> allSentences = dao.getAllGeneratedSentences();
            for (String s : allSentences) {
                System.out.println(" - " + s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
