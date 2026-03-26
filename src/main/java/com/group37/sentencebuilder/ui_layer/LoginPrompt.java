/**
 * File: LoginPrompt.java
 * Description: 
 *
 * Author: Cortland KImzey
 * Created: 2026-03-25
 * Last Modified: 2026-03-25
 *
 * Version: 1.0
 */

package com.group37.sentencebuilder.ui_layer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

public class LoginPrompt {

    private static final String CONFIG_FILE = "data/db_config.txt";

    public static String[] show() {

        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Database Login");

        Label userLabel = new Label("Username:");
        TextField username = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField password = new PasswordField();

        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String savedUser = reader.readLine();
                String savedPass = reader.readLine();

                if (savedUser != null) username.setText(savedUser);
                if (savedPass != null) password.setText(savedPass);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(userLabel, 0, 0);
        grid.add(username, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(password, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String user = username.getText().trim();
                String pass = password.getText().trim();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
                    writer.write(user);
                    writer.newLine();
                    writer.write(pass);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return new String[]{user, pass};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static boolean isCredentials()
    {
        File file = new File(CONFIG_FILE);
        if (!file.exists())
            return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String user = reader.readLine();
            String pass = reader.readLine();
            
            if (user != null && !user.trim().isEmpty() &&
                pass != null && !pass.trim().isEmpty()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}