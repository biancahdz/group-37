/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    .java
 *  Author:  
 *
 *  Description:
 *      <description>
 *
 *  Version: 1.0
 *  Created: 
 *  Last Modified: 
 *
 *  Responsibilities:
 *      - <responsibilities 1>
 *      - <responsibilities 2>
 * ------------------------------------------------------------
 */

/**
 * File: DefaultDataLoader.java
 * Description: Preloads the database with bundled default text files on first
 *              login if the database is empty. Executes a pre-built SQL dump
 *              instead of parsing raw text files, making first-time setup
 *              significantly faster. Also ensures the database schema is
 *              correct on every login, rebuilding it from the bundled schema
 *              file if any required tables are missing.
 *
 * Author: Amrita Thapa
 * Created: 2026-04-09
 * Last Modified: 2026-04-10
 *
 */

package com.group37.sentencebuilder.data_layer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DefaultDataLoader {

    private static final String DUMP_RESOURCE   = "/database/default_data.sql";
    private static final String SCHEMA_RESOURCE = "/database/SentenceBuilderDatabase.sql";

    private static final String[] REQUIRED_TABLES = {
        "txt", "words", "sentence", "nextword", "txt_word", "txt_nextword"
    };

    /**
     * Checks schema on every login and rebuilds the database if any required
     * tables are missing, protecting against outdated or mismatched schemas.
     *
     * @param database a Database instance configured with valid credentials
     */
    public static void ensureSchema(Database database) {
        database.connect();
        boolean schemaOk = hasAllTables(database);
        database.disconnect();

        if (!schemaOk) {
            System.out.println("[DefaultDataLoader] Schema missing or incomplete — rebuilding...");
            rebuildSchema(database);
        }
    }

    /**
     * Returns true if all required tables exist in the database.
     *
     * @param database an already-connected Database instance
     * @return true if all required tables are present, false otherwise
     */
    private static boolean hasAllTables(Database database) {
        try {
            Connection conn = database.getConnection();
            DatabaseMetaData meta = conn.getMetaData();

            for (String table : REQUIRED_TABLES) {
                try (ResultSet rs = meta.getTables(
                        Database.DEFAULT_DATABASE_NAME, null, table, new String[]{"TABLE"})) {
                    if (!rs.next()) {
                        System.out.println("[DefaultDataLoader] Missing table: " + table);
                        return false;
                    }
                }
            }
            return true;

        } catch (SQLException e) {
            System.err.println("[DefaultDataLoader] Error checking tables: " + e.getMessage());
            return false;
        }
    }

    /**
     * Drops and recreates the SentenceBuilder database from the bundled
     * schema file. Connects to "mysql" first since the schema drops the DB.
     *
     * @param database a Database instance used to retrieve credentials
     */
    private static void rebuildSchema(Database database) {
        InputStream in = DefaultDataLoader.class.getResourceAsStream(SCHEMA_RESOURCE);

        if (in == null) {
            System.err.println("[DefaultDataLoader] Schema resource not found: " + SCHEMA_RESOURCE);
            return;
        }

        // Connect to root "mysql" DB since we are dropping and recreating SentenceBuilder
        Database rootDb = new Database(database.getUsername(), database.getPassword(), "mysql");
        rootDb.connect();

        try {
            Connection conn = rootDb.getConnection();
            conn.setAutoCommit(true);

            try (Statement stmt = conn.createStatement();
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8))) {

                // Skip UTF-8 BOM if present
                reader.mark(1);
                if (reader.read() != '\uFEFF') reader.reset();

                // Accumulate lines into full statements, execute on semicolon
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();

                    if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("/*")) continue;
                    if (trimmed.contains("--")) trimmed = trimmed.substring(0, trimmed.indexOf("--")).trim();

                    sb.append(trimmed).append(" ");

                    if (trimmed.endsWith(";")) {
                        String sql = sb.toString().trim();
                        if (!sql.isEmpty()) {
                            try { stmt.execute(sql); }
                            catch (SQLException e) {
                                System.err.println("[DefaultDataLoader] Schema statement failed: " + e.getMessage());
                            }
                        }
                        sb.setLength(0);
                    }
                }

                System.out.println("[DefaultDataLoader] Schema rebuilt successfully.");

            } catch (Exception e) {
                System.err.println("[DefaultDataLoader] Schema rebuild failed: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("[DefaultDataLoader] Root connection error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            rootDb.disconnect();
        }
    }

    /**
     * Returns true if the database has no imported text files yet.
     *
     * @param database a Database instance configured with valid credentials
     * @return true if no text files have been imported yet, false otherwise
     */
    public static boolean isDatabaseEmpty(Database database) {
        if (!database.connect()) {
            return false;
        }
        try {
            return database.getTxtCount() == 0;
        } finally {
            database.disconnect();
        }
    }

    /**
     * Executes the pre-built SQL dump into the database in a single
     * transaction using INSERT IGNORE to skip duplicate sentinel rows.
     *
     * @param database a Database instance configured with valid credentials
     * @return true if the dump was executed successfully, false otherwise
     */
    public static boolean executeDump(Database database) {
        InputStream in = DefaultDataLoader.class.getResourceAsStream(DUMP_RESOURCE);

        if (in == null) {
            System.err.println("[DefaultDataLoader] Dump resource not found: " + DUMP_RESOURCE);
            return false;
        }

        database.connect();

        try {
            Connection conn = database.getConnection();
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement();
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8))) {

                // Skip UTF-8 BOM if present
                reader.mark(1);
                if (reader.read() != '\uFEFF') reader.reset();

                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();

                    if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("/*")) continue;

                    // INSERT IGNORE skips duplicate sentinel rows without error
                    stmt.addBatch(trimmed.replaceFirst("(?i)^INSERT INTO", "INSERT IGNORE INTO"));
                }

                stmt.executeBatch();
                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                System.err.println("[DefaultDataLoader] Dump execution failed: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("[DefaultDataLoader] Connection error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            database.disconnect();
        }
    }
}