package com.sentinel;

import com.sentinel.core.CryptoEngine;
import com.sentinel.model.PasswordEntry;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Base64;
import javax.crypto.SecretKey;

public class DatabaseManager {

    // AES encryption for db file
    private static final String URL = "jdbc:h2:./vault;CIPHER=AES;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static Connection connection;


    public static void initialize(String masterPass) throws Exception {
        // trying to connect. H2 throws exception if password is incorrect
        try (Connection conn = getConnection(masterPass)) {
            // if connection is successfully, make sure the table exists
            initializeDatabase(conn);
        }
        System.out.println("[Database] Initialization and password verification successful.");
    }

    public static Connection getConnection(String masterPassword) throws SQLException {
        if (connection == null || connection.isClosed()) {
            // masterPassword used for both the file encryption and the user login
            String dbPassword = masterPassword + " " + masterPassword;
            connection = DriverManager.getConnection(URL, USER, dbPassword);
        }
        return connection;
    }


    private static void initializeDatabase(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS passwords (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "site_name VARCHAR(255), " +
                "username VARCHAR(255), " +
                "encrypted_password VARCHAR(MAX), " +
                "iv VARCHAR(255), " +
                "salt VARCHAR(255))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<PasswordEntry> getAllEntries(String masterPass) {
        List<PasswordEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM passwords";
        try (Connection conn = getConnection(masterPass);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                entries.add(new PasswordEntry(
                        rs.getString("site_name"),
                        rs.getString("username"),
                        rs.getString("encrypted_password"),
                        rs.getString("iv"),
                        rs.getString("salt")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    public void addPassword(String masterPass, String site, String user, String encryptedPass, String ivBase64, String saltBase64) {
        String query = "INSERT INTO passwords (site_name, username, encrypted_password, iv, salt) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(masterPass);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, site);
            pstmt.setString(2, user);
            pstmt.setString(3, encryptedPass);
            pstmt.setString(4, ivBase64);
            pstmt.setString(5, saltBase64);
            pstmt.executeUpdate();
            System.out.println("Success: Saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteEntry(String masterPass, String site, String user) {
        String sql = "DELETE FROM passwords WHERE site_name = ? AND username = ?";
        try (Connection conn = getConnection(masterPass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, site);
            pstmt.setString(2, user);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveAndAutoType(String masterPass, String site) {
        String query = "SELECT * FROM passwords WHERE site_name = ?";
        try (Connection conn = getConnection(masterPass);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, site);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String encryptedBase64 = rs.getString("encrypted_password");
                String ivBase64 = rs.getString("iv");
                String saltBase64 = rs.getString("salt");
                String username = rs.getString("username");

                byte[] iv = Base64.getDecoder().decode(ivBase64);
                byte[] salt = Base64.getDecoder().decode(saltBase64);

                SecretKey key = CryptoEngine.deriveKey(masterPass, salt);
                String decryptedPassword = CryptoEngine.decrypt(encryptedBase64, key, iv);

                System.out.println("Auto-typing for: " + username);
                //existing auto type logic called here
                com.sentinel.core.AutoTypeEngine.type(username, decryptedPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchAndAutoType(String masterPass, String windowTitle) {
        String query = "SELECT site_name FROM passwords";
        try (Connection conn = getConnection(masterPass);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String site = rs.getString("site_name");
                if (windowTitle.toLowerCase().contains(site.toLowerCase())) {
                    retrieveAndAutoType(masterPass, site);
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}