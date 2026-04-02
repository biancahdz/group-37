package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui_layer.model.ReportRow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/** Reports table UI with mock rows and client-side filter (no database). */
public class ReportsController {

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
    }

    private void updateFilter(FilteredList<ReportRow> filtered) {
        String algo = algorithmFilter.getSelectionModel().getSelectedItem();
        filtered.setPredicate(row -> {
            if (algo != null && !algo.equals("All algorithms") && !row.getAlgorithm().equals(algo)) {
                return false;
            }
            // Date filter is visual only for demo — all mock rows pass
            return true;
        });
    }
}
