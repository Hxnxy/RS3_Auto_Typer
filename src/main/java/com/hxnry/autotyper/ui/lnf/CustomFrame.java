package com.hxnry.autotyper.ui.lnf;

import com.hxnry.autotyper.Boot;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public abstract class CustomFrame extends JFrame {

    private static final int W = 4;
    private static final Color BORDER_COLOR = Color.WHITE   ;
    //private static final Color BACKGROUND_COLOR = new Color(40, 41, 45);
    private static final Color BACKGROUND_COLOR = new Color(26, 24, 21);

    private final JPanel contentPanel = new JPanel(new BorderLayout());
    private JLabel titleText;

    private int state = 0;

    public CustomFrame() {

        setUndecorated(true);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new Color(255, 255, 255));

        JPanel title = new JPanel(new BorderLayout());
        JPanel navs = new JPanel();
        navs.add(Box.createHorizontalGlue());
        navs.add(createMinimiseButton());
        //navs.add(createHelpButton());
        navs.add(createCloseButton());
        navs.setOpaque(false);

        DragAdapter dwl = new DragAdapter();
        title.addMouseListener(dwl);
        title.addMouseMotionListener(dwl);
        title.setOpaque(false);
        title.setBorder(BorderFactory.createEmptyBorder(W, W, W, W));

        titleText = new JLabel(getTitle(), SwingConstants.CENTER);
        titleText.setForeground(new Color(192, 192, 192));

        titleText.setFont(Boot.customFont);
        title.add(titleText, BorderLayout.WEST);
        //title.add(navs,BorderLayout.EAST);
        //title.add(createMinimiseButton(),BorderLayout.LINE_END);
        //title.add(createCloseButton(), BorderLayout.LINE_END);

        SideLabel left = new SideLabel(Side.W);
        SideLabel right = new SideLabel(Side.E);
        SideLabel top = new SideLabel(Side.N);
        SideLabel bottom = new SideLabel(Side.S);
        SideLabel topLeft = new SideLabel(Side.NW);
        SideLabel topRight = new SideLabel(Side.NE);
        SideLabel bottomLeft = new SideLabel(Side.SW);
        SideLabel bottomRight = new SideLabel(Side.SE);

        if(isResizable()) {
            ResizeAdapter rwl = new ResizeAdapter(this);
            for (SideLabel l : Arrays.asList(left, right, top, bottom, topLeft, topRight, bottomLeft, bottomRight)) {
                l.addMouseListener(rwl);
                l.addMouseMotionListener(rwl);
            }
        }

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(top, BorderLayout.NORTH);
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(navs, BorderLayout.EAST);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topLeft, BorderLayout.WEST);
        northPanel.add(titlePanel, BorderLayout.CENTER);
        northPanel.add(topRight, BorderLayout.EAST);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(bottomLeft, BorderLayout.WEST);
        southPanel.add(bottom, BorderLayout.CENTER);
        southPanel.add(bottomRight, BorderLayout.EAST);

        JPanel resizePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                g2.setPaint(BACKGROUND_COLOR);
                g2.fillRect(0, 0, w, h);
                g2.setPaint(BORDER_COLOR);
                g2.drawRect(0, 0, w - 1, h - 1);
                g2.dispose();
            }
        };

        resizePanel.add(left, BorderLayout.WEST);
        resizePanel.add(right, BorderLayout.EAST);
        resizePanel.add(northPanel, BorderLayout.NORTH);
        resizePanel.add(southPanel, BorderLayout.SOUTH);
        resizePanel.add(contentPanel, BorderLayout.CENTER);

        titlePanel.setOpaque(false);
        northPanel.setOpaque(false);
        southPanel.setOpaque(false);
        contentPanel.setOpaque(false);
        resizePanel.setOpaque(false);
        setContentPane(resizePanel);
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        titleText.setText(title);
    }

    @Override
    public Container getContentPane() {
        return contentPanel;
    }

    private JButton createCloseButton() {
        CloseIcon closeIcon = new CloseIcon();
        JButton button = new JButton(closeIcon);
        button.setBackground(Color.red);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(e -> {
            JComponent b = (JComponent) e.getSource();
            Container c = b.getTopLevelAncestor();
            if (c instanceof Window) {
                Window w = (Window) c;
                w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
            }
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                closeIcon.setHovered(true);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                closeIcon.setHovered(false);
            }
        });
        return button;
    }

    private JButton createHelpButton() {
        MaximizeIcon maximizeIcon = new MaximizeIcon();
        JButton button = new JButton(maximizeIcon);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(BACKGROUND_COLOR);
        button.addActionListener(e -> {
            switch (state) {
                case 0:
                    state = 1;
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                    break;
                case 1:
                    state = 0;
                    setExtendedState(JFrame.NORMAL);
                    break;
            }
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                maximizeIcon.setHovered(true);
            }
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                maximizeIcon.setHovered(false);
            }
        });
        return button;
    }

    private JButton createMinimiseButton() {
        MinimizeIcon minimizeIcon = new MinimizeIcon();
        JButton button = new JButton(minimizeIcon);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(BACKGROUND_COLOR);
        button.addActionListener(e -> {
            setState(JFrame.ICONIFIED);
            System.out.println(getState());
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                minimizeIcon.setHovered(true);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                minimizeIcon.setHovered(false);
            }
        });
        return button;
    }

    private enum Side {

        N(Cursor.N_RESIZE_CURSOR, new Dimension(0, 4)),
        W(Cursor.W_RESIZE_CURSOR, new Dimension(4, 0)),
        E(Cursor.E_RESIZE_CURSOR, new Dimension(4, 0)),
        S(Cursor.S_RESIZE_CURSOR, new Dimension(0, 4)),
        NW(Cursor.NW_RESIZE_CURSOR, new Dimension(4, 4)),
        NE(Cursor.NE_RESIZE_CURSOR, new Dimension(4, 4)),
        SW(Cursor.SW_RESIZE_CURSOR, new Dimension(4, 4)),
        SE(Cursor.SE_RESIZE_CURSOR, new Dimension(4, 4));

        private final Dimension dim;
        private final int cursor;

        Side(int cursor, Dimension dim) {
            this.cursor = cursor;
            this.dim = dim;
        }
    }

    private class DragAdapter extends MouseAdapter {

        private final Point start;

        private DragAdapter() {
            start = new Point();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                start.setLocation(e.getPoint());
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Component c = SwingUtilities.getRoot(e.getComponent());
            if (c instanceof Window && SwingUtilities.isLeftMouseButton(e)) {
                Window window = (Window) c;
                Point loc = window.getLocation();
                window.setLocation(loc.x - start.x + e.getX(), loc.y - start.y + e.getY());
            }
        }
    }

    private class SideLabel extends JLabel {

        private final Side side;

        private SideLabel(Side side) {
            super();
            this.side = side;
            setCursor(Cursor.getPredefinedCursor(side.cursor));
        }

        @Override
        public Dimension getPreferredSize() {
            return side.dim;
        }

        @Override
        public Dimension getMinimumSize() {
            return side.dim;
        }

        @Override
        public Dimension getMaximumSize() {
            return side.dim;
        }
    }

    private class ResizeAdapter extends MouseAdapter {

        private final JFrame frame;
        private final Rectangle rect;

        protected ResizeAdapter(JFrame frame) {
            super();
            this.frame = frame;
            rect = frame.getBounds();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            rect.setBounds(frame.getBounds());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (rect.isEmpty()) {
                return;
            }
            Side side = ((SideLabel) e.getComponent()).side;
            frame.setBounds(getResizedRect(rect, side, e.getX(), e.getY()));
        }

        private Rectangle getResizedRect(Rectangle bounds, Side side, int dx, int dy) {
            switch (side) {
                case NW:
                    bounds.y += dy;
                    bounds.height -= dy;
                    bounds.x += dx;
                    bounds.width -= dx;
                    break;
                case N:
                    bounds.y += dy;
                    bounds.height -= dy;
                    break;
                case NE:
                    bounds.y += dy;
                    bounds.height -= dy;
                    bounds.width += dx;
                    break;
                case W:
                    bounds.x += dx;
                    bounds.width -= dx;
                    break;
                case E:
                    bounds.width += dx;
                    break;
                case SW:
                    bounds.height += dy;
                    bounds.x += dx;
                    bounds.width -= dx;
                    break;
                case S:
                    bounds.height += dy;
                    break;
                case SE:
                    bounds.height += dy;
                    bounds.width += dx;
                    break;
                default: {
                    throw new AssertionError("Unknown SideLabel");
                }
            }
            return bounds;
        }
    }
}
