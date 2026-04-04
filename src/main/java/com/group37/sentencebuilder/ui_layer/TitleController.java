/**
 * File: TitleController.java
 * Description: 
 *
 * Author: Cortland Kimzey
 * Created: 2026-03-26
 * Last Modified: 2026-03-26
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.ui_layer;

import com.group37.sentencebuilder.data_layer.Database;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TitleController {

    @FXML private Label titleLabel;
    @FXML private VBox loginPane;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private Runnable onLoginSuccess;

    public void setOnLoginSuccess(Runnable r)
    {
        this.onLoginSuccess = r;
    }

    @FXML
    private void initialize()
    {
        if (Database.canConnect())
            launchAnimation();
        else
            loginLaunchAnimation();
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

        seq.setOnFinished(e -> {
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        });

        seq.play();
    }

    @FXML
    private void onLogin()
    {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (Database.canConfigConnect(user, pass))
            if (onLoginSuccess != null)
                onLoginSuccess.run();
    }
}