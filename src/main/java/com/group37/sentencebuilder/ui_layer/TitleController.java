/**
 * ------------------------------------------------------------
 *  Project: Sentence Builder
 *  File:    TitleController.java
 *  Author:  Cortland Kimzey, Amrita Thapa
 *
 *  Description:
 *      Title/login screen controller. Connects to the database, optionally preloads defaults, then launches the main shell.
 *
 *  Version: 1.0
 *  Created: 2026-03-26
 *  Last Modified: 2026-05-06
 *
 *  Responsibilities:
 *      - Validate DB configuration and handle login/auto-login flow
 *      - Trigger default-corpus preload when needed, then transition to the main shell
 * ------------------------------------------------------------
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;
import com.group37.sentencebuilder.data_layer.DefaultDataLoader;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TitleController {

    /**
     * When true, the next FXML load shows the login panel instead of auto-entering the main shell
     * (used after logout while Database.canConnect() is still true).
     */
    private static volatile boolean nextShowLoginOnly;

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Sets a one-shot flag so the next FXML load shows the login panel even when
     *      the database is already configured (used after logout).
     *
     * @param value true to force the login panel on next load
     */
    public static void setNextShowLoginOnly(boolean value) {
        nextShowLoginOnly = value;
    }

    @FXML private Label titleLabel;
    @FXML private VBox loginPane;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private Runnable onLoginSuccess;

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Registers the callback to invoke on the JavaFX thread after a successful login
     *      or auto-connect sequence completes.
     *
     * @param r runnable to execute when the login or auto-connect flow finishes
     */
    public void setOnLoginSuccess(Runnable r)
    {
        this.onLoginSuccess = r;
    }

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Decides whether to auto-launch into the main shell or show the login form
     *      based on whether the database is reachable and the loginOnly flag.
     */
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

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Animation Vibe Coded with Copilot.
     *      Plays the title fade-in, a brief pause, a slide upward, and the login panel
     *      fade-in as a sequential animation shown when DB credentials are not yet configured.
     */
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

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Animation Vibe Coded with Copilot.
     *      Plays the title fade-in, a brief pause, and a slide-off animation when the
     *      database is already reachable, then invokes the preload-and-launch flow on completion.
     */
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

    /**
     * Author: Cortland Kimzey
     * Description:
     *      Validates the credentials entered in the login form against the database and
     *      triggers the preload-and-launch flow on success, or shows an error message on failure.
     */
    @FXML
    private void onLogin()
    {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (Database.canConfigConnect(user, pass)) {
            runPreloadThenLaunch();
        } else {
            statusLabel.setText(
                    "Could not connect to MySQL. Ensure the server is running on localhost:3306, "
                            + "the SentenceBuilder database exists (see README / SentenceBuilderDatabase.sql), "
                            + "and username/password match your MySQL account.");
        }
    }

    /**
     * Author: Amrita Thapa
     * Description:
     *      Ensures the schema exists, then checks if the database is empty. If empty, runs
     *      all default-file preload tasks on a background thread, then fires onLoginSuccess
     *      on the JavaFX thread when done.
     */
    private void runPreloadThenLaunch() {
        Thread thread = new Thread(() -> {

            // Always check and fix schema first before anything else
            DefaultDataLoader.ensureSchema(Database.getDatabase());

            boolean isEmpty = DefaultDataLoader.isDatabaseEmpty(
                    Database.getDatabase());

            if (!isEmpty) {
                Platform.runLater(() -> {
                    if (onLoginSuccess != null) onLoginSuccess.run();
                });
                return;
            }

            Platform.runLater(() -> statusLabel.setText("Setting up for first time use..."));

            boolean success = DefaultDataLoader.executeDump(Database.getDatabase());

            if (!success) {
                System.err.println("[DefaultDataLoader] Dump failed, launching anyway.");
            }

            Platform.runLater(() -> {
                statusLabel.setText("Done!");
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            });
        });

        thread.setDaemon(true);
        thread.start();
    }
}
