package com.hxnry.autotyper.ui.lnf;

import java.awt.*;

public class CloseIcon extends HoverableIcon {

    private static final int SIZE = 16;

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.setPaint(isHovered ? new Color(192, 192, 192) : new Color(150, 150, 150));
        g2.drawLine(4, 4, 11, 11);
        g2.drawLine(4, 5, 10, 11);
        g2.drawLine(5, 4, 11, 10);
        g2.drawLine(11, 4, 4, 11);
        g2.drawLine(11, 5, 5, 11);
        g2.drawLine(10, 4, 4, 10);
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return SIZE;
    }

    @Override
    public int getIconHeight() {
        return SIZE;
    }
}
