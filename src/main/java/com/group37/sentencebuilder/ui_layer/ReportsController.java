package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui.DarkSurfaceText;
import com.group37.sentencebuilder.ui.UiPreferences;

import com.group37.sentencebuilder.ui_layer.model.ReportRow;

import javafx.application.Platform;
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
public class ReportsController implements ApplicationPage {

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
        algorithmFilter.getItems().setAll("All algorithms", "Markov chain (bigram)", "Weighted next-word");
        algorithmFilter.getSelectionModel().selectFirst();

        dateFilter.getItems().setAll("Any time", "Last 7 days", "Last 30 days");
        dateFilter.getSelectionModel().selectFirst();

        master = FXCollections.observableArrayList(
                new ReportRow("1042", "Markov chain (bigram)", "Mar 20, 2026 · 11:02 AM",
                        "The library waited, patient and full of quiet promises…"),
                new ReportRow("1041", "Weighted next-word", "Mar 19, 2026 · 6:45 PM",
                        "She opened the notebook and the sentence began on its own…"),
                new ReportRow("1040", "Markov chain (bigram)", "Mar 18, 2026 · 8:12 AM",
                        "Words gathered like rain along the edge of the paragraph…"),
                new ReportRow("1039", "Weighted next-word", "Mar 17, 2026 · 3:30 PM",
                        "Tomorrow’s draft leaned against today’s doubts…")
        );

        FilteredList<ReportRow> filtered = new FilteredList<>(master, r -> true);
        algorithmFilter.valueProperty().addListener((o, oldV, newV) -> updateFilter(filtered));
        dateFilter.valueProperty().addListener((o, oldV, newV) -> updateFilter(filtered));

        SortedList<ReportRow> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(reportTable.comparatorProperty());
        reportTable.setItems(sorted);

        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colAlgorithm.setCellValueFactory(c -> c.getValue().algorithmProperty());
        colWhen.setCellValueFactory(c -> c.getValue().generatedAtProperty());
        colPreview.setCellValueFactory(c -> c.getValue().previewProperty());

        applyRoundedClip(reportTable, 12);
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
            // Date filter applied client-side to the rows currently in the table
            return true;
        });
    }

    @Override
    public void onPageEnter() {
        applyReportsDarkChrome();
    }

    @Override
    public void onPageLeave() {
    }

    private void applyReportsDarkChrome() {
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
}
