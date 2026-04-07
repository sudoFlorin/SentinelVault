package com.sentinel;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public class WindowUtils {

    public static String getActiveWindowTitle() {
        char[] buffer = new char[2048];
        // get active window handle
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        // get the title from that handle (window text name)
        User32.INSTANCE.GetWindowText(hwnd, buffer, 1024);

        return Native.toString(buffer);
    }
}