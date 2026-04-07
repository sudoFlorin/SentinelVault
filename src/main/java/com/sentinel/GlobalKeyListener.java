package com.sentinel;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {

    private String masterPassword;
    private DatabaseManager dbManager;

    public void setMasterPassword(String newPassword) {
        this.masterPassword = newPassword;
    }

    public GlobalKeyListener(String masterPassword) {
        this.masterPassword = masterPassword;
        this.dbManager = new DatabaseManager();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // get modifiers
        int modifiers = e.getModifiers();

        // 2. check if CTRL and ALT are pressed
        boolean isCtrlPressed = (modifiers & NativeKeyEvent.CTRL_MASK) != 0;
        boolean isAltPressed = (modifiers & NativeKeyEvent.ALT_MASK) != 0;


        if (isCtrlPressed && isAltPressed && e.getKeyCode() == NativeKeyEvent.VC_V) {
            System.out.println("\n[MATCH] Hotkey CTRL+ALT+V triggered!");

            // Get the window title
            String activeTitle = WindowUtils.getActiveWindowTitle();
            System.out.println("[Hook] Active Window Identified: " + activeTitle);

            // Run the database search
            dbManager.searchAndAutoType(masterPassword, activeTitle);
        }
    }

    private static boolean isRegistered = false;

    private static GlobalKeyListener instance;

    public static void startListening(String masterPass) {
        if (instance != null) {
            instance.setMasterPassword(masterPass);
            System.out.println("[Hook] Password updated for active listener.");
            return;
        }

        try {
            GlobalScreen.registerNativeHook();
            instance = new GlobalKeyListener(masterPass);
            GlobalScreen.addNativeKeyListener(instance);
            System.out.println("[Hook] Listener started successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // required by interface
    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}
}