package com.hxnry.autotyper.util;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import static com.sun.jna.win32.W32APIOptions.UNICODE_OPTIONS;

public interface User32X extends User32 {

    User32X INSTANCE = Native.loadLibrary("user32", User32X.class, UNICODE_OPTIONS);


    boolean IsWindowVisible(HWND hWnd);

    boolean SetWindowText(HWND hWnd, WString lpString);

    boolean ClientToScreen(WinDef.HWND hWnd, WinDef.POINT lpPoint);
}