package com.sentinel;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class AutoTyper {

    public void typeCredentials(String username, String password) {
        try {
            Robot robot = new Robot();

            // small delay to let user switch windows in case auto-recognition not being used
            robot.delay(2000);

            // username typing
            typeString(robot, username);

            // press tab to move to password field
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);

            // type password
            typeString(robot, password);

            // press enter
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void typeString(Robot robot, String text){
        for (char c : text.toCharArray()){
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (KeyEvent.CHAR_UNDEFINED == keyCode) {
                throw new RuntimeException("Key code not found for character: " + c);
            }

            robot.keyPress(keyCode);
            robot.delay(10); // delay for realism
            robot.keyRelease(keyCode);
        }
    }
}
