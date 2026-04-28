/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    ReportsController.java
 *  Author:  Cortland Kimzey
 *
 *  Description:
 *      <description>
 *
 *  Version: 1.0
 *  Created: 2026-03-26
 *  Last Modified: 2026-03-24
 *
 *  Responsibilities:
 *      - <responsibilities 1>
 *      - <responsibilities 2>
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui.LabelThemeRegistry;

import com.group37.sentencebuilder.ui_layer.model.ReportRow;

import com.group37.sentencebuilder.data_layer.Database;
import com.group37.sentencebuilder.ui_layer.ApplicationPage;
import com.group37.sentencebuilder.ui_layer.DatabasePage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/** Reports table UI with client-side filters. */
public class ReportsController implements ApplicationPage, DatabasePage {

    private final LabelThemeRegistry labelTheme = new LabelThemeRegistry();

    private Database database;

    @FXML
    private ComboBox<String> algorithmFilter;

    @FXML
    private ComboBox<String> dateFilter;

    @FXML
    private TableView<ReportRow> reportTable;

    @FXML
    private TableColumn<ReportRow, String> colId;

    @FXML
    private TableColumn<ReportRow, String> colAlgorithm;

    @FXML
    private TableColumn<ReportRow, String> colWhen;

    @FXML
    private TableColumn<ReportRow, String> colPreview;

    @FXML
    private Label sectionEyebrowLabel;

    private ObservableList<ReportRow> master;

    @FXML
    private void initialize() {
        algorithmFilter.getItems().setAll("All algorithms", "Stochastic Markov chain", "Greedy Markov chain", "Random walk with seed", "Beam Search with Scoring");
        algorithmFilter.getSelectionModel().selectFirst();

        dateFilter.getItems().setAll("Any time", "Last 7 days", "Last 30 days");
        dateFilter.getSelectionModel().selectFirst();

        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colAlgorithm.setCellValueFactory(c -> c.getValue().algorithmProperty());
        colWhen.setCellValueFactory(c -> c.getValue().generatedAtProperty());
        colPreview.setCellValueFactory(c -> c.getValue().previewProperty());

        applyRoundedClip(reportTable, 12);

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

    private void updateFilter(FilteredList<ReportRow> filtered) {
        String algo = algorithmFilter.getSelectionModel().getSelectedItem();
        filtered.setPredicate(row -> {
            if (algo != null && !algo.equals("All algorithms") && !row.getAlgorithm().equals(algo)) {
                return false;
            }
            return true;
        });
    }

    @Override
    public void onPageEnter() {
        labelTheme.apply();
        database.connect();

        master = database.getReports();
        FilteredList<ReportRow> filtered = new FilteredList<>(master, r -> true);
        algorithmFilter.valueProperty().addListener((o, oldV, newV) -> updateFilter(filtered));
        dateFilter.valueProperty().addListener((o, oldV, newV) -> updateFilter(filtered));

        SortedList<ReportRow> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(reportTable.comparatorProperty());
        reportTable.setItems(sorted);
    }

    @Override
    public void onPageLeave() {
        database.disconnect();
    }

    @Override
    public void setDatabase(Database database)
    {
        this.database = database;
    }
}
