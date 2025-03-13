package com.hxnry.autotyper.keybinds;


import me.coley.simplejna.hook.key.KeyEventReceiver;
import me.coley.simplejna.hook.key.KeyHookManager;

public class KeybindHookReceiver extends KeyEventReceiver {

    String name = "Keybind Hook";
    int id = -1;

    public KeybindHookReceiver(KeyHookManager hookManager) {
        super(hookManager);
        setName("Keybind Hook");
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean onKeyUpdate(SystemState sysState, PressState pressState, int time, int vkCode) {
        return false;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected int getId() {
        return this.id;
    }
}
