package com.sentinel.core;

public class StrengthEngine {

    public static double calculateStrength(String password) {
        if (password == null || password.isEmpty()) return 0;

        double score = 0;
        if (password.length() >= 8) score += 0.25;
        if (password.length() >= 12) score += 0.25;
        if (password.matches(".*[A-Z].*")) score += 0.15; // Has Uppercase
        if (password.matches(".*[0-9].*")) score += 0.15; // Has Number
        if (password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}].*")) score += 0.20; // Has Special Char

        return Math.min(score, 1.0); // Caps the score at 100%
    }

    public static String getColor(double score) {
        if (score <= 0.3) return "-fx-accent: red;";
        if (score <= 0.6) return "-fx-accent: orange;";
        return "-fx-accent: green;";
    }
}