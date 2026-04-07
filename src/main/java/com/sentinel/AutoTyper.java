package com.sentinel;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class AutoTyper {

    public void typeCredentials(String username, String password) {
        try {
            Robot robot = new Robot();

            // small delay to let user switch windows in case auto-recognition not being used
            System.out.println("Release keys NOW...");
            robot.delay(1000);

            // safety reset - force releasing ctrl + alt
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.delay(100);

            // username typing
            typeString(robot, username);


            // press tab to move to password field
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(500);

            // type password
            typeString(robot, password);

            // press enter
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void typeString(Robot robot, String text) {
        for (char c : text.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);

            // Skip if keycode is not found
            if (keyCode == KeyEvent.VK_UNDEFINED) continue;

            // Check if SHIFT key is required for special chars
            boolean shiftNeeded = Character.isUpperCase(c) || "!@#$%^&*()_+:\"<>?".indexOf(c) != -1;

            if (shiftNeeded) {
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.delay(70); // Tiny pause to ensure Shift is registered
            }

            try {
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
            } catch (IllegalArgumentException e) {
                System.err.println("Cannot type character: " + c);
            }

            if (shiftNeeded) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.delay(10);
            }

            // to increase this in case double lette issue reappears
            robot.delay(30);
        }
    }
        }
