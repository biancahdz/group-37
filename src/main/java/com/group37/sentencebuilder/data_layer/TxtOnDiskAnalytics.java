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

package com.group37.sentencebuilder.data_layer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Per-file word and bigram stats by re-reading a .txt from disk. Uses the same
 * sentence split and token rules as {@link TxtFileReader} — keep them aligned.
 * <p>
 * Looks for {@code Txt Files/&lt;txtName&gt;} under the JVM working directory, then
 * a plain {@code new File(txtName)} if that path exists.
 */
public final class TxtOnDiskAnalytics {

    private TxtOnDiskAnalytics() {
    }

    public record ScanResult(
            List<Database.TopWordEntry> topWords,
            List<Database.TopBigramEntry> topBigrams,
            Database.CorpusAggregate aggregate) {
    }

    public static Optional<File> locate(String txtName) {
        if (txtName == null || txtName.isBlank()) {
            return Optional.empty();
        }
        Path base = Path.of(System.getProperty("user.dir", "."));
        File inFolder = base.resolve("Txt Files").resolve(txtName).toFile();
        if (inFolder.isFile()) {
            return Optional.of(inFolder);
        }
        File asPath = new File(txtName);
        if (asPath.isFile()) {
            return Optional.of(asPath);
        }
        return Optional.empty();
    }

    /**
     * @return empty if the file is not found or cannot be read
     */
    public static Optional<ScanResult> scan(String txtName, int limit) {
        Optional<File> file = locate(txtName);
        if (file.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(scanFile(file.get(), limit));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    static ScanResult scanFile(File file, int limit) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append(" ");
            }
        }

        List<String> sentences = splitSentences(sb.toString());
        Map<String, Integer> wordCounts = new HashMap<>();
        Map<String, Integer> bigramCounts = new HashMap<>();

        for (String sentence : sentences) {
            List<String> tokens = tokenizeSentence(sentence);
            for (String w : tokens) {
                wordCounts.merge(w, 1, Integer::sum);
            }
            for (int i = 0; i < tokens.size() - 1; i++) {
                String key = tokens.get(i) + "\t" + tokens.get(i + 1);
                bigramCounts.merge(key, 1, Integer::sum);
            }
        }

        List<Database.TopWordEntry> topWords = wordCounts.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry<String, Integer>::getValue).reversed())
                .limit(limit)
                .map(e -> new Database.TopWordEntry(e.getKey(), e.getValue()))
                .toList();

        List<Database.TopBigramEntry> topBigrams = bigramCounts.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry<String, Integer>::getValue).reversed())
                .limit(limit)
                .map(e -> {
                    String[] p = e.getKey().split("\t", 2);
                    return new Database.TopBigramEntry(p[0], p[1], e.getValue());
                })
                .toList();

        long totalTokens = wordCounts.values().stream().mapToLong(Integer::intValue).sum();
        long unique = wordCounts.size();
        long maxPair = bigramCounts.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        return new ScanResult(
                new ArrayList<>(topWords),
                new ArrayList<>(topBigrams),
                new Database.CorpusAggregate(unique, totalTokens, maxPair));
    }

    /** Same regex split as {@link TxtFileReader#getSentences(StringBuilder)}. */
    static List<String> splitSentences(String fullText) {
        String regex = "(?<=[.!?][\"',]?)(?=\\s+|$)";
        String[] raw = fullText.trim().split(regex);
        List<String> sentences = new ArrayList<>();
        for (String s : raw) {
            if (!s.isBlank()) {
                sentences.add(s.trim().replaceAll("[\"',]+$", ""));
            }
        }
        return sentences;
    }

    /** Same cleaning as {@link TxtFileReader} import loop. */
    static List<String> tokenizeSentence(String sentence) {
        String[] rawWords = sentence.split("\\s+");
        List<String> words = new ArrayList<>();
        for (String w : rawWords) {
            String word = w.replaceAll("[^A-Za-z]", "").toLowerCase();
            if (!word.isBlank()) {
                words.add(word);
            }
        }
        return words;
    }
}
