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

import java.io.IOException;
import java.util.Map;
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
                Objects.requireNonNull(getClass().getResource("/fxml/TitleView.fxml")));
        Parent root = loader.load();

        TitleController controller = loader.getController();

        controller.setOnLoginSuccess(() -> {
            // #region agent log
            DebugLog.agent("pre-fix", "H4", "SentenceBuilderApp:loginCallback", "start", Map.of());
            // #endregion
            try {
                loadMainShell(stage);
                // #region agent log
                DebugLog.agent("pre-fix", "H4", "SentenceBuilderApp:loginCallback", "loadMainShell done", Map.of());
                // #endregion
            } catch (IOException e) {
                // #region agent log
                DebugLog.agent("pre-fix", "H4", "SentenceBuilderApp:loginCallback", "IOException", Map.of(
                        "ex", e.getClass().getSimpleName()));
                // #endregion
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
        // #region agent log
        DebugLog.agent("pre-fix", "H3", "SentenceBuilderApp:loadMainShell", "enter", Map.of());
        // #endregion
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/fxml/MainShell.fxml"))
        );
        Parent root = loader.load();
        // #region agent log
        DebugLog.agent("pre-fix", "H3", "SentenceBuilderApp:loadMainShell", "fxml loaded", Map.of(
                "rootClass", root.getClass().getSimpleName()));
        // #endregion

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/app-theme.css")).toExternalForm()
        );

        stage.setScene(scene);
        // #region agent log
        DebugLog.agent("pre-fix", "H3", "SentenceBuilderApp:loadMainShell", "scene set", Map.of(
                "sceneW", String.valueOf(scene.getWidth()),
                "sceneH", String.valueOf(scene.getHeight())));
        // #endregion
    }

    public static void main(String[] args) {
        launch(args);
    }
}
