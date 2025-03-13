package com.hxnry.autotyper.ui.autotyper;

import com.hxnry.autotyper.io.profiling.managers.ConfigurationProfileManager;
import com.hxnry.autotyper.io.profiling.managers.SettingsProfileManager;
import com.hxnry.autotyper.io.profiling.profiles.ConfigurationProfile;
import com.hxnry.autotyper.ui.lnf.CustomFrame;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Frame extends CustomFrame {

    CustomMenuBar menuBar;
    JTabbedPane contentTabbedPane = new JTabbedPane();
    public AutoTypePanel autoTypePanel;

    public Frame() {
        autoTypePanel = new AutoTypePanel();
        menuBar = new CustomMenuBar();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                autoTypePanel.debugTimer.stop();
                ConfigurationProfile configurationProfile = ConfigurationProfileManager.getProfileByName("Configuration");
                if(configurationProfile != null) {
                    int currentSetting = SettingsProfileManager.getCurrentSetting();
                    configurationProfile.setWindowX(getLocation().x);
                    configurationProfile.setWindowY(getLocation().y);
                    configurationProfile.setWidth(getWidth());
                    configurationProfile.setHeight(getHeight());
                    configurationProfile.getGlobalKeybindsList().clear();
                    configurationProfile.getGlobalKeybindsList().add(autoTypePanel.autoTypePanelConstants);
                    ConfigurationProfileManager.saveJson(configurationProfile);
                }
                autoTypePanel.removeKeyHook();
                autoTypePanel.getTypePanelList().forEach(typePanel -> {
                    typePanel.removeMouseHook();
                    typePanel.removeKeyHook();
                    System.out.println("Completed Unhooking " + "Auto Typer" + " ID: " + typePanel.getUid());
                });
            }
        });

        setTitle("Runescape 3 Auto Typer" + " _ version 1.4.3");
        setMinimumSize(new Dimension(650, 150));
        contentTabbedPane.setLayout(new BorderLayout());
        contentTabbedPane.addTab("Runescape 3", autoTypePanel);
        //contentTabbedPane.addTab("Settings", new JLabel("Settings Tab"));
        JhromeTabbedPaneUI ui = new JhromeTabbedPaneUI();
        contentTabbedPane.setUI(ui);
        contentTabbedPane.putClientProperty(JhromeTabbedPaneUI.NEW_TAB_BUTTON_VISIBLE, false);
        contentTabbedPane.setBorder(new EmptyBorder(5, 0, 0, 0));
        getContentPane().add(menuBar, BorderLayout.NORTH);
        getContentPane().add(contentTabbedPane, BorderLayout.CENTER);
    }

    public void repairFrame() {
        setPreferredSize(null);
        pack();
    }

    public void loadFrameData() {
        loadLocation();
        loadSize();
        loadCurrentSetting();
    }

    public void loadCurrentSetting() {
        ConfigurationProfile configuration = ConfigurationProfileManager.getProfileByName("Configuration");
        if (configuration == null) return;
    }

    public void loadLocation() {
        ConfigurationProfile configuration = ConfigurationProfileManager.getProfileByName("Configuration");
        if (configuration == null) return;
        setLocation(configuration.getWindowX(), configuration.getWindowY());
        setAlwaysOnTop(true);
        setAlwaysOnTop(false);
    }

    public void loadSize() {
        ConfigurationProfile configuration = ConfigurationProfileManager.getProfileByName("Configuration");
        if (configuration == null) return;
        setPreferredSize(new Dimension(configuration.getWidth(), configuration.getHeight()));
    }
}
