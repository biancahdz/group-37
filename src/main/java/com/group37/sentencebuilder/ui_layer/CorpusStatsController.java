/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    CorpusStatsController.java
 *  Author:  Sebastian Sarinana, Huy Nong
 *
 *  Description:
 *      Word analytics controller. Displays corpus-wide and per-file word/bigram rankings, summary chips, and charts.
 *
 *  Version: 1.0
 *  Created: 2026-03-31
 *  Last Modified: 2026-05-07
 *
 *  Responsibilities:
 *      - Query analytics aggregates/rankings from Database and render tables/charts
 *      - Support switching scope between all sources and a selected imported file
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;
import com.group37.sentencebuilder.data_layer.Database.CorpusAggregate;
import com.group37.sentencebuilder.data_layer.TxtOnDiskAnalytics;
import com.group37.sentencebuilder.data_layer.Database.TopBigramEntry;
import com.group37.sentencebuilder.data_layer.Database.TopWordEntry;
import com.group37.sentencebuilder.data_layer.Database.TxtFileSummary;
import com.group37.sentencebuilder.ui_layer.theming.AppTheme;
import com.group37.sentencebuilder.ui_layer.theming.UiPreferences;
import com.group37.sentencebuilder.ui_layer.theming.LabelThemeRegistry;

import com.group37.sentencebuilder.ui_layer.model.BigramRow;
import com.group37.sentencebuilder.ui_layer.model.FileCorpusStatRow;
import com.group37.sentencebuilder.ui_layer.model.WordFrequencyRow;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Word analytics backed by Database. Dropdown switches scope; per-file word/pair tables
 * use txt_word / txt_nextword when those rows exist for an import.
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
    private Button compareFilesButton;

    @FXML
    private ToggleButton tablesButton;

    @FXML
    private ToggleButton chartsButton;

    @FXML
    private Label sectionEyebrowLabel;

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
    private TableColumn<FileCorpusStatRow, String> colFileUniqueWords;

    @FXML
    private TableColumn<FileCorpusStatRow, String> colFileTopPairCount;

    @FXML
    private TableColumn<FileCorpusStatRow, String> colFileImported;

    @FXML
    private Label fileCompareSubtitle;

    @FXML
    private BarChart<String, Number> topWordsChart;

    @FXML
    private BarChart<String, Number> topPairsChart;

    @FXML
    private BarChart<String, Number> fileCompareChart;

    @FXML
    private TabPane analyticsTabPane;

    @FXML
    private Tab chartsTab;

    private final LabelThemeRegistry labelTheme = new LabelThemeRegistry();

    private Database database;

    private CorpusAggregate aggregate = new CorpusAggregate(0, 0, 0);

    private List<TopWordEntry> cachedWords = List.of();

    private List<TopBigramEntry> cachedBigrams = List.of();

    private List<FileCorpusStatRow> cachedFileRows = List.of();
    private String lastScopeHint = "";

    /** Combo label (name · imported) → row (includes txtID for per-file queries). */
    private final Map<String, TxtFileSummary> fileByComboLabel = new LinkedHashMap<>();
    /** Combo label (name · imported) → rendered comparison row for tables/charts. */
    private final Map<String, FileCorpusStatRow> fileRowByComboLabel = new LinkedHashMap<>();
    /** Selected combo labels to include in file comparison charts. */
    private final Set<String> selectedCompareLabels = new LinkedHashSet<>();

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      JavaFX lifecycle initializer. Binds all table column cell-value factories, wires
     *      source-combo, export, compare, and tab-toggle listeners, configures bar-chart gaps
     *      and axis rotation, and applies the initial chart theme.
     */
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
        colFileUniqueWords.setCellValueFactory(c -> c.getValue().uniqueWordsProperty());
        colFileTopPairCount.setCellValueFactory(c -> c.getValue().topPairCountProperty());
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
        compareFilesButton.setOnAction(e -> onCompareFilesRequested());

        tablesButton.setOnAction(e -> analyticsTabPane.getSelectionModel().selectFirst());
        chartsButton.setOnAction(e -> analyticsTabPane.getSelectionModel().select(chartsTab));
        analyticsTabPane.getSelectionModel().selectedItemProperty().addListener((o, prev, cur) -> updateTabToggle());
        updateTabToggle();

        Platform.runLater(() -> {
            javafx.scene.Node headerArea = analyticsTabPane.lookup(".tab-header-area");
            if (headerArea != null) {
                headerArea.setVisible(false);
                headerArea.setManaged(false);
            }
        });

        topWordsChart.setCategoryGap(16);
        topWordsChart.setBarGap(4);
        topPairsChart.setCategoryGap(16);
        topPairsChart.setBarGap(4);
        fileCompareChart.setCategoryGap(18);
        fileCompareChart.setBarGap(6);
        ((CategoryAxis) topWordsChart.getXAxis()).setTickLabelRotation(-20);
        ((CategoryAxis) topPairsChart.getXAxis()).setTickLabelRotation(-28);
        ((CategoryAxis) fileCompareChart.getXAxis()).setTickLabelRotation(-24);
        topWordsChart.setLegendVisible(true);
        topPairsChart.setLegendVisible(true);
        fileCompareChart.setLegendVisible(true);
        UiPreferences.get().themeProperty().addListener((obs, oldV, newV) -> applyChartTheme());
        applyChartTheme();

        labelTheme.add(sectionEyebrowLabel, Color.WHITE);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Applies a rounded-rectangle clip to a TableView so its corners match the UI card style.
     *
     * @param table  the TableView to clip
     * @param radius the corner arc radius in pixels
     */
    private static void applyRoundedClip(TableView<?> table, double radius) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(table.widthProperty());
        clip.heightProperty().bind(table.heightProperty());
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        table.setClip(clip);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Prompts the user to choose a TXT or CSV export format and a save path, then writes
     *      the current analytics snapshot (summary chips, word table, bigram table, per-file
     *      breakdown, and compare selection) to disk and confirms with an alert.
     */
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

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Builds a plain-text representation of the current analytics view, including the scope
     *      hint, summary chips, word-count table, bigram table, per-file breakdown, and the
     *      selected file comparison section.
     *
     * @return a formatted multi-line string ready to be written to a .txt export file
     */
    private String buildTxtExport() {
        StringBuilder sb = new StringBuilder();
        sb.append("WORD ANALYTICS EXPORT\n");
        sb.append("Scope: ").append(sourceCombo.getValue() == null ? "—" : sourceCombo.getValue()).append('\n');
        sb.append("Hint: ").append(getScopeHintText()).append('\n');
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
        sb.append("File\tSentences\tWords\tUnique Words\tTop Pair Count\tImported\n");
        if (fileTable.getItems().isEmpty()) {
            sb.append("(No rows in current scope)\n");
        } else {
            for (FileCorpusStatRow row : fileTable.getItems()) {
                sb.append(row.fileNameProperty().get()).append('\t')
                        .append(row.sentencesProperty().get()).append('\t')
                        .append(row.wordsProperty().get()).append('\t')
                        .append(row.uniqueWordsProperty().get()).append('\t')
                        .append(row.topPairCountProperty().get()).append('\t')
                        .append(row.importedProperty().get()).append('\n');
            }
        }
        sb.append('\n');
        appendCompareSelectionTxt(sb);
        return sb.toString();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Opens a multi-select checkbox dialog listing all imported files and updates the set of
     *      labels used in the file-comparison bar chart. Navigates to the charts tab on confirm
     *      and warns the user if no files are selected.
     */
    private void onCompareFilesRequested() {
        if (fileByComboLabel.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No files to compare");
            alert.setHeaderText("Import files first");
            alert.setContentText("There are no imported text files available to compare yet.");
            alert.showAndWait();
            return;
        }

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Compare files");
        dialog.setHeaderText("Select imported files for chart comparison");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox checksBox = new VBox(8);
        List<CheckBox> checkBoxes = new ArrayList<>();
        List<String> effectiveSelection = getEffectiveCompareLabels();
        for (String label : fileByComboLabel.keySet()) {
            CheckBox cb = new CheckBox(label);
            cb.setSelected(effectiveSelection.contains(label));
            checkBoxes.add(cb);
            checksBox.getChildren().add(cb);
        }

        ScrollPane scroll = new ScrollPane(checksBox);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(260);
        dialog.getDialogPane().setContent(scroll);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) {
                return null;
            }
            List<String> selected = new ArrayList<>();
            for (CheckBox cb : checkBoxes) {
                if (cb.isSelected()) {
                    selected.add(cb.getText());
                }
            }
            return selected;
        });

        Optional<List<String>> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }
        if (result.get().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No files selected");
            alert.setHeaderText("Keep at least one file");
            alert.setContentText("Select one or more files to compare in charts.");
            alert.showAndWait();
            return;
        }

        selectedCompareLabels.clear();
        selectedCompareLabels.addAll(result.get());
        refreshCharts(sourceCombo.getValue());
        if (analyticsTabPane != null && chartsTab != null) {
            analyticsTabPane.getSelectionModel().select(chartsTab);
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Synchronizes the Tables/Charts toggle-button pair with the currently selected tab,
     *      applying active and inactive inline styles so only the selected button appears highlighted.
     */
    private void updateTabToggle() {
        boolean onCharts = analyticsTabPane.getSelectionModel().getSelectedItem() == chartsTab;
        tablesButton.setSelected(!onCharts);
        chartsButton.setSelected(onCharts);

        String active   = "-fx-background-color: white; -fx-background-insets: 0; "
                        + "-fx-text-fill: #111111; -fx-effect: null;";
        String inactive = "-fx-background-color: transparent; -fx-background-insets: 0; "
                        + "-fx-text-fill: #888888; -fx-effect: null;";

        tablesButton.setStyle((onCharts ? inactive : active)
                + " -fx-background-radius: 10 0 0 10;");
        chartsButton.setStyle((onCharts ? active : inactive)
                + " -fx-background-radius: 0 10 10 0;");
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Builds a CSV representation of the current analytics view, including scope metadata,
     *      summary chips, word-count table, bigram table, per-file breakdown, and the selected
     *      file comparison section.
     *
     * @return a CSV-formatted string ready to be written to a .csv export file
     */
    private String buildCsvExport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Section,Field,Value\n");
        sb.append(csv("Summary", "Scope", sourceCombo.getValue() == null ? "—" : sourceCombo.getValue()));
        sb.append(csv("Summary", "Hint", getScopeHintText()));
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

        sb.append("Per-file Breakdown,File,Sentences,Words,Unique Words,Top Pair Count,Imported\n");
        if (fileTable.getItems().isEmpty()) {
            sb.append("Per-file Breakdown,(none),,,,,\n");
        } else {
            for (FileCorpusStatRow row : fileTable.getItems()) {
                sb.append(csvLine("Per-file Breakdown",
                        row.fileNameProperty().get(),
                        row.sentencesProperty().get(),
                        row.wordsProperty().get(),
                        row.uniqueWordsProperty().get(),
                        row.topPairCountProperty().get(),
                        row.importedProperty().get()));
            }
        }
        sb.append('\n');
        appendCompareSelectionCsv(sb);
        return sb.toString();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Convenience shorthand for csvLine with exactly three values (section, field, value).
     *
     * @param section the CSV section label
     * @param field the field name within the section
     * @param value the field value to export
     * @return a single CSV-formatted line string
     */
    private static String csv(String section, String field, String value) {
        return csvLine(section, field, value);
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Formats an arbitrary number of values as a single double-quoted CSV line, escaping
     *      embedded double-quotes with the standard two-quote sequence.
     *
     * @param values the field values to quote and join
     * @return a newline-terminated CSV line string
     */
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

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Stores the injected Database instance for use during analytics data loads.
     *
     * @param db the shared database connection wrapper
     */
    @Override
    public void setDatabase(Database db) {
        this.database = db;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Applies theme-aware label styling and triggers a full database reload to refresh
     *      corpus analytics when the word analytics page becomes active.
     */
    @Override
    public void onPageEnter() {
        labelTheme.apply();
        reloadFromDatabase();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      No cleanup required when leaving the word analytics page.
     */
    @Override
    public void onPageLeave() {
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Connects to the database, fetches corpus aggregates, top words, top bigrams, and
     *      per-file summaries, populates the source combo, and triggers an initial snapshot render.
     */
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
            fileRowByComboLabel.clear();
            List<FileCorpusStatRow> fileRows = new ArrayList<>();
            List<String> comboItems = new ArrayList<>();
            comboItems.add(ALL_SOURCES);

            for (TxtFileSummary f : files) {
                String label = f.txtName() + " · " + formatImported(f.importedAt());
                fileByComboLabel.put(label, f);
                comboItems.add(label);
                CorpusAggregate perFileAgg = database.fetchCorpusAggregateForTxt(f.txtID());
                FileCorpusStatRow row = new FileCorpusStatRow(
                        f.txtName(),
                        INT_FMT.format(f.numSentences()),
                        INT_FMT.format(f.numWords()),
                        perFileAgg.uniqueWordTypes() > 0 ? INT_FMT.format(perFileAgg.uniqueWordTypes()) : "—",
                        perFileAgg.topBigramCount() > 0 ? INT_FMT.format(perFileAgg.topBigramCount()) : "—",
                        formatImported(f.importedAt()));
                fileRows.add(row);
                fileRowByComboLabel.put(label, row);
            }
            cachedFileRows = fileRows;
            selectedCompareLabels.retainAll(fileByComboLabel.keySet());
            if (selectedCompareLabels.isEmpty()) {
                selectedCompareLabels.addAll(fileByComboLabel.keySet());
            }

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

    /**
     * Author: Huy Nong
     * Description:
     *      Resets all cached data and UI controls to an empty disconnected state and shows
     *      an error hint when the database cannot be reached.
     */
    private void showDisconnectedState() {
        aggregate = new CorpusAggregate(0, 0, 0);
        cachedWords = List.of();
        cachedBigrams = List.of();
        cachedFileRows = List.of();
        fileByComboLabel.clear();
        fileRowByComboLabel.clear();
        selectedCompareLabels.clear();
        sourceCombo.getItems().setAll(ALL_SOURCES);
        sourceCombo.getSelectionModel().selectFirst();
        setScopeHint("Could not connect to the database. Check MySQL and data/db_config.txt.");
        chipUniqueWords.setText("—");
        chipTotalTokens.setText("—");
        chipTopCombo.setText("—");
        wordTable.setItems(FXCollections.observableArrayList());
        bigramTable.setItems(FXCollections.observableArrayList());
        fileTable.setVisible(false);
        fileTable.setManaged(false);
        fileTable.setItems(FXCollections.observableArrayList());
        clearCharts();
        fileCompareSubtitle.setText("No data to chart while disconnected.");
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Populates tables, chips, and charts for the selected source scope. When the scope is
     *      "All imported files" uses cached corpus data; for a specific file, prefers an on-disk
     *      scan then falls back to per-file database rows.
     *
     * @param key the combo label identifying the scope to display
     */
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

            setScopeHint("Aggregating every imported text file. Word and pair ranks use the full corpus.");
            chipUniqueWords.setText(INT_FMT.format(aggregate.uniqueWordTypes()));
            chipTotalTokens.setText(INT_FMT.format(aggregate.totalTokens()));
            chipTopCombo.setText(aggregate.topBigramCount() > 0 ? INT_FMT.format(aggregate.topBigramCount()) : "—");

            boolean hasFiles = !cachedFileRows.isEmpty();
            fileTable.setVisible(hasFiles);
            fileTable.setManaged(hasFiles);
            if (hasFiles) {
                fileTable.setItems(FXCollections.observableArrayList(cachedFileRows));
                fileTableSubtitle.setText("Compare each import by sentence count, token count, unique words, top pair count, and ingest time.");
            } else {
                fileTable.setItems(FXCollections.observableArrayList());
                fileTableSubtitle.setText("No files in the corpus yet. Import a .txt file to see per-file totals here.");
            }
            refreshCharts(key);
            return;
        }

        TxtFileSummary meta = fileByComboLabel.get(key);
        if (meta == null) {
            clearCharts();
            fileCompareSubtitle.setText("No file selected for chart comparison.");
            return;
        }

        // Prefer on-disk scan (no extra DB tables): Txt Files/<txtName> under the app working directory.
        Optional<TxtOnDiskAnalytics.ScanResult> fromDisk =
                TxtOnDiskAnalytics.scan(meta.txtName(), TABLE_LIMIT);
        if (fromDisk.isPresent()) {
            TxtOnDiskAnalytics.ScanResult r = fromDisk.get();
            renderPerFileTables(r.topWords(), r.topBigrams(), r.aggregate(), meta);
            setScopeHint(
                    "Rankings from the file on disk (Txt Files/ or full path), using the same token rules as import. "
                            + "If this file changed after import, counts may differ from the database summary.");
            finishSingleFileScope();
            refreshCharts(key);
            return;
        }

        // Optional fallback: per-row tables in MySQL (txt_word / txt_nextword) if present and populated.
        int txtId = meta.txtID();
        if (database == null || txtId <= 0) {
            clearPerFileTables(meta.txtName());
            finishSingleFileScope();
            refreshCharts(key);
            return;
        }

        if (!database.connect()) {
            database.disconnect();
            setScopeHint("Could not connect. Add \"" + meta.txtName()
                    + "\" under the project's Txt Files folder for offline per-file rankings, or fix the DB connection.");
            chipUniqueWords.setText("—");
            chipTotalTokens.setText("—");
            chipTopCombo.setText("—");
            wordTable.setItems(FXCollections.observableArrayList());
            bigramTable.setItems(FXCollections.observableArrayList());
            finishSingleFileScope();
            refreshCharts(key);
            return;
        }

        try {
            List<TopWordEntry> words = database.fetchTopWordsForTxt(txtId, TABLE_LIMIT);
            List<TopBigramEntry> bigrams = database.fetchTopBigramsForTxt(txtId, TABLE_LIMIT);
            CorpusAggregate localAgg = database.fetchCorpusAggregateForTxt(txtId);

            renderPerFileTables(words, bigrams, localAgg, meta);

            boolean missingPerFileRows = meta.numWords() > 0 && words.isEmpty() && bigrams.isEmpty();
            if (missingPerFileRows) {
                setScopeHint(
                        "No per-file DB rows for this import. Copy \"" + meta.txtName()
                                + "\" into the Txt Files folder (same name as in the database) to fill tables from disk.");
            } else {
                setScopeHint("Word counts, pairs, and chips from per-import database rows.");
            }
        } finally {
            database.disconnect();
        }

        finishSingleFileScope();
        refreshCharts(key);
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Converts per-file word and bigram entry lists into ranked table rows, updates
     *      the word and bigram tables, and refreshes the summary chip values.
     *
     * @param words the top word entries for the selected file
     * @param bigrams the top bigram entries for the selected file
     * @param localAgg the corpus aggregate for the selected file
     * @param meta the file summary record for fallback total token count
     */
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

    /**
     * Author: Huy Nong
     * Description:
     *      Clears word and bigram tables and resets chips to "—" when neither the on-disk file
     *      nor per-file database rows are available, and stores a descriptive scope hint.
     *
     * @param txtName the name of the file that could not be found on disk or in the database
     */
    private void clearPerFileTables(String txtName) {
        setScopeHint(
                "Could not find \"" + txtName + "\" on disk (try Txt Files/" + txtName
                        + " next to where you run the app). Per-file DB tables are also empty for this import.");
        chipUniqueWords.setText("—");
        chipTotalTokens.setText("—");
        chipTopCombo.setText("—");
        wordTable.setItems(FXCollections.observableArrayList());
        bigramTable.setItems(FXCollections.observableArrayList());
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Hides and empties the per-file breakdown table and updates its subtitle to prompt
     *      switching to "All imported files" for side-by-side comparison.
     */
    private void finishSingleFileScope() {
        fileTable.setVisible(false);
        fileTable.setManaged(false);
        fileTable.setItems(FXCollections.observableArrayList());
        fileTableSubtitle.setText("Select \"" + ALL_SOURCES + "\" to compare every file side by side.");
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Rebuilds all three bar charts (top words, top pairs, file comparison) for the current
     *      scope and reapplies the active chart theme colors.
     *
     * @param scopeKey the currently selected source combo label
     */
    private void refreshCharts(String scopeKey) {
        updateTopWordsChart();
        updateTopPairsChart();
        updateFileCompareChart(scopeKey);
        applyChartTheme();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Removes all data series from all three bar charts, leaving them empty.
     */
    private void clearCharts() {
        topWordsChart.getData().clear();
        topPairsChart.getData().clear();
        fileCompareChart.getData().clear();
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Populates the top-words bar chart with up to 10 entries from the current word table,
     *      then decorates each bar with the accent theme color and a hover tooltip.
     */
    private void updateTopWordsChart() {
        topWordsChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Word count");
        int count = 0;
        for (WordFrequencyRow row : wordTable.getItems()) {
            if (count++ >= 10) {
                break;
            }
            series.getData().add(new XYChart.Data<>(row.getWord(), parseCount(row.getCount())));
        }
        topWordsChart.getData().add(series);
        decorateSeries(series, "Word count", themeHex("accent"));
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Populates the top-pairs bar chart with up to 10 bigram entries from the bigram table,
     *      labeling each bar as "first → second" and decorating with the violet theme color.
     */
    private void updateTopPairsChart() {
        topPairsChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pair count");
        int count = 0;
        for (BigramRow row : bigramTable.getItems()) {
            if (count++ >= 10) {
                break;
            }
            String pairLabel = row.getFirstWord() + " → " + row.getSecondWord();
            series.getData().add(new XYChart.Data<>(pairLabel, parseCount(row.getComboCount())));
        }
        topPairsChart.getData().add(series);
        decorateSeries(series, "Pair count", themeHex("violet"));
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Builds three grouped series (words, unique words, top pair count) for the file comparison
     *      chart. Uses the effective compare-label selection when multiple files are available,
     *      or falls back to showing the current scope file only.
     *
     * @param scopeKey the currently selected source combo label, used for single-file fallback
     */
    private void updateFileCompareChart(String scopeKey) {
        fileCompareChart.getData().clear();
        XYChart.Series<String, Number> wordsSeries = new XYChart.Series<>();
        wordsSeries.setName("Words");
        XYChart.Series<String, Number> uniqueSeries = new XYChart.Series<>();
        uniqueSeries.setName("Unique words");
        XYChart.Series<String, Number> topPairSeries = new XYChart.Series<>();
        topPairSeries.setName("Top pair count");

        List<String> compareLabels = getEffectiveCompareLabels();
        if (!compareLabels.isEmpty() && !fileRowByComboLabel.isEmpty()) {
            for (String label : compareLabels) {
                FileCorpusStatRow row = fileRowByComboLabel.get(label);
                if (row == null) {
                    continue;
                }
                String chartLabel = simplifyCompareLabel(label, row.fileNameProperty().get());
                wordsSeries.getData().add(new XYChart.Data<>(chartLabel, parseCount(row.wordsProperty().get())));
                uniqueSeries.getData().add(new XYChart.Data<>(chartLabel, parseCount(row.uniqueWordsProperty().get())));
                topPairSeries.getData().add(new XYChart.Data<>(chartLabel, parseCount(row.topPairCountProperty().get())));
            }
            fileCompareSubtitle.setText("Comparing " + compareLabels.size()
                    + " selected file(s). Use \"Compare files\" to change selection.");
        } else {
            TxtFileSummary meta = fileByComboLabel.get(scopeKey);
            String label = meta != null ? meta.txtName() : (scopeKey == null ? "Selected scope" : scopeKey);
            wordsSeries.getData().add(new XYChart.Data<>(label, parseCount(chipTotalTokens.getText())));
            uniqueSeries.getData().add(new XYChart.Data<>(label, parseCount(chipUniqueWords.getText())));
            topPairSeries.getData().add(new XYChart.Data<>(label, parseCount(chipTopCombo.getText())));
            fileCompareSubtitle.setText("Selected file only. Choose \"" + ALL_SOURCES + "\" for side-by-side comparison.");
        }

        fileCompareChart.getData().add(wordsSeries);
        fileCompareChart.getData().add(uniqueSeries);
        fileCompareChart.getData().add(topPairSeries);
        decorateSeries(wordsSeries, "Words", themeHex("accent"));
        decorateSeries(uniqueSeries, "Unique words", themeHex("violet"));
        decorateSeries(topPairSeries, "Top pair count", themeHex("teal"));
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns a short display label for a chart axis by using the file name if available,
     *      or stripping the import timestamp suffix from the combo label.
     *
     * @param comboLabel the full combo label including the import timestamp
     * @param fallbackFileName the file name to prefer if non-blank
     * @return a shortened label suitable for chart axes
     */
    private static String simplifyCompareLabel(String comboLabel, String fallbackFileName) {
        if (fallbackFileName != null && !fallbackFileName.isBlank()) {
            return fallbackFileName;
        }
        int idx = comboLabel.indexOf(" · ");
        return idx > 0 ? comboLabel.substring(0, idx) : comboLabel;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns a hex color string for the given role ("accent", "violet", "teal") appropriate
     *      for the currently active theme palette, ensuring readable bar colors in each theme.
     *
     * @param role the semantic color role to resolve
     * @return hex color string for the active theme and role
     */
    private String themeHex(String role) {
        return switch (UiPreferences.get().resolvedPaletteClass()) {
            case "theme-light", "theme-system" -> switch (role) {
                case "violet" -> "#4f46e5"; case "teal" -> "#0f766e"; default -> "#1d4ed8";
            };
            case "theme-christmas" -> switch (role) {
                case "violet" -> "#c084fc"; case "teal" -> "#2dd4bf"; default -> "#6A161C";
            };
            case "theme-thanksgiving" -> switch (role) {
                case "violet" -> "#6B7050"; case "teal" -> "#6B7050"; default -> "#B34E22";
            };
            case "theme-rainbow" -> switch (role) {
                case "violet" -> "#22d3ee"; case "teal" -> "#4ade80"; default -> "#f472b6";
            };
            case "theme-luna" -> switch (role) {
                case "violet" -> "#9a8cff"; case "teal" -> "#77e4f2"; default -> "#54acbf";
            };
            case "theme-neon-noir" -> switch (role) {
                case "violet" -> "#411e3a"; case "teal" -> "#17364f"; default -> "#09dbc7";
            };
            case "theme-fairy-lights" -> switch (role) {
                case "violet" -> "#9f99d1"; case "teal" -> "#9fd6cf"; default -> "#86bada";
            };
            case "theme-deep-ink" -> switch (role) {
                case "violet" -> "#c68d5d"; case "teal" -> "#7baacb"; default -> "#27606c";
            };
            case "theme-emerald-light" -> switch (role) {
                case "violet" -> "#1c434a"; case "teal" -> "#6dabc3"; default -> "#057569";
            };
            case "theme-trapped-rainbow" -> switch (role) {
                case "violet" -> "#dbaad7"; case "teal" -> "#86bada"; default -> "#9f99d1";
            };
            case "theme-invert-system" -> switch (role) {
                case "violet" -> "#c084fc"; case "teal" -> "#5eead4"; default -> "#fde047";
            };
            default -> switch (role) {
                case "violet" -> "#818cf8"; case "teal" -> "#2dd4bf"; default -> "#60a5fa";
            };
        };
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Sets the inline bar-fill style and installs a hover tooltip on every bar node in the
     *      series; defers to a property listener for bars whose scene node is not yet built.
     *
     * @param series the chart series whose bar nodes to style
     * @param metric the label shown in the tooltip beside the count value
     * @param barColorHex hex color string to apply as the bar fill
     */
    private void decorateSeries(XYChart.Series<String, Number> series, String metric, String barColorHex) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            int exact = data.getYValue().intValue();
            String msg = data.getXValue()
                    + "\n" + metric + ": " + INT_FMT.format(exact)
                    + "\nExact value: " + INT_FMT.format(exact);
            Node node = data.getNode();
            if (node != null) {
                node.setStyle("-fx-bar-fill: " + barColorHex + ";");
                Tooltip.install(node, buildChartTooltip(msg));
            } else {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-bar-fill: " + barColorHex + ";");
                        Tooltip.install(newNode, buildChartTooltip(msg));
                    }
                });
            }
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Applies theme-aware inline styles to all chart axes, legend items, legend symbols, and
     *      tick labels across all three bar charts, running the legend pass after the next layout pulse.
     */
    private void applyChartTheme() {
        boolean dark = UiPreferences.get().isResolvedDarkSurface();
        String textHex = dark ? "#f8fafc" : "#111827";
        String subHex = dark ? "#d1d5db" : "#374151";
        LegendTheme legendTheme = legendThemeFor(UiPreferences.get().getTheme());
        Paint tick = Color.web(textHex);

        for (BarChart<String, Number> chart : List.of(topWordsChart, topPairsChart, fileCompareChart)) {
            if (chart == null) {
                continue;
            }
            if (chart.getXAxis() instanceof CategoryAxis x) {
                x.setTickLabelFill(tick);
            }
            if (chart.getYAxis() instanceof javafx.scene.chart.ValueAxis<?> y) {
                y.setTickLabelFill(tick);
            }
        }

        // Axis labels/legend text are internal nodes; style after CSS pass.
        Platform.runLater(() -> {
            for (BarChart<String, Number> chart : List.of(topWordsChart, topPairsChart, fileCompareChart)) {
                if (chart == null) {
                    continue;
                }
                for (Node n : chart.lookupAll(".axis-label")) {
                    n.setStyle("-fx-text-fill: " + subHex + "; -fx-fill: " + subHex + "; -fx-font-weight: bold;");
                }
                for (Node n : chart.lookupAll(".chart-legend-item")) {
                    n.setStyle("-fx-text-fill: " + legendTheme.textHex + "; -fx-font-size: 0.98em; "
                            + "-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, "
                            + legendTheme.textShadowHex + ", 1.5, 0.35, 0, 1); -fx-opacity: 1;");
                }
                for (Node n : chart.lookupAll(".chart-legend-item .label")) {
                    n.setStyle("-fx-text-fill: " + legendTheme.textHex + "; -fx-font-weight: bold; -fx-opacity: 1;");
                }
                for (Node n : chart.lookupAll(".chart-legend-item .text")) {
                    n.setStyle("-fx-fill: " + legendTheme.textHex + ";");
                }
                for (Node n : chart.lookupAll(".tick-label")) {
                    n.setStyle("-fx-fill: " + textHex + ";");
                }
                for (Node n : chart.lookupAll(".chart-legend")) {
                    n.setStyle("-fx-background-color: " + legendTheme.backgroundHex + "; -fx-background-radius: 10; "
                            + "-fx-border-color: " + legendTheme.borderHex + "; -fx-border-radius: 10; -fx-border-width: 1; "
                            + "-fx-opacity: 1; "
                            + "-fx-padding: 6 10 6 10;");
                }

                List<Node> symbols = new ArrayList<>(chart.lookupAll(".chart-legend-item-symbol"));
                String[] palette = legendPaletteFor(chart);
                for (int i = 0; i < symbols.size(); i++) {
                    String hex = palette[Math.min(i, palette.length - 1)];
                    symbols.get(i).setStyle("-fx-background-color: " + hex + ", " + hex + ";");
                }
            }
        });
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the array of theme hex colors to apply to the legend symbols for the given
     *      bar chart, matched by the chart's fx:id.
     *
     * @param chart the bar chart whose legend symbol palette is needed
     * @return ordered hex color array matching the chart's series count
     */
    private String[] legendPaletteFor(BarChart<String, Number> chart) {
        if (chart == null || chart.getId() == null) {
            return new String[] {themeHex("accent"), themeHex("violet"), themeHex("teal")};
        }
        return switch (chart.getId()) {
            case "topWordsChart"   -> new String[] {themeHex("accent")};
            case "topPairsChart"   -> new String[] {themeHex("violet")};
            case "fileCompareChart" -> new String[] {themeHex("accent"), themeHex("violet"), themeHex("teal")};
            default -> new String[] {themeHex("accent"), themeHex("violet"), themeHex("teal")};
        };
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Creates a styled dark-background tooltip for bar chart hover with short show/hide
     *      delays and rounded border styling.
     *
     * @param text the tooltip body text to display
     * @return the configured Tooltip instance
     */
    private static Tooltip buildChartTooltip(String text) {
        Tooltip t = new Tooltip(text);
        t.setShowDelay(Duration.millis(90));
        t.setHideDelay(Duration.millis(120));
        t.setStyle("-fx-background-color: rgba(15,23,42,0.96);"
                + "-fx-text-fill: #f8fafc;"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 8 10 8 10;"
                + "-fx-border-color: rgba(148,163,184,0.45);"
                + "-fx-border-radius: 8;"
                + "-fx-border-width: 1;");
        return t;
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Maps the active AppTheme to a LegendTheme holding the chart legend background,
     *      border, text, and shadow colors appropriate for dark or light palettes.
     *
     * @param theme the active AppTheme; null defaults to dark legend styling
     * @return the LegendTheme for the given theme
     */
    private static LegendTheme legendThemeFor(AppTheme theme) {
        if (theme == null) {
            return LegendTheme.dark();
        }
        return switch (theme) {
            case DEFAULT,
                    CHRISTMAS,
                    RAINBOW,
                    LUNA,
                    NEON_NOIR,
                    FAIRY_LIGHTS,
                    DEEP_INK,
                    INVERT_SYSTEM -> LegendTheme.dark();
            case LIGHT,
                    THANKSGIVING,
                    EMERALD_LIGHT,
                    TRAPPED_RAINBOW,
                    SYSTEM -> LegendTheme.light();
        };
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Holds the four chart legend style tokens (text, background, border, text shadow) as a
     *      data record, with dark() and light() factory methods for the two palette families.
     */
    private record LegendTheme(String textHex, String backgroundHex, String borderHex, String textShadowHex) {
        private static LegendTheme dark() {
            return new LegendTheme("#f8fafc", "rgba(15,23,42,0.94)", "rgba(148,163,184,0.45)", "rgba(2,6,23,0.72)");
        }

        private static LegendTheme light() {
            return new LegendTheme("#111827", "rgba(255,255,255,0.96)", "rgba(17,24,39,0.24)", "rgba(255,255,255,0.4)");
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Returns the active file comparison label list, filtered to only labels still present in
     *      fileByComboLabel. Falls back to all known file labels if the selection is empty.
     *
     * @return ordered list of combo labels to include in the file comparison chart
     */
    private List<String> getEffectiveCompareLabels() {
        if (fileByComboLabel.isEmpty()) {
            return List.of();
        }
        List<String> selected = new ArrayList<>();
        for (String label : fileByComboLabel.keySet()) {
            if (selectedCompareLabels.contains(label)) {
                selected.add(label);
            }
        }
        if (!selected.isEmpty()) {
            return selected;
        }
        return new ArrayList<>(fileByComboLabel.keySet());
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Appends a TXT-formatted section listing the effective file comparison selection with
     *      per-file word, unique word, top pair count, and sentence columns.
     *
     * @param sb the StringBuilder to append the comparison section to
     */
    private void appendCompareSelectionTxt(StringBuilder sb) {
        List<String> labels = getEffectiveCompareLabels();
        sb.append("SELECTED FILE COMPARISON\n");
        sb.append("Selection count: ").append(labels.size()).append('\n');
        sb.append("Label\tWords\tUnique Words\tTop Pair Count\tSentences\n");
        for (String label : labels) {
            FileCorpusStatRow row = fileRowByComboLabel.get(label);
            if (row == null) {
                continue;
            }
            sb.append(label).append('\t')
                    .append(row.wordsProperty().get()).append('\t')
                    .append(row.uniqueWordsProperty().get()).append('\t')
                    .append(row.topPairCountProperty().get()).append('\t')
                    .append(row.sentencesProperty().get()).append('\n');
        }
    }

    /**
     * Author: Sebastian Sarinana
     * Description:
     *      Appends a CSV-formatted section listing the effective file comparison selection with
     *      per-file word, unique word, top pair count, and sentence columns.
     *
     * @param sb the StringBuilder to append the comparison section to
     */
    private void appendCompareSelectionCsv(StringBuilder sb) {
        List<String> labels = getEffectiveCompareLabels();
        sb.append("Selected Comparison,Selection count,").append(labels.size()).append('\n');
        sb.append("Selected Comparison,Label,Words,Unique Words,Top Pair Count,Sentences\n");
        for (String label : labels) {
            FileCorpusStatRow row = fileRowByComboLabel.get(label);
            if (row == null) {
                continue;
            }
            sb.append(csvLine("Selected Comparison",
                    label,
                    row.wordsProperty().get(),
                    row.uniqueWordsProperty().get(),
                    row.topPairCountProperty().get(),
                    row.sentencesProperty().get()));
        }
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Stores the scope hint text used in TXT/CSV export output to describe the active data context.
     *
     * @param text the hint string to store; null is treated as an empty string
     */
    private void setScopeHint(String text) {
        // Kept for export context now that the textbox is removed from the UI.
        this.lastScopeHint = text == null ? "" : text;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Returns the stored scope hint string, or an empty string if none has been set.
     *
     * @return current scope hint text, never null
     */
    private String getScopeHintText() {
        return lastScopeHint == null ? "" : lastScopeHint;
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Parses a locale-formatted number string (e.g. "1,234") to an integer, stripping all
     *      non-digit characters. Returns 0 for null, blank, or dash values.
     *
     * @param raw the formatted count string from a table cell or chip label
     * @return parsed integer count, or 0 if the input is not a valid number
     */
    private static int parseCount(String raw) {
        if (raw == null || raw.isBlank() || "—".equals(raw)) {
            return 0;
        }
        try {
            return Integer.parseInt(raw.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Author: Huy Nong
     * Description:
     *      Formats a SQL Timestamp as a human-readable import date string using the local time zone.
     *      Returns "—" if the timestamp is null.
     *
     * @param ts the import timestamp from the database record
     * @return formatted date string or "—" if null
     */
    private static String formatImported(java.sql.Timestamp ts) {
        if (ts == null) {
            return "—";
        }
        LocalDateTime ldt = LocalDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
        return ldt.format(IMPORT_FMT);
    }
}
