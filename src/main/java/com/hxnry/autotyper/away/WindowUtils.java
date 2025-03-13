package com.hxnry.autotyper.away;

import com.hxnry.autotyper.ui.autotyper.TyperConstants;
import com.hxnry.autotyper.util.Random;
import com.hxnry.autotyper.util.User32X;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public class WindowUtils {

    public static boolean isMinimized(WinDef.HWND hwnd) {
        User32.WINDOWPLACEMENT placement = new User32.WINDOWPLACEMENT();
        if(User32.INSTANCE.GetWindowPlacement(hwnd, placement).booleanValue()) {
            return placement.showCmd == User32.SW_SHOWMINIMIZED;
        }
        return false;
    }

    public static void unminimizeWindow(WinDef.HWND hwnd) {
        User32.INSTANCE.ShowWindow(hwnd, User32.SW_RESTORE);
    }

    public static void setForegroundWindow(WinDef.HWND hwnd) {
        User32.INSTANCE.SetForegroundWindow(hwnd);
    }

    public static boolean isForeground(WinDef.HWND hwnd) {
        WinDef.HWND foreground = User32.INSTANCE.GetForegroundWindow();
        return foreground != null && !foreground.equals(hwnd);
    }
    public static boolean clickWindow(TyperConstants constants, WinDef.HWND hwnd) {

        if(hwnd == null) {
            return false;
        }

        if(isMinimized(hwnd)) {
            unminimizeWindow(hwnd);
        }

        setForegroundWindow(hwnd);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final int MOUSEEVENTF_RIGHTDOWN = 0x0008;
        final int MOUSEEVENTF_RIGHTUP = 0x0010;

        // Get the position and size of the window
        WinDef.RECT rect = new WinDef.RECT();

        if(!User32.INSTANCE.GetClientRect(hwnd, rect)) {
            System.out.println("Ooops, an error occurred, couldn't find window! ");
            return false;
        }

        // Generate random values for the x and y coordinates
        int x = Random.nextInt(rect.right - rect.left);
        int y = Random.nextInt(rect.bottom - rect.top);

        // Convert the client area coordinates to screen coordinates
        WinDef.POINT point = new WinDef.POINT();
        point.x = x;
        point.y = y;
        User32X.INSTANCE.ClientToScreen(hwnd, point);

        User32.INSTANCE.SetCursorPos(point.x, point.y);

        System.out.println("Clicking window: " + constants.getPid() + " @ x:" + x + " y:" + y);

        // Create a new INPUT structure and set the dx and dy fields to the random values
        WinUser.INPUT input = new WinUser.INPUT();
        input.type = new WinDef.DWORD(WinUser.INPUT.INPUT_MOUSE);
        input.input.setType("mi");
        input.input.mi.dx = new WinDef.LONG(point.x);
        input.input.mi.dy = new WinDef.LONG(point.y);
        input.input.mi.mouseData = new WinDef.DWORD(0);
        input.input.mi.dwFlags = new WinDef.DWORD(MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_RIGHTUP);
        input.input.mi.time = new WinDef.DWORD(0);
        int cbSize = input.size();

        // Simulate a mouse click
        User32.INSTANCE.SendInput(new WinDef.DWORD(1), new WinUser.INPUT[] {input}, cbSize);
        return true;
    }
}
