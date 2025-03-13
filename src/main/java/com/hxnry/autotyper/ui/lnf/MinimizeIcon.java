package com.hxnry.autotyper.ui.lnf;

import java.awt.*;

public class MinimizeIcon extends HoverableIcon {

    private static final int SIZE = 16;

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x,y);
        g2.setPaint(isHovered ? new Color(192, 192, 192) : new Color(150, 150, 150));
        g2.setStroke(new BasicStroke(1.25F));
        g2.drawLine(4,8,12,8);
        g2.drawLine(4,9,12,9);
        g2.drawLine(4,10,12,10);
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
