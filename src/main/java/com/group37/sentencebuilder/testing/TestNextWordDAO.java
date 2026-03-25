package src.main.java.com.group37.sentencebuilder.testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import src.main.java.com.group37.sentencebuilder.DAO.NextWordDAO;
import src.main.java.com.group37.sentencebuilder.DAO.WordDAO;

public class TestNextWordDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/SentenceBuilder";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "epic";

    public static void main(String[] args) {

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            WordDAO wordDAO = new WordDAO(conn);
            NextWordDAO nextWordDAO = new NextWordDAO(conn);

            // Insert test words
            int helloID = wordDAO.insertOrGetWord("hello");
            int worldID = wordDAO.insertOrGetWord("world");
            int thereID = wordDAO.insertOrGetWord("there");

            // Record relationships
            nextWordDAO.insertOrUpdateNextWord(helloID, worldID);
            nextWordDAO.insertOrUpdateNextWord(helloID, worldID);
            nextWordDAO.insertOrUpdateNextWord(helloID, thereID);

            System.out.println("Inserted relationships:");

            // Get possible next words
            Map<Integer, Integer> nextWords = nextWordDAO.getNextWords(helloID);

            for (Map.Entry<Integer, Integer> entry : nextWords.entrySet()) {

                String word = wordDAO.getWordByID(entry.getKey());
                int count = entry.getValue();

                System.out.println("hello → " + word + " (" + count + ")");
            }

            // Test random selection
            System.out.println("\nRandom next word selections:");

            for (int i = 0; i < 10; i++) {

                int nextWordID = nextWordDAO.getRandomNextWord(helloID);

                String nextWord = wordDAO.getWordByID(nextWordID);

                System.out.println("Next word: " + nextWord);
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
