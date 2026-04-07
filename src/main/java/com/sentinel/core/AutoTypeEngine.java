package com.sentinel.core;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class AutoTypeEngine {

    public static void type(String username, String password) {
        try {
            Robot robot = new Robot();

            // delay before robot kicks in
            Thread.sleep(2000);

            // username typing
            typeString(robot, username);

            // robot automatically presses tab to move to the password field
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(500);

            // password is pasted for complex passwords
            pasteString(robot, password);

            // automatically pressing enter after inputting username & pw
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void pasteString(Robot robot, String text) {
        // clipboard password copying
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        // paste
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        // clipboard clearing after password has been copied
        robot.delay(100);
        StringSelection empty = new StringSelection("");
        clipboard.setContents(empty, empty);
    }

    private static void typeString(Robot robot, String text) {
        for (char c : text.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (keyCode != KeyEvent.VK_UNDEFINED) {
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                robot.delay(30);
            }
        }
    }
}