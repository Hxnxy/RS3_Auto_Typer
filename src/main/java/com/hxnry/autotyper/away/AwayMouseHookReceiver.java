package com.hxnry.autotyper.away;

import com.hxnry.autotyper.util.Clock;
import com.hxnry.autotyper.util.Random;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import me.coley.simplejna.hook.mouse.MouseEventReceiver;
import me.coley.simplejna.hook.mouse.MouseHookManager;
import me.coley.simplejna.hook.mouse.struct.MouseButtonType;

import java.awt.*;

public class AwayMouseHookReceiver extends MouseEventReceiver {

    String name = "";
    public long lastPressed = -1;
    public long nextPress = Random.high(1000, 240000);
    private int pid;

    public long getTimePassed() {
        return System.currentTimeMillis() - lastPressed;
    }

    public AwayMouseHookReceiver(MouseHookManager hookManager) {
        super(hookManager);
    }

    @Override
    public boolean onMousePress(MouseButtonType type, WinDef.HWND hwnd, WinDef.POINT info) {

        if(this.pid == -1) return false;

        WinDef.HWND window = User32.INSTANCE.GetForegroundWindow();
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(window, rect);
        Rectangle bounds = rect.toRectangle();

        int processId = User32.INSTANCE.GetWindowThreadProcessId(window, null);

        if(processId == this.pid && bounds.contains(info.x, info.y)) {
            System.out.println("pressed " + type.name() + " on process: " + processId + " @ x:" + info.x + ", y:" + info.y  + "\n" +
                    (lastPressed == -1 ? "Just now." : "Last input: " + Clock.formatTime(System.currentTimeMillis() - lastPressed)) + " ago.");
            lastPressed = System.currentTimeMillis();
            long next = Random.nextInt(1000, 240000);
            nextPress = next;
        }
        return false;
    }

    @Override
    public boolean onMouseRelease(MouseButtonType type, WinDef.HWND hwnd, WinDef.POINT info) {
        return false;
    }

    @Override
    public boolean onMouseScroll(boolean down, WinDef.HWND hwnd, WinDef.POINT info) {
        return false;
    }

    @Override
    public boolean onMouseMove(WinDef.HWND hwnd, WinDef.POINT info) {
        return false;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
