/**
 * File: 
 * Description: 
 *
 * Author: 
 * Created: 
 * Last Modified: 2026-03-24
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.ui_layer.model.ImportHistoryRow;
import com.group37.sentencebuilder.data_layer.TxtFileReader;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.concurrent.Task;

import java.io.File;

/** Import screen UI — file picker and progress are visual stubs only. */
public class ImportController {

    @FXML
    private Label selectedFileLabel;

    @FXML
    private ProgressBar importProgress;

    @FXML
    private Label importStatusLabel;

    @FXML
    private TableView<ImportHistoryRow> importTable;

    @FXML
    private TableColumn<ImportHistoryRow, String> colFile;

    @FXML
    private TableColumn<ImportHistoryRow, String> colDate;

    @FXML
    private TableColumn<ImportHistoryRow, String> colSentences;

    @FXML
    private TableColumn<ImportHistoryRow, String> colWords;

    @FXML
    private TableColumn<ImportHistoryRow, String> colStatus;

    private File stagedFile;

    @FXML
    private void initialize() {
        colFile.setCellValueFactory(c -> c.getValue().fileNameProperty());
        colDate.setCellValueFactory(c -> c.getValue().importedAtProperty());
        colSentences.setCellValueFactory(c -> c.getValue().sentencesProperty());
        colWords.setCellValueFactory(c -> c.getValue().wordsProperty());
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());

        ObservableList<ImportHistoryRow> rows = FXCollections.observableArrayList(
                new ImportHistoryRow("sample_corpus.txt", "Mar 18, 2026 · 2:14 PM", "12,480", "89,204", "Complete"),
                new ImportHistoryRow("notes_draft.txt", "Mar 17, 2026 · 9:02 AM", "842", "6,110", "Complete"),
                new ImportHistoryRow("lecture_transcript.txt", "Mar 15, 2026 · 4:51 PM", "3,201", "24,883", "Complete")
        );
        importTable.setItems(rows);
        importProgress.setProgress(0);
        importStatusLabel.setText("No import running.");
    }

    @FXML
    private void onChooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a text file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        Window window = selectedFileLabel.getScene().getWindow();
        File file = chooser.showOpenDialog(window);
        if (file != null) {
            stagedFile = file;
            selectedFileLabel.setText(file.getName());
            importStatusLabel.setText("File selected — ready to import (UI demo).");
        }
    }

    @FXML
    private void onImport() {
        if (stagedFile == null) {
            importStatusLabel.setText("Choose a file first.");
            return;
        }

        importStatusLabel.setText("Importing...");
        importProgress.setProgress(0);

        TxtFileReader fileReader = new TxtFileReader(stagedFile);

        Task<Void> task = fileReader.createTask();

        importProgress.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> importStatusLabel.setText("Import complete!"));
        task.setOnFailed(e -> importStatusLabel.setText("Import failed."));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}