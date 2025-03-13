package com.hxnry.autotyper.ui.lnf;

import javax.swing.*;

public abstract class HoverableIcon implements Icon {

    protected boolean isHovered;

    public void setHovered(boolean b) {
        this.isHovered = b;
    }

    public boolean isHovered() {
        return this.isHovered;
    }
}
