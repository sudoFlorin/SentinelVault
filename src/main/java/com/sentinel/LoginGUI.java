package com.sentinel;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;

public class LoginGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SentinelVault - Security Check");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        // checks if the db file already exists
        File dbFile = new File("vault.mv.db");
        boolean isFirstRun = !dbFile.exists();

        // dynamic labels that change whether we're in setup or login mode
        Label header = new Label(isFirstRun ? "Set Master Password" : "Enter Master Password");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(isFirstRun ? "Create Password" : "Master Password");
        passwordField.setMaxWidth(250);

        Button actionBtn = new Button(isFirstRun ? "Create Vault" : "Unlock Vault");
        actionBtn.setPrefWidth(250);
        actionBtn.setStyle(isFirstRun ? "-fx-background-color: #3498db; -fx-text-fill: white;"
                : "-fx-background-color: #2ecc71; -fx-text-fill: white;");

        Label statusLabel = new Label(isFirstRun ? "Choose a strong password to encrypt your data." : "");
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER);

        actionBtn.setOnAction(e -> {
            String input = passwordField.getText();
            if (input.isEmpty()) {
                statusLabel.setText("Password cannot be empty!");
                return;
            }

            try {
                // Create OR unlock database if it already exists
                DatabaseManager.initialize(input);

                AddEntryGUI mainApp = new AddEntryGUI();
                mainApp.setMasterPassword(input);
                mainApp.start(new Stage());

                primaryStage.close();
            } catch (Exception ex) {
                // fail ? incorrect password returned by H2
                statusLabel.setText("Incorrect Master Password!");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        layout.getChildren().addAll(header, passwordField, actionBtn, statusLabel);

        Scene scene = new Scene(layout, 350, 300); // Increased height slightly for the sub-label
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}