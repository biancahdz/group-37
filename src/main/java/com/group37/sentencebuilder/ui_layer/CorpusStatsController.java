package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui_layer.model.BigramRow;
import com.group37.sentencebuilder.ui_layer.model.FileCorpusStatRow;
import com.group37.sentencebuilder.ui_layer.model.WordFrequencyRow;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Word analytics — hard-coded tables only; dropdown swaps sample datasets.
 */
public class CorpusStatsController implements ApplicationPage {

    static final String ALL_SOURCES = "All imported files";

    @FXML
    private ComboBox<String> sourceCombo;

    @FXML
    private Label scopeHintLabel;

    @FXML
    private Label chipUniqueWords;

    @FXML
    private Label chipTotalTokens;

    @FXML
    private Label chipTopCombo;

    @FXML
    private TableView<WordFrequencyRow> wordTable;

    @FXML
    private TableColumn<WordFrequencyRow, String> colWordRank;

    @FXML
    private TableColumn<WordFrequencyRow, String> colWord;

    @FXML
    private TableColumn<WordFrequencyRow, String> colWordCount;

    @FXML
    private TableView<BigramRow> bigramTable;

    @FXML
    private TableColumn<BigramRow, String> colBiRank;

    @FXML
    private TableColumn<BigramRow, String> colBiFirst;

    @FXML
    private TableColumn<BigramRow, String> colBiSecond;

    @FXML
    private TableColumn<BigramRow, String> colBiCount;

    @FXML
    private Label fileTableSubtitle;

    @FXML
    private TableView<FileCorpusStatRow> fileTable;

    @FXML
    private TableColumn<FileCorpusStatRow, String> colFileName;

    @FXML
    private TableColumn<FileCorpusStatRow, String> colFileSentences;

    @FXML
    private TableColumn<FileCorpusStatRow, String> colFileWords;

    @FXML
    private TableColumn<FileCorpusStatRow, String> colFileImported;

    private final Map<String, CorpusSnapshot> snapshots = new LinkedHashMap<>();

    @FXML
    private void initialize() {
        buildMockSnapshots();

        sourceCombo.getItems().setAll(snapshots.keySet());
        sourceCombo.getSelectionModel().selectFirst();

        colWordRank.setCellValueFactory(c -> c.getValue().rankProperty());
        colWord.setCellValueFactory(c -> c.getValue().wordProperty());
        colWordCount.setCellValueFactory(c -> c.getValue().countProperty());

        colBiRank.setCellValueFactory(c -> c.getValue().rankProperty());
        colBiFirst.setCellValueFactory(c -> c.getValue().firstWordProperty());
        colBiSecond.setCellValueFactory(c -> c.getValue().secondWordProperty());
        colBiCount.setCellValueFactory(c -> c.getValue().comboCountProperty());

        colFileName.setCellValueFactory(c -> c.getValue().fileNameProperty());
        colFileSentences.setCellValueFactory(c -> c.getValue().sentencesProperty());
        colFileWords.setCellValueFactory(c -> c.getValue().wordsProperty());
        colFileImported.setCellValueFactory(c -> c.getValue().importedProperty());

        sourceCombo.valueProperty().addListener((o, prev, cur) -> applySnapshot(cur));

        applySnapshot(sourceCombo.getSelectionModel().getSelectedItem());
    }

    private void buildMockSnapshots() {
        snapshots.put(ALL_SOURCES, new CorpusSnapshot(
                "Aggregating every text source in the corpus.",
                "12,840", "186,220", "4,902",
                List.of(
                        new WordFrequencyRow("1", "the", "9,842"),
                        new WordFrequencyRow("2", "and", "6,110"),
                        new WordFrequencyRow("3", "to", "4,905"),
                        new WordFrequencyRow("4", "of", "4,201"),
                        new WordFrequencyRow("5", "a", "3,884"),
                        new WordFrequencyRow("6", "in", "2,910"),
                        new WordFrequencyRow("7", "that", "2,403"),
                        new WordFrequencyRow("8", "it", "2,102")
                ),
                List.of(
                        new BigramRow("1", "of", "the", "1,240"),
                        new BigramRow("2", "in", "the", "982"),
                        new BigramRow("3", "to", "be", "876"),
                        new BigramRow("4", "on", "the", "744"),
                        new BigramRow("5", "and", "the", "701"),
                        new BigramRow("6", "at", "the", "612"),
                        new BigramRow("7", "for", "a", "540"),
                        new BigramRow("8", "with", "the", "498")
                ),
                List.of(
                        new FileCorpusStatRow("corpus_lecture_spring.txt", "3,201", "24,883", "Mar 15, 2026 · 4:51 PM"),
                        new FileCorpusStatRow("notes_draft.txt", "842", "6,110", "Mar 17, 2026 · 9:02 AM"),
                        new FileCorpusStatRow("sample_corpus.txt", "12,480", "89,204", "Mar 18, 2026 · 2:14 PM"),
                        new FileCorpusStatRow("micro_essay.txt", "317", "2,023", "Mar 19, 2026 · 11:20 AM")
                )
        ));

        snapshots.put("sample_corpus.txt", new CorpusSnapshot(
                "Stats scoped to sample_corpus.txt only.",
                "8,920", "89,204", "2,104",
                List.of(
                        new WordFrequencyRow("1", "the", "4,102"),
                        new WordFrequencyRow("2", "and", "2,881"),
                        new WordFrequencyRow("3", "data", "1,440"),
                        new WordFrequencyRow("4", "model", "1,205"),
                        new WordFrequencyRow("5", "to", "1,102"),
                        new WordFrequencyRow("6", "of", "998")
                ),
                List.of(
                        new BigramRow("1", "of", "the", "612"),
                        new BigramRow("2", "in", "the", "501"),
                        new BigramRow("3", "the", "data", "440"),
                        new BigramRow("4", "data", "set", "398"),
                        new BigramRow("5", "to", "the", "355")
                ),
                List.of()
        ));

        snapshots.put("notes_draft.txt", new CorpusSnapshot(
                "Stats scoped to notes_draft.txt only.",
                "1,940", "6,110", "188",
                List.of(
                        new WordFrequencyRow("1", "the", "402"),
                        new WordFrequencyRow("2", "todo", "301"),
                        new WordFrequencyRow("3", "meeting", "244"),
                        new WordFrequencyRow("4", "and", "220"),
                        new WordFrequencyRow("5", "chapter", "198")
                ),
                List.of(
                        new BigramRow("1", "the", "meeting", "88"),
                        new BigramRow("2", "for", "the", "72"),
                        new BigramRow("3", "in", "chapter", "61"),
                        new BigramRow("4", "and", "then", "54")
                ),
                List.of()
        ));

        snapshots.put("lecture_transcript.txt", new CorpusSnapshot(
                "Stats scoped to lecture_transcript.txt only.",
                "4,102", "31,402", "640",
                List.of(
                        new WordFrequencyRow("1", "the", "2,001"),
                        new WordFrequencyRow("2", "we", "1,442"),
                        new WordFrequencyRow("3", "so", "1,110"),
                        new WordFrequencyRow("4", "that", "990"),
                        new WordFrequencyRow("5", "is", "876")
                ),
                List.of(
                        new BigramRow("1", "we", "can", "240"),
                        new BigramRow("2", "going", "to", "198"),
                        new BigramRow("3", "of", "the", "176"),
                        new BigramRow("4", "this", "is", "155")
                ),
                List.of()
        ));
    }

    private void applySnapshot(String key) {
        CorpusSnapshot snap = snapshots.get(key);
        if (snap == null) {
            return;
        }

        scopeHintLabel.setText(snap.hint());
        chipUniqueWords.setText(snap.uniqueWords());
        chipTotalTokens.setText(snap.totalTokens());
        chipTopCombo.setText(snap.topComboCount());

        wordTable.setItems(FXCollections.observableArrayList(snap.words()));
        bigramTable.setItems(FXCollections.observableArrayList(snap.bigrams()));

        boolean showPerFile = ALL_SOURCES.equals(key);
        fileTable.setVisible(showPerFile);
        fileTable.setManaged(showPerFile);
        if (showPerFile) {
            fileTable.setItems(FXCollections.observableArrayList(snap.perFile()));
            fileTableSubtitle.setText("Each imported file — sentence totals, token totals, and ingest time.");
        } else {
            fileTable.setItems(FXCollections.observableArrayList());
            fileTableSubtitle.setText("Select \"" + ALL_SOURCES + "\" in the dropdown to compare every file side by side.");
        }
    }

    @Override
    public void onPageEnter() {
        // Tables stay in sync via cached controller state; avoid extra UI passes that could lag navigation.
    }

    @Override
    public void onPageLeave() {
    }

    private record CorpusSnapshot(
            String hint,
            String uniqueWords,
            String totalTokens,
            String topComboCount,
            List<WordFrequencyRow> words,
            List<BigramRow> bigrams,
            List<FileCorpusStatRow> perFile
    ) {}
}
