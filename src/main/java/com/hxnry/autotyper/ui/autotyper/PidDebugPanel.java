package com.hxnry.autotyper.ui.autotyper;

import com.hxnry.autotyper.Boot;
import com.hxnry.autotyper.util.User32X;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class PidDebugPanel extends JPanel {

    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    public int pid = -1;

    public int getPid() {
        return this.pid;
    }

    public PidDebugPanel() {
        setLayout(new BorderLayout());
        panel = new JPanel();
        tableModel = genModel();
        tableModel.addColumn("Window");
        tableModel.addColumn("Pid");
        table = new JTable(tableModel);
        table.getTableHeader().setFont(Boot.customFont);
        table.setFont(Boot.customFont);
        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JButton reset = new JButton("refresh");
        reset.setFocusPainted(false);
        reset.addActionListener(al -> {
            refreshTable();
        });
        reset.setMaximumSize(new Dimension(50, 35));

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(reset);

        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(boxlayout);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(buttonPane);
        panel.add(scrollPane);
        add(panel);

        refreshTable();

    }

    public boolean windowStartsWith(String text, String... options) {
        return Arrays.stream(options).anyMatch(text.toLowerCase()::startsWith);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            WinDef.HWND parenthWnd = hWnd;
            int processId = User32.INSTANCE.GetWindowThreadProcessId(hWnd, null);
            char[] name = new char[512];
            char[] className = new char[512];
            User32.INSTANCE.GetWindowText(hWnd, name, name.length);
            User32.INSTANCE.GetClassName(hWnd, className, 512);
            String title = Native.toString(name);
            if (windowStartsWith(title, "rs3 -", "runescape", "osbuddy", "old school")) {
                User32.INSTANCE.EnumChildWindows(hWnd, (hwnd, pointer) -> {

                    User32.INSTANCE.GetWindowText(hWnd, name, name.length);
                    String parent = Native.toString(name);
                    if(parent.equalsIgnoreCase("RuneScape")) {
                        WString string = new WString("RS3 - " + processId);
                        User32X.INSTANCE.SetWindowText(parenthWnd, string);
                    }
                    char[] innerTitle = new char[512];
                    char[] innerClass = new char[512];
                    User32.INSTANCE.GetWindowText(hwnd, innerTitle, 512);
                    User32.INSTANCE.GetClassName(hwnd, innerClass, 512);
                    String innerClassFormatted = Native.toString(innerClass);
                    tableModel.addRow(new Object[]{
                            parent + " -> " + innerClassFormatted,
                            processId

                    });
                    return true;
                }, data);
            }
            return true; // Keep searching

        }, null);
    }

    private DefaultTableModel genModel() {
        return new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return JLabel.class;
                    case 1:
                        return JLabel.class;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid column: " + column);
                }
            }
        };
    }

}