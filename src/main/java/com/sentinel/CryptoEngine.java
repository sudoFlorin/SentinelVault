package com.sentinel;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptoEngine {

    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12; // gcm standard
    private static final int TAG_LENGTH = 128;


    // master password key derivation
    public static SecretKey deriveKey(String masterPassword, byte[] salt) throws Exception {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
    return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    // encrypt data
    public static String encrypt(String strToEncrypt, SecretKey secretKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

        // decrypt data
        public static String decrypt(String strToDecrypt, SecretKey secretKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] decode = Base64.getDecoder().decode(strToDecrypt);
        return new String(cipher.doFinal(decode));
        }

    }


