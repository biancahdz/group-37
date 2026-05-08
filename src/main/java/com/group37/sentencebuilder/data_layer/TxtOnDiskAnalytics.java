/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    TxtOnDiskAnalytics.java
 *  Author:  Huy Nong, Bianca Hernandez 
 *
 *  Description:
 *      Computes per-file word and bigram statistics by scanning a `.txt` on disk.
 *      Used by Word Analytics as an offline-first path when per-import DB tables are missing.
 *
 *  Version: 1.0
 *  Created: 2026-04-06 
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Locate a target `.txt` under `Txt Files/` or by direct path
 *      - Tokenize sentences and compute top words, top bigrams, and headline aggregates
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

    /**
     * Author: Huy Nong
     * Description:
     *      Value object returned by {@link #scan(String, int)} containing ranked tables and aggregate metrics.
     */
    public record ScanResult(
            List<Database.TopWordEntry> topWords,
            List<Database.TopBigramEntry> topBigrams,
            Database.CorpusAggregate aggregate) {
    }

    /**
     * Author:Bianca Hernandez
     * Description: locates a .txt file by searching in the txt files folder or by 
     *            a direct path 
     *      <description>
     * 
     * @param txtName 
     * @return an optional containing the file if found empty otherwise
     */
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
     * Author: Bianca Hernandez
     * Description: scans a text file by name and returns
     *         statistics up to the given limit 
     *      
     * 
     * @param txtName 
     * @return optional scan result 
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

    /**
     * Author: Huy Nong
     * Description:
     *      Scans the provided text file and computes word-frequency and bigram-frequency statistics
     *      using the same sentence splitting and tokenization rules as the import pipeline.
     *
     *      This method is the core implementation behind {@link #scan(String, int)} and is used to
     *      support offline per-file analytics (reading the `.txt` directly from disk).
     *
     * @param file  the `.txt` file to read and analyze
     * @param limit maximum number of ranked word/bigram entries to return
     * @return computed top words, top bigrams, and aggregate metrics for the file
     * @throws IOException if the file cannot be read
     */
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

    /**
     * Author: Bianca Hernandez 
     * Description: Splits raw text into individual sentences using the same
     *           regex as TxtFileReader
     *      Same regex split as {@link TxtFileReader#getSentences(StringBuilder)}.
     * 
     * @param fullText the raw text to split 
     * @return list of parsed sentences 
     */
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

    /**
     * Author: Bianca Hernandez
     * Description: 
     *      Same cleaning as {@link TxtFileReader} import loop. 
     *      Cleans and tokenizes a sentence into lowercase words, 
     *      stripping all non-alphabetic character. 
     * 
     * @param sentence the sentence to tokenize 
     * @return list of cleaned lowercase words 
     */
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
