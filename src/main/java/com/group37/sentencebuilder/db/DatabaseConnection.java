/*==============================================================
  File Name   : DatabaseConnection.java
  Description : Provides a shared MySQL connection.

  Author      : Amrita Thapa
  Created On  : 2026-03-25

  Last Modified By : Amrita Thapa
  Last Modified On : 2026-03-25
  Change History   : 2026-03-25  - Amrita Thapa - Singleton db connection

  Database    : SentenceBuilder
==============================================================*/
package src.main.java.com.group37.sentencebuilder.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/SentenceBuilder";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "epic"; // <-- change this

    private static Connection instance = null;

    // Prevent instantiation
    private DatabaseConnection() {}

    // Returns a singleton Connection, opening one if it is null or closed.
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }
        return instance;
    }


    // Closes the shared connection. Call this once when the application shuts down.
    public static void close() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) {
                    instance.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                instance = null;
            }
        }
    }
}