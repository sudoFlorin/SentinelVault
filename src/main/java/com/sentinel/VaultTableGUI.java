package com.sentinel;

import com.sentinel.core.AutoTypeEngine;
import com.sentinel.model.PasswordEntry;
import com.sentinel.utils.ClipboardManager;
import com.sentinel.core.CryptoEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList; // MUST add this import
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.crypto.SecretKey;
import java.util.Base64;

public class VaultTableGUI {
    private TableView<PasswordEntry> table = new TableView<>();
    private String masterPass;
    private boolean columnsInitialized = false;

    public void display(String masterPass) {
        this.masterPass = masterPass;

        if (!columnsInitialized) {
            setupColumns();
            columnsInitialized = true;
        }

        Stage stage = new Stage();
        stage.setTitle("Sentinel Vault - Stored Credentials");

        // search field for pw vault
        TextField searchField = new TextField();
        searchField.setPromptText("Search by site name or username...");

        // filtering logic for searching pws
        ObservableList<PasswordEntry> masterData = loadData();
        FilteredList<PasswordEntry> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(entry -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();

                if (entry.getSiteName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (entry.getUsername().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        table.setItems(filteredData);

        // search bar layout box
        VBox layout = new VBox(10, new Label("Search Vault:"), searchField, table);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 650, 450);
        stage.setScene(scene);
        stage.show();
    }

    private void setupColumns() {
        TableColumn<PasswordEntry, String> siteCol = new TableColumn<>("Site");
        siteCol.setCellValueFactory(new PropertyValueFactory<>("siteName"));

        TableColumn<PasswordEntry, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<PasswordEntry, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button copyBtn = new Button("Copy");
            private final Button autoBtn = new Button("Auto-Fill");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(5, copyBtn, autoBtn, deleteBtn);

            {
                autoBtn.setOnAction(event -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    handleAutoType(entry);
                });

                copyBtn.setOnAction(event -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    decryptAndCopy(entry);
                });

                deleteBtn.setStyle("-fx-text-fill: red;");
                deleteBtn.setOnAction(event -> {
                    PasswordEntry entry = getTableView().getItems().get(getIndex());
                    handleDelete(entry);
                });
                pane.setPadding(new Insets(2));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(siteCol, userCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void refresh() {
        ObservableList<PasswordEntry> freshData = loadData();
        FilteredList<PasswordEntry> filteredData = new FilteredList<>(freshData, p -> true);
        table.setItems(filteredData);
        table.refresh();
        System.out.println("[Vault] Table refreshed and search index updated.");
    }

    private ObservableList<PasswordEntry> loadData() {
        return FXCollections.observableArrayList(DatabaseManager.getAllEntries(masterPass));
    }

    private void handleDelete(PasswordEntry entry) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + entry.getSiteName() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                DatabaseManager.deleteEntry(masterPass, entry.getSiteName(), entry.getUsername());
                refresh();
            }
        });
    }

    private void handleAutoType(PasswordEntry entry) {
        try {
            byte[] salt = Base64.getDecoder().decode(entry.getSalt());
            byte[] iv = Base64.getDecoder().decode(entry.getIv());
            SecretKey key = CryptoEngine.deriveKey(masterPass, salt);
            String plainPass = CryptoEngine.decrypt(entry.getEncryptedPassword(), key, iv);

            AutoTypeEngine.type(entry.getUsername(), plainPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decryptAndCopy(PasswordEntry entry) {
        try {
            byte[] salt = Base64.getDecoder().decode(entry.getSalt());
            byte[] iv = Base64.getDecoder().decode(entry.getIv());
            SecretKey key = CryptoEngine.deriveKey(masterPass, salt);
            String plainPass = CryptoEngine.decrypt(entry.getEncryptedPassword(), key, iv);

            ClipboardManager.copyAndClear(plainPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}