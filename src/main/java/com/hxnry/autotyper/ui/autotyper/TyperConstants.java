package com.hxnry.autotyper.ui.autotyper;

import com.hxnry.autotyper.util.Random;
import com.sun.jna.platform.win32.WinDef;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TyperConstants {

    private int uid = -1;
    private int pid = -1;
    private WinDef.HWND hwnd;
    private String message = "";
    private boolean pressEnter = true;
    private long minInterval = Random.low(20, 40);
    private long maxInterval = Random.mid(75, 200);
    private long messageDelay = Random.nextInt(4800, 6800);
    private boolean stayLoggedIn = true;
    private List<Integer> keycodes = new ArrayList<>();
}
