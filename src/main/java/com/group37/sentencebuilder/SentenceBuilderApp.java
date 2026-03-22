package com.group37.sentencebuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX entry point for the Sentence Builder desktop shell (UI only).
 */
public class SentenceBuilderApp extends Application {

    private static final double MIN_WIDTH = 1024;
    private static final double MIN_HEIGHT = 640;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/fxml/MainShell.fxml")));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/app-theme.css")).toExternalForm());

        stage.setTitle("Sentence Builder");
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
