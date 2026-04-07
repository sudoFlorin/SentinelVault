package com.sentinel.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;

public class ClipboardManager {

    public static void copyAndClear(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);

        System.out.println("[Security] Password copied to clipboard. Clearing in 30 seconds...");

        // automatic clearing of clipboard
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(30),
                ae -> {
                    clipboard.clear();
                    System.out.println("[Security] Clipboard cleared automatically.");
                }
        ));
        timeline.setCycleCount(1);
        timeline.play();
    }
}