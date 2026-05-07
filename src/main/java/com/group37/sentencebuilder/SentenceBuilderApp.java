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

import com.group37.sentencebuilder.ui_layer.theming.UiPreferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.group37.sentencebuilder.ui_layer.MainShellController;
import com.group37.sentencebuilder.ui_layer.TitleController;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX entry point for the Sentence Builder desktop shell (UI only).
 */
public class SentenceBuilderApp extends Application {

    private static final double MIN_WIDTH = 1024;
    private static final double MIN_HEIGHT = 640;

    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Sentence Builder");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.initStyle(StageStyle.DECORATED);

        showTitleView(stage, false);
        stage.show();
    }

    /**
     * @param afterLogout if true, skip auto-launch when the DB is reachable so the user sees login again
     */
    /**
     * Author: 
     * Description: 
     *      <description>
     * 
     * @param input description
     * @return result description
     */
    private void showTitleView(Stage stage, boolean afterLogout) throws IOException {
        if (afterLogout) {
            TitleController.setNextShowLoginOnly(true);
        }
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
                Objects.requireNonNull(getClass().getResource("/css/theme-palettes.css")).toExternalForm());
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/app-theme.css")).toExternalForm());

        /* After stylesheets exist so theme classes + #sb-shell-root rules resolve on first paint. */
        UiPreferences.get().attachShell(root);

        stage.setScene(scene);
    }

    private void loadMainShell(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/fxml/MainShell.fxml")));
        Parent root = loader.load();

        MainShellController shell = loader.getController();
        shell.setOnLogout(() -> {
            try {
                showTitleView(stage, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/theme-palettes.css")).toExternalForm());
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/app-theme.css")).toExternalForm());

        UiPreferences.get().attachShell(root);

        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
