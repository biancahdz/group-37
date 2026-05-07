/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    ReportsController.java
 *  Author:  Huy Nong, Cortland Kimzey
 *
 *  Description:
 *      Reports page controller. Displays generated-sentence reports and supports client-side filtering/sorting.
 *
 *  Version: 1.0
 *  Created: 2026-03-26
 *  Last Modified: 2026-03-24
 *
 *  Responsibilities:
 *      - Populate and bind the reports TableView to {@link ReportRow} properties
 *      - Apply filter selections and keep the table sorted for quick review
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui_layer.theming.LabelThemeRegistry;

import com.group37.sentencebuilder.ui_layer.model.ReportRow;

import com.group37.sentencebuilder.data_layer.Database;

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

    /**
     * Author: Huy Nong
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
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

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
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
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    private void updateFilter(FilteredList<ReportRow> filtered) {
        String algo = algorithmFilter.getSelectionModel().getSelectedItem();
        filtered.setPredicate(row -> {
            if (algo != null && !algo.equals("All algorithms") && !row.getAlgorithm().equals(algo)) {
                return false;
            }
            return true;
        });
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
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

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    @Override
    public void onPageLeave() {
        database.disconnect();
    }

    /**
     * Author: Cortland Kimzey
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    @Override
    public void setDatabase(Database database)
    {
        this.database = database;
    }
}
