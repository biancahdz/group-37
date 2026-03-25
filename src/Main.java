/*==============================================================
  File Name   : Main.java
  Description : This is the main entry point of the application.

  Author      : Amrita Thapa
  Created On  : 2026-03-25

  Last Modified By : Amrita Thapa
  Last Modified On : 2026-03-25
  Change History   : 2026-03-25  - Amrita Thapa - Created a basic
        temporary demo for DAO's and db connection

  Database    : SentenceBuilder
==============================================================*/
package src;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

import src.DAO.TxtFileDAO;
import src.db.DatabaseConnection;
import src.ingestion.TextFileImporter;

public class Main {

    public static void main(String[] args) throws Exception {

        Connection conn = DatabaseConnection.getConnection();

        // Ask user for the file path
        System.out.print("Enter the full path to your .txt file: ");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine().trim();
        scanner.close();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        System.out.println("Connecting to database...");
        System.out.println("Connected! Starting import of: " + file.getName());
        int txtID = TextFileImporter.importFile(file, conn);
        System.out.println("Done! Import complete.");

        // Prints the imported txt file's stats
        TxtFileDAO dao = new TxtFileDAO(conn);
        System.out.println( dao.getTxtStats(txtID));

        // List all files
        List<String> files = dao.getAllTxtFiles();
        System.out.println("All files:");
        for (String f : files) {
            System.out.println(" - " + f);
        }

        DatabaseConnection.close();
    }
}
