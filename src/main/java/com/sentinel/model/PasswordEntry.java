package com.sentinel.model;

public class PasswordEntry {
    private String siteName;
    private String username;
    private String encryptedPassword;
    private String iv;
    private String salt;

    // constructors used to create objects from the results pulled from db
    public PasswordEntry(String siteName, String username, String encryptedPassword, String iv, String salt) {
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.iv = iv;
        this.salt = salt;
    }

    // data pulling for TableView so contents can be seen in the vault
    public String getSiteName() {
        return siteName; }
    public String getUsername() {
        return username; }
    public String getEncryptedPassword() {
        return encryptedPassword; }
    public String getIv() {
        return iv; }
    public String getSalt() {
        return salt; }
}