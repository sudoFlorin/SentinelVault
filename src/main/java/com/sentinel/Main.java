package com.sentinel;

import javafx.application.Application;


public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("[System] Initializing Sentinel Vault...");
            Application.launch(AddEntryGUI.class, args);
        } catch (Exception e) {
            System.err.println("[Critical] Failed to launch application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}