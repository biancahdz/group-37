package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;
import com.group37.sentencebuilder.data_layer.Database.CorpusAggregate;
import com.group37.sentencebuilder.data_layer.TxtOnDiskAnalytics;
import com.group37.sentencebuilder.data_layer.Database.TopBigramEntry;
import com.group37.sentencebuilder.data_layer.Database.TopWordEntry;
import com.group37.sentencebuilder.data_layer.Database.TxtFileSummary;
import com.group37.sentencebuilder.ui.LabelThemeRegistry;

import com.group37.sentencebuilder.ui_layer.model.BigramRow;
import com.group37.sentencebuilder.ui_layer.model.FileCorpusStatRow;
import com.group37.sentencebuilder.ui_layer.model.WordFrequencyRow;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

    private final LabelThemeRegistry labelTheme = new LabelThemeRegistry();

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

        labelTheme.add(sectionEyebrowLabel, Color.WHITE);
    }

    private static void applyRoundedClip(TableView<?> table, double radius) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(table.widthProperty());
        clip.heightProperty().bind(table.heightProperty());
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        table.setClip(clip);
    }

    private void onExportRequested() {
        ChoiceDialog<String> formatDialog = new ChoiceDialog<>("TXT", Arrays.asList("TXT", "CSV"));
        formatDialog.setTitle("Export word analytics");
        formatDialog.setHeaderText("Choose export format");
        formatDialog.setContentText("Format:");

        Optional<String> selected = formatDialog.showAndWait();
        if (selected.isEmpty()) {
            return;
        }

        String format = selected.get();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save analytics export");
        String scope = sourceCombo.getValue() == null ? "analytics" : sourceCombo.getValue().replaceAll("[^A-Za-z0-9._-]+", "_");
        String extension = format.equals("CSV") ? ".csv" : ".txt";
        chooser.setInitialFileName("word-analytics-" + scope + extension);
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter(format + " file", "*" + extension)
        );

        File target = chooser.showSaveDialog(exportButton.getScene().getWindow());
        if (target == null) {
            return;
        }

        String out = format.equals("CSV") ? buildCsvExport() : buildTxtExport();
        try {
            Files.writeString(target.toPath(), out, StandardCharsets.UTF_8);
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Export complete");
            ok.setHeaderText("Saved analytics export");
            ok.setContentText(target.getAbsolutePath());
            ok.showAndWait();
        } catch (IOException ex) {
            Alert fail = new Alert(Alert.AlertType.ERROR);
            fail.setTitle("Export failed");
            fail.setHeaderText("Could not write export file");
            fail.setContentText(ex.getMessage());
            fail.showAndWait();
        }
    }

    private String buildTxtExport() {
        StringBuilder sb = new StringBuilder();
        sb.append("WORD ANALYTICS EXPORT\n");
        sb.append("Scope: ").append(sourceCombo.getValue() == null ? "—" : sourceCombo.getValue()).append('\n');
        sb.append("Hint: ").append(scopeHintLabel.getText() == null ? "" : scopeHintLabel.getText()).append('\n');
        sb.append('\n');
        sb.append("SUMMARY CHIPS\n");
        sb.append("- Unique words: ").append(chipUniqueWords.getText()).append('\n');
        sb.append("- Total tokens: ").append(chipTotalTokens.getText()).append('\n');
        sb.append("- Top pair count: ").append(chipTopCombo.getText()).append('\n');
        sb.append('\n');

        sb.append("HIGHEST WORD COUNTS\n");
        sb.append("No.\tWord\tCount\n");
        for (WordFrequencyRow row : wordTable.getItems()) {
            sb.append(row.rankProperty().get()).append('\t')
                    .append(row.getWord()).append('\t')
                    .append(row.getCount()).append('\n');
        }
        sb.append('\n');

        sb.append("STRONGEST WORD PAIRS\n");
        sb.append("No.\tFirst\tNext\tPair Count\n");
        for (BigramRow row : bigramTable.getItems()) {
            sb.append(row.rankProperty().get()).append('\t')
                    .append(row.getFirstWord()).append('\t')
                    .append(row.getSecondWord()).append('\t')
                    .append(row.getComboCount()).append('\n');
        }
        sb.append('\n');

        sb.append("PER-FILE BREAKDOWN\n");
        sb.append("Subtitle: ").append(fileTableSubtitle.getText() == null ? "" : fileTableSubtitle.getText()).append('\n');
        sb.append("File\tSentences\tWords\tImported\n");
        if (fileTable.getItems().isEmpty()) {
            sb.append("(No rows in current scope)\n");
        } else {
            for (FileCorpusStatRow row : fileTable.getItems()) {
                sb.append(row.fileNameProperty().get()).append('\t')
                        .append(row.sentencesProperty().get()).append('\t')
                        .append(row.wordsProperty().get()).append('\t')
                        .append(row.importedProperty().get()).append('\n');
            }
        }
        return sb.toString();
    }

    private String buildCsvExport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Section,Field,Value\n");
        sb.append(csv("Summary", "Scope", sourceCombo.getValue() == null ? "—" : sourceCombo.getValue()));
        sb.append(csv("Summary", "Hint", scopeHintLabel.getText()));
        sb.append(csv("Summary", "Unique words", chipUniqueWords.getText()));
        sb.append(csv("Summary", "Total tokens", chipTotalTokens.getText()));
        sb.append(csv("Summary", "Top pair count", chipTopCombo.getText()));
        sb.append('\n');

        sb.append("Word Counts,No.,Word,Count\n");
        if (wordTable.getItems().isEmpty()) {
            sb.append("Word Counts,(none),,\n");
        } else {
            for (WordFrequencyRow row : wordTable.getItems()) {
                sb.append(csvLine("Word Counts", row.rankProperty().get(), row.getWord(), row.getCount()));
            }
        }
        sb.append('\n');

        sb.append("Word Pairs,No.,First,Next,Pair Count\n");
        if (bigramTable.getItems().isEmpty()) {
            sb.append("Word Pairs,(none),,,\n");
        } else {
            for (BigramRow row : bigramTable.getItems()) {
                sb.append(csvLine("Word Pairs", row.rankProperty().get(), row.getFirstWord(), row.getSecondWord(), row.getComboCount()));
            }
        }
        sb.append('\n');

        sb.append("Per-file Breakdown,File,Sentences,Words,Imported\n");
        if (fileTable.getItems().isEmpty()) {
            sb.append("Per-file Breakdown,(none),,,\n");
        } else {
            for (FileCorpusStatRow row : fileTable.getItems()) {
                sb.append(csvLine("Per-file Breakdown",
                        row.fileNameProperty().get(),
                        row.sentencesProperty().get(),
                        row.wordsProperty().get(),
                        row.importedProperty().get()));
            }
        }
        return sb.toString();
    }

    private static String csv(String section, String field, String value) {
        return csvLine(section, field, value);
    }

    private static String csvLine(String... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append('"');
            String value = values[i] == null ? "" : values[i];
            line.append(value.replace("\"", "\"\""));
            line.append('"');
        }
        line.append('\n');
        return line.toString();
    }

    @Override
    public void setDatabase(Database db) {
        this.database = db;
    }

    @Override
    public void onPageEnter() {
        labelTheme.apply();
        reloadFromDatabase();
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
