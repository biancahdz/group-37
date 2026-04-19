package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;
import com.group37.sentencebuilder.data_layer.Database.CorpusAggregate;
import com.group37.sentencebuilder.data_layer.TxtOnDiskAnalytics;
import com.group37.sentencebuilder.data_layer.Database.TopBigramEntry;
import com.group37.sentencebuilder.data_layer.Database.TopWordEntry;
import com.group37.sentencebuilder.data_layer.Database.TxtFileSummary;
import com.group37.sentencebuilder.ui.DarkSurfaceText;
import com.group37.sentencebuilder.ui.UiPreferences;

import com.group37.sentencebuilder.ui_layer.model.BigramRow;
import com.group37.sentencebuilder.ui_layer.model.FileCorpusStatRow;
import com.group37.sentencebuilder.ui_layer.model.WordFrequencyRow;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.shape.Rectangle;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Word analytics backed by {@link Database}. Dropdown switches scope; per-file word/pair tables
 * use {@code txt_word} / {@code txt_nextword} when those rows exist for an import.
 */
public class CorpusStatsController implements ApplicationPage, DatabasePage {

    static final String ALL_SOURCES = "All imported files";

    private static final int TABLE_LIMIT = 50;

    private static final NumberFormat INT_FMT = NumberFormat.getIntegerInstance(Locale.US);

    private static final DateTimeFormatter IMPORT_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a", Locale.US);

    @FXML
    private ComboBox<String> sourceCombo;

    @FXML
    private Button exportButton;

    @FXML
    private Label sectionEyebrowLabel;

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

    private Database database;

    private CorpusAggregate aggregate = new CorpusAggregate(0, 0, 0);

    private List<TopWordEntry> cachedWords = List.of();

    private List<TopBigramEntry> cachedBigrams = List.of();

    private List<FileCorpusStatRow> cachedFileRows = List.of();

    /** Combo label ({@code name · imported}) → row (includes {@code txtID} for per-file queries). */
    private final Map<String, TxtFileSummary> fileByComboLabel = new LinkedHashMap<>();

    @FXML
    private void initialize() {
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

        applyRoundedClip(wordTable, 12);
        applyRoundedClip(bigramTable, 12);
        applyRoundedClip(fileTable, 12);

        sourceCombo.valueProperty().addListener((o, prev, cur) -> {
            if (cur != null) {
                applySnapshot(cur);
            }
        });

        exportButton.setOnAction(e -> onExportRequested());
    }

    private static void applyRoundedClip(TableView<?> table, double radius) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(table.widthProperty());
        clip.heightProperty().bind(table.heightProperty());
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        table.setClip(clip);
    }

    /** Placeholder for backend export (e.g. CSV); use {@link #sourceCombo} scope when implementing. */
    private void onExportRequested() {
    }

    @Override
    public void setDatabase(Database db) {
        this.database = db;
    }

    @Override
    public void onPageEnter() {
        applyCorpusDarkChrome();
        reloadFromDatabase();
    }

    private void applyCorpusDarkChrome() {
        if (!UiPreferences.get().isResolvedDarkSurface()) {
            if (sectionEyebrowLabel != null) {
                sectionEyebrowLabel.setTextFill(null);
                sectionEyebrowLabel.setStyle(null);
            }
            return;
        }
        if (sectionEyebrowLabel != null) {
            DarkSurfaceText.forceLabeledFill(sectionEyebrowLabel, Color.WHITE);
            Platform.runLater(() -> DarkSurfaceText.forceLabeledFill(sectionEyebrowLabel, Color.WHITE));
        }
    }

    @Override
    public void onPageLeave() {
    }

    private void reloadFromDatabase() {
        String previous = sourceCombo.getValue();

        if (database == null) {
            showDisconnectedState();
            return;
        }

        if (!database.connect()) {
            database.disconnect();
            showDisconnectedState();
            return;
        }

        try {
            aggregate = database.fetchCorpusAggregate();
            cachedWords = database.fetchTopWords(TABLE_LIMIT);
            cachedBigrams = database.fetchTopBigrams(TABLE_LIMIT);

            List<TxtFileSummary> files = database.listTxtFileSummaries();
            fileByComboLabel.clear();
            List<FileCorpusStatRow> fileRows = new ArrayList<>();
            List<String> comboItems = new ArrayList<>();
            comboItems.add(ALL_SOURCES);

            for (TxtFileSummary f : files) {
                String label = f.txtName() + " · " + formatImported(f.importedAt());
                fileByComboLabel.put(label, f);
                comboItems.add(label);
                fileRows.add(new FileCorpusStatRow(
                        f.txtName(),
                        INT_FMT.format(f.numSentences()),
                        INT_FMT.format(f.numWords()),
                        formatImported(f.importedAt())));
            }
            cachedFileRows = fileRows;

            sourceCombo.getItems().setAll(comboItems);

            if (previous != null && comboItems.contains(previous)) {
                sourceCombo.setValue(previous);
            } else {
                sourceCombo.getSelectionModel().selectFirst();
            }

            applySnapshot(sourceCombo.getValue());
        } finally {
            database.disconnect();
        }
    }

    private void showDisconnectedState() {
        aggregate = new CorpusAggregate(0, 0, 0);
        cachedWords = List.of();
        cachedBigrams = List.of();
        cachedFileRows = List.of();
        fileByComboLabel.clear();
        sourceCombo.getItems().setAll(ALL_SOURCES);
        sourceCombo.getSelectionModel().selectFirst();
        scopeHintLabel.setText("Could not connect to the database. Check MySQL and data/db_config.txt.");
        chipUniqueWords.setText("—");
        chipTotalTokens.setText("—");
        chipTopCombo.setText("—");
        wordTable.setItems(FXCollections.observableArrayList());
        bigramTable.setItems(FXCollections.observableArrayList());
        fileTable.setVisible(false);
        fileTable.setManaged(false);
        fileTable.setItems(FXCollections.observableArrayList());
    }

    private void applySnapshot(String key) {
        if (key == null) {
            return;
        }

        if (ALL_SOURCES.equals(key)) {
            List<WordFrequencyRow> wordRows = new ArrayList<>();
            int rank = 1;
            for (TopWordEntry e : cachedWords) {
                wordRows.add(new WordFrequencyRow(String.valueOf(rank++), e.word(), INT_FMT.format(e.count())));
            }
            List<BigramRow> biRows = new ArrayList<>();
            rank = 1;
            for (TopBigramEntry e : cachedBigrams) {
                biRows.add(new BigramRow(
                        String.valueOf(rank++), e.firstWord(), e.secondWord(), INT_FMT.format(e.count())));
            }
            wordTable.setItems(FXCollections.observableArrayList(wordRows));
            bigramTable.setItems(FXCollections.observableArrayList(biRows));

            scopeHintLabel.setText("Aggregating every imported text file. Word and pair ranks use the full corpus.");
            chipUniqueWords.setText(INT_FMT.format(aggregate.uniqueWordTypes()));
            chipTotalTokens.setText(INT_FMT.format(aggregate.totalTokens()));
            chipTopCombo.setText(aggregate.topBigramCount() > 0 ? INT_FMT.format(aggregate.topBigramCount()) : "—");

            boolean hasFiles = !cachedFileRows.isEmpty();
            fileTable.setVisible(hasFiles);
            fileTable.setManaged(hasFiles);
            if (hasFiles) {
                fileTable.setItems(FXCollections.observableArrayList(cachedFileRows));
                fileTableSubtitle.setText("Each imported file — sentence totals, token totals, and ingest time.");
            } else {
                fileTable.setItems(FXCollections.observableArrayList());
                fileTableSubtitle.setText("No files in the corpus yet. Import a .txt file to see per-file totals here.");
            }
            return;
        }

        TxtFileSummary meta = fileByComboLabel.get(key);
        if (meta == null) {
            return;
        }

        // Prefer on-disk scan (no extra DB tables): Txt Files/<txtName> under the app working directory.
        Optional<TxtOnDiskAnalytics.ScanResult> fromDisk =
                TxtOnDiskAnalytics.scan(meta.txtName(), TABLE_LIMIT);
        if (fromDisk.isPresent()) {
            TxtOnDiskAnalytics.ScanResult r = fromDisk.get();
            renderPerFileTables(r.topWords(), r.topBigrams(), r.aggregate(), meta);
            scopeHintLabel.setText(
                    "Rankings from the file on disk (Txt Files/ or full path), using the same token rules as import. "
                            + "If this file changed after import, counts may differ from the database summary.");
            finishSingleFileScope();
            return;
        }

        // Optional fallback: per-row tables in MySQL (txt_word / txt_nextword) if present and populated.
        int txtId = meta.txtID();
        if (database == null || txtId <= 0) {
            clearPerFileTables(meta.txtName());
            finishSingleFileScope();
            return;
        }

        if (!database.connect()) {
            database.disconnect();
            scopeHintLabel.setText("Could not connect. Add \"" + meta.txtName()
                    + "\" under the project's Txt Files folder for offline per-file rankings, or fix the DB connection.");
            chipUniqueWords.setText("—");
            chipTotalTokens.setText("—");
            chipTopCombo.setText("—");
            wordTable.setItems(FXCollections.observableArrayList());
            bigramTable.setItems(FXCollections.observableArrayList());
            finishSingleFileScope();
            return;
        }

        try {
            List<TopWordEntry> words = database.fetchTopWordsForTxt(txtId, TABLE_LIMIT);
            List<TopBigramEntry> bigrams = database.fetchTopBigramsForTxt(txtId, TABLE_LIMIT);
            CorpusAggregate localAgg = database.fetchCorpusAggregateForTxt(txtId);

            renderPerFileTables(words, bigrams, localAgg, meta);

            boolean missingPerFileRows = meta.numWords() > 0 && words.isEmpty() && bigrams.isEmpty();
            if (missingPerFileRows) {
                scopeHintLabel.setText(
                        "No per-file DB rows for this import. Copy \"" + meta.txtName()
                                + "\" into the Txt Files folder (same name as in the database) to fill tables from disk.");
            } else {
                scopeHintLabel.setText("Word counts, pairs, and chips from per-import database rows.");
            }
        } finally {
            database.disconnect();
        }

        finishSingleFileScope();
    }

    private void renderPerFileTables(
            List<TopWordEntry> words,
            List<TopBigramEntry> bigrams,
            CorpusAggregate localAgg,
            TxtFileSummary meta) {
        List<WordFrequencyRow> wordRows = new ArrayList<>();
        int rank = 1;
        for (TopWordEntry e : words) {
            wordRows.add(new WordFrequencyRow(String.valueOf(rank++), e.word(), INT_FMT.format(e.count())));
        }
        List<BigramRow> biRows = new ArrayList<>();
        rank = 1;
        for (TopBigramEntry e : bigrams) {
            biRows.add(new BigramRow(
                    String.valueOf(rank++), e.firstWord(), e.secondWord(), INT_FMT.format(e.count())));
        }
        wordTable.setItems(FXCollections.observableArrayList(wordRows));
        bigramTable.setItems(FXCollections.observableArrayList(biRows));

        chipUniqueWords.setText(localAgg.uniqueWordTypes() > 0 ? INT_FMT.format(localAgg.uniqueWordTypes()) : "—");
        chipTotalTokens.setText(localAgg.totalTokens() > 0 ? INT_FMT.format(localAgg.totalTokens()) : "—");
        chipTopCombo.setText(localAgg.topBigramCount() > 0 ? INT_FMT.format(localAgg.topBigramCount()) : "—");

        if (localAgg.totalTokens() == 0 && meta.numWords() > 0) {
            chipTotalTokens.setText(INT_FMT.format(meta.numWords()));
        }
    }

    private void clearPerFileTables(String txtName) {
        scopeHintLabel.setText(
                "Could not find \"" + txtName + "\" on disk (try Txt Files/" + txtName
                        + " next to where you run the app). Per-file DB tables are also empty for this import.");
        chipUniqueWords.setText("—");
        chipTotalTokens.setText("—");
        chipTopCombo.setText("—");
        wordTable.setItems(FXCollections.observableArrayList());
        bigramTable.setItems(FXCollections.observableArrayList());
    }

    private void finishSingleFileScope() {
        fileTable.setVisible(false);
        fileTable.setManaged(false);
        fileTable.setItems(FXCollections.observableArrayList());
        fileTableSubtitle.setText("Select \"" + ALL_SOURCES + "\" to compare every file side by side.");
    }

    private static String formatImported(java.sql.Timestamp ts) {
        if (ts == null) {
            return "—";
        }
        LocalDateTime ldt = LocalDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
        return ldt.format(IMPORT_FMT);
    }
}
