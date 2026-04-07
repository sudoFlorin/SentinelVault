package com.sentinel.core;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length) {
        if (length < 8) length = 16;

        StringBuilder password = new StringBuilder();

        for (int i =0; i < 2; i++) {
            password.append(UPPER.charAt(random.nextInt(UPPER.length())));
            password.append(LOWER.charAt(random.nextInt(LOWER.length())));
            password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
            password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        }

        for (int i = 8; i < length; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        List<Character> letters = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(letters);

        return letters.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}