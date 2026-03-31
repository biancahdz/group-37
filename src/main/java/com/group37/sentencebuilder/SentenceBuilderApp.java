/**
 * File: 
 * Description: 
 *
 * Author: 
 * Created: 
 * Last Modified: 2026-03-25
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.group37.sentencebuilder.ui_layer.TitleController;
import com.group37.sentencebuilder.ui_layer.MainShellController;

import java.io.IOException;
import java.util.Objects;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * JavaFX entry point for the Sentence Builder desktop shell (UI only).
 */
public class SentenceBuilderApp extends Application {

    private static final double MIN_WIDTH = 1024;
    private static final double MIN_HEIGHT = 640;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/fxml/TitleView.fxml")));
        Parent root = loader.load();

        TitleController controller = loader.getController();

        controller.setOnLoginSuccess(() -> {
            try {
                loadMainShell(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

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

    private void loadMainShell(Stage stage) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/fxml/MainShell.fxml"))
        );
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/app-theme.css")).toExternalForm()
        );

        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
