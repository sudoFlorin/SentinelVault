package com.sentinel;

import javax.crypto.SecretKey;
import java.security.SecureRandom;

public class Main {
    public static void main(String[] args) {
        try {
            // default user data
            String masterPassword = "SuperPassword123";
            String secretSitePassword = "LloydsPassword123";

            // generate random Salt & IV
            byte[] salt = new byte[16];
            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            random.nextBytes(iv);

            System.out.println("--- SentinelVault Encryption Test ---");
            System.out.println("Original Password: " + secretSitePassword);

            // derive key from master pw
            SecretKey key = CryptoEngine.deriveKey(masterPassword, salt);

            // encryption
            String encryptedData = CryptoEngine.encrypt(secretSitePassword, key, iv);
            System.out.println("Encrypted (Base64): " + encryptedData);

            // decryption
            String decryptedData = CryptoEngine.decrypt(encryptedData, key, iv);
            System.out.println("Decrypted Result: " + decryptedData);

            // verify
            if (secretSitePassword.equals(decryptedData)) {
                System.out.println("\nSUCCESS: Encryption/Decryption cycle completed");
            }
        } catch (Exception e) {
            System.err.println("Error during crypto test: " + e.getMessage());
            e.printStackTrace();


        }
    }
}
