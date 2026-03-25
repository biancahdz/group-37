package src.main.java.com.group37.sentencebuilder.testing;

import java.sql.*;
import java.util.List;

import src.main.java.com.group37.sentencebuilder.DAO.TxtFileDAO;

public class TestTxtFileDAO {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/SentenceBuilder";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "epic";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            TxtFileDAO dao = new TxtFileDAO(conn);

            // Insert file (matching your method signature)
            int txtID = dao.insertTxtFile("demo_file.txt", 1, 0, 0);
            System.out.println("Inserted txtID: " + txtID);

            // List all files
            List<String> files = dao.getAllTxtFiles();
            System.out.println("All files:");
            for (String f : files) {
                System.out.println(" - " + f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
