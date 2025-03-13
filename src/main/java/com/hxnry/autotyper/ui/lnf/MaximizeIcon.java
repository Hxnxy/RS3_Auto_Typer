package com.hxnry.autotyper.ui.lnf;

import java.awt.*;


public class MaximizeIcon extends HoverableIcon {

    private static final int SIZE = 16;

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2.translate(x, y + 4);
        g2.setPaint(isHovered ? new Color(192, 192, 192) : new Color(150, 150, 150));
        g2.setStroke(new BasicStroke(1.25F));
        g2.drawRect(x + 2, y - 2, 10, 8);
        g2.drawRect(x, y, 10, 8);
        g2.dispose();
    }

    /**
     * g2.drawOval(x - 1, y, 8, 8);
     g2.drawOval(x + 1, y, 8, 8);
     g2.drawOval(x, y, 8, 8);
     g2.drawOval(x, y, 8, 8);
     * @return
     */

    @Override
    public int getIconWidth() {
        return SIZE;
    }

    @Override
    public int getIconHeight() {
        return SIZE;
    }

}
