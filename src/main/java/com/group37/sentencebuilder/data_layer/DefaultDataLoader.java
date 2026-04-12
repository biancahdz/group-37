/**
 * File: DefaultDataLoader.java
 * Description: Preloads the database with bundled default text files on first
 *              login if the database is empty. Uses the same parsing and
 *              insertion logic as the manual import feature (TxtFileReader)
 *              to ensure consistency. Allows users to generate sentences
 *              immediately without needing to import files first.
 *
 * Author: Amrita Thapa
 * Created: 2026-04-09
 * Last Modified: 2026-04-09
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.data_layer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;

public class DefaultDataLoader {

    // Names of files bundled under src/main/resources/defaults/ */
    private static final String[] DEFAULT_FILES = {
        "sample1.txt",
        "sample2.txt"
    };

    /**
     * @return true only when we can connect and the {@code txt} table has no rows yet.
     *         If connect fails, returns false so we do not run preload on a bad connection
     *         ({@link Database#getTxtCount()} would otherwise report 0 with no connection).
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

    // Builds a list of TxtFileReader tasks for each bundled default file.
    // Each resource is copied to a temp file so TxtFileReader (which needs
    // a real File on disk) can read it normally.
    // Returns an empty list if extraction fails for a file — that file is skipped.
    public static List<Task<Void>> buildPreloadTasks() {
        List<Task<Void>> tasks = new ArrayList<>();

        for (String resourceName : DEFAULT_FILES) {
            try (InputStream in = DefaultDataLoader.class.getResourceAsStream("/defaults/" + resourceName)) {
                if (in == null) {
                    System.err.println("[DefaultDataLoader] Resource not found: /defaults/" + resourceName);
                    continue;
                }

                Path tempFile = Files.createTempFile("sb_default_", "_" + resourceName);
                tempFile.toFile().deleteOnExit();
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);

                TxtFileReader reader = new TxtFileReader(tempFile.toFile(), resourceName);
                tasks.add(reader.createTask());

            } catch (IOException e) {
                System.err.println("[DefaultDataLoader] Failed to extract: " + resourceName);
                e.printStackTrace();
            }
        }

        return tasks;
    }
}