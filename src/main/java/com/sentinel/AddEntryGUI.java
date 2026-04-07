package com.sentinel;

import com.sentinel.core.CryptoEngine;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import com.sentinel.core.PasswordGenerator;
import com.sentinel.utils.ClipboardManager;
import javafx.geometry.Pos;
import com.sentinel.core.StrengthEngine;
import javafx.scene.control.ProgressBar;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKey;

public class AddEntryGUI extends Application {

    private VaultTableGUI vaultUI;

    private String initialMasterPassword = "";


    public void setMasterPassword(String pass) {
        this.initialMasterPassword = pass;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SentinelVault - Add New Entry");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setVgap(10);
        grid.setHgap(10);

        // UI
        PasswordField masterPassField = new PasswordField();
        masterPassField.setPromptText("Master Password");

        // password auto fill
        masterPassField.setText(initialMasterPassword);

        TextField siteField = new TextField();
        siteField.setPromptText("e.g. Netflix");

        TextField userField = new TextField();
        userField.setPromptText("Username");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button saveButton = new Button("Save to Vault");
        Button genButton = new Button("Generate Password");
        Button copyButton = new Button("Copy to Clipboard");
        Button viewVaultBtn = new Button("View Stored Passwords");

        // view vault button
        viewVaultBtn.setOnAction(e -> {
            if (vaultUI == null) {
                vaultUI = new VaultTableGUI();
            }
            vaultUI.display(masterPassField.getText());
        });

        // button for generating password
        genButton.setOnAction(e -> {
            String securePass = PasswordGenerator.generate(16);
            passField.setText(securePass);
        });

        // copy on clipboard action
        copyButton.setOnAction(e -> {
            if (!passField.getText().isEmpty()) {
                ClipboardManager.copyAndClear(passField.getText());
            }
        });

        // strenght meter
        ProgressBar strengthBar = new ProgressBar(0);
        strengthBar.setPrefWidth(200);
        Label strengthLabel = new Label("Strength: N/A");

        passField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                strengthBar.setProgress(0);
                strengthBar.setStyle("");
                strengthLabel.setText("Strength: N/A");
                return;
            }

            double strength = StrengthEngine.calculateStrength(newValue);
            strengthBar.setProgress(strength);
            strengthBar.setStyle(StrengthEngine.getColor(strength));

            if (strength <= 0.3) strengthLabel.setText("Weak");
            else if (strength <= 0.6) strengthLabel.setText("Medium");
            else strengthLabel.setText("Strong");
        });

        // action for the save button
        saveButton.setOnAction(e -> {
            try {
                String actualMaster = masterPassField.getText();

                saveData(actualMaster, siteField.getText(),
                        userField.getText(), passField.getText());

                if (vaultUI != null) {
                    vaultUI.refresh();
                }

                new Alert(Alert.AlertType.INFORMATION, "Entry has been successfully saved").show();
                siteField.clear();
                userField.clear();
                passField.clear();

            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
            }
        });

        // placement of layout
        grid.add(new Label("Master Key:"), 0, 0);
        grid.add(masterPassField, 1, 0);
        grid.add(new Label("Site Name:"), 0, 1);
        grid.add(siteField, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(userField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passField, 1, 3);
        grid.add(saveButton, 1, 4);
        grid.add(genButton, 1, 5);
        grid.add(copyButton, 1, 6);
        grid.add(new Label("Security Level:"), 0, 7);
        grid.add(strengthBar, 1, 7);
        grid.add(strengthLabel, 2, 7);
        grid.add(viewVaultBtn, 1, 8);

        Scene scene = new Scene(grid, 500, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveData(String master, String site, String user, String pass) throws Exception {
        byte[] salt = new byte[16];
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(salt);
        new SecureRandom().nextBytes(iv);

        SecretKey key = CryptoEngine.deriveKey(master, salt);
        String encrypted = CryptoEngine.encrypt(pass, key, iv);

        DatabaseManager db = new DatabaseManager();
        db.addPassword(master, site, user, encrypted,
                Base64.getEncoder().encodeToString(iv),
                Base64.getEncoder().encodeToString(salt));
    }

    public static void main(String[] args) {
        launch(args);
    }
}