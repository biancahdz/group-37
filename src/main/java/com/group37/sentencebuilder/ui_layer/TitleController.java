/**
 * File: TitleController.java
 * Description: Controls the title/login screen. On successful login,
 *              preloads the database with default text files if empty,
 *              then transitions to the main shell.
 *
 * Author: Cortland Kimzey
 * Created: 2026-03-26
 * Last Modified: 2026-03-26 - Cortland Kimzey
 *                2026-04-26 - Amrita Thapa: Added runPreloadThenLaunch()
 *  to trigger default database preloading on login if the database is empty
 *  before transitioning to the main shell.
 *
 * Version: 1.1
 */

package com.group37.sentencebuilder.ui_layer;

import java.util.List;

import com.group37.sentencebuilder.data_layer.Database;
import com.group37.sentencebuilder.data_layer.DefaultDataLoader;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TitleController {

    /**
     * When true, the next FXML load shows the login panel instead of auto-entering the main shell
     * (used after logout while {@link Database#canConnect()} is still true).
     */
    private static volatile boolean nextShowLoginOnly;

    public static void setNextShowLoginOnly(boolean value) {
        nextShowLoginOnly = value;
    }

    @FXML private Label titleLabel;
    @FXML private VBox loginPane;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private Runnable onLoginSuccess;

    public void setOnLoginSuccess(Runnable r)
    {
        this.onLoginSuccess = r;
    }

    @FXML
    private void initialize()
    {
        boolean loginOnly = nextShowLoginOnly;
        if (loginOnly) {
            nextShowLoginOnly = false;
        }

        if (Database.canConnect() && !loginOnly) {
            launchAnimation();
        } else {
            loginLaunchAnimation();
        }
    }

    private void loginLaunchAnimation()
    {
        PauseTransition pause1 = new PauseTransition(Duration.millis(600));

        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), titleLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause2 = new PauseTransition(Duration.millis(600));

        TranslateTransition slide = new TranslateTransition(Duration.millis(700), titleLabel);
        slide.setFromY(0);
        slide.setToY(-180);
        slide.setInterpolator(Interpolator.EASE_BOTH);

        FadeTransition fadeLogin = new FadeTransition(Duration.millis(500), loginPane);
        fadeLogin.setFromValue(0);
        fadeLogin.setToValue(1);

        SequentialTransition seq = new SequentialTransition(
                pause1,
                fadeIn,
                pause2,
                slide,
                fadeLogin
        );

        seq.play();
    }

    private void launchAnimation()
    {
        PauseTransition pause1 = new PauseTransition(Duration.millis(600));

        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), titleLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause2 = new PauseTransition(Duration.millis(600));

        TranslateTransition slide = new TranslateTransition(Duration.millis(830), titleLabel);
        slide.setFromY(0);
        slide.setToY(-500);
        slide.setInterpolator(Interpolator.EASE_BOTH);

        SequentialTransition seq = new SequentialTransition(
                pause1,
                fadeIn,
                pause2,
                slide
        );

        seq.setOnFinished(e -> runPreloadThenLaunch());

        seq.play();
    }

    @FXML
    private void onLogin()
    {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (Database.canConfigConnect(user, pass))
            runPreloadThenLaunch();
    }

    /**
     * Checks if the database is empty; if so, runs all default-file preload
     * tasks sequentially on a background thread, then fires onLoginSuccess
     * on the JavaFX thread when done.
     */
    private void runPreloadThenLaunch() {
        Thread thread = new Thread(() -> {

            boolean isEmpty = DefaultDataLoader.isDatabaseEmpty(
                    Database.getDatabase());

            if (!isEmpty) {
                Platform.runLater(() -> {
                    if (onLoginSuccess != null) onLoginSuccess.run();
                });
                return;
            }

            Platform.runLater(() -> statusLabel.setText("Setting up for first time use..."));

            List<Task<Void>> tasks = DefaultDataLoader.buildPreloadTasks();

            if (tasks.isEmpty()) {
                Platform.runLater(() -> {
                    if (onLoginSuccess != null) onLoginSuccess.run();
                });
                return;
            }

            for (int i = 0; i < tasks.size(); i++) {
                final int fileNum = i + 1;
                final int total = tasks.size();
                Platform.runLater(() ->
                    statusLabel.setText("Importing default files (" + fileNum + "/" + total + ")..."));
                try {
                    tasks.get(i).run();
                } catch (Exception e) {
                    System.err.println("[DefaultDataLoader] Task failed: " + e.getMessage());
                }
            }

            Platform.runLater(() -> {
                statusLabel.setText("Done!");
                if (onLoginSuccess != null) onLoginSuccess.run();
            });
        });

        thread.setDaemon(true);
        thread.start();
    }
}