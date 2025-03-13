package com.hxnry.autotyper.ui.timer;

import com.hxnry.autotyper.io.profiling.managers.SettingsProfileManager;
import com.hxnry.autotyper.io.profiling.managers.TimerConfigurationProfileManager;
import com.hxnry.autotyper.io.profiling.profiles.TimerConfigurationProfile;
import com.hxnry.autotyper.ui.lnf.CustomFrame;
import lombok.Getter;
import lombok.Setter;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TimerFrame extends CustomFrame {

    TimerMenuBar menuBar = new TimerMenuBar(this);

    @Getter
    public TimerPanel rootPanel;

    public TimerFrame() {
        rootPanel = new TimerPanel();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                rootPanel.debugTimer.stop();
                TimerConfigurationProfile configuration = TimerConfigurationProfileManager.getProfileByName("Timer Configuration");
                if(configuration != null) {
                    int currentSetting = SettingsProfileManager.getCurrentSetting();
                    configuration.setWindowX(getLocation().x);
                    configuration.setWindowY(getLocation().y);
                    configuration.setWidth(getWidth());
                    configuration.setHeight(getHeight());
                    configuration.getGBindsList().clear();
                    configuration.getGBindsList().add(rootPanel.timerConstants);
                    TimerConfigurationProfileManager.saveJson(configuration);
                }
                rootPanel.removeKeyHook();
                System.out.println("Completed Unhooking " + "Timer by Hxnry");
            }
        });

        setTitle("Timer by Hxnry" + " _ version 1.0.1a");
        setMinimumSize(new Dimension(650, 150));
        //contentTabbedPane.addTab("Settings", new JLabel("Settings Tab"));
        JhromeTabbedPaneUI ui = new JhromeTabbedPaneUI();
        getContentPane().add(menuBar, BorderLayout.NORTH);
        getContentPane().add(rootPanel, BorderLayout.CENTER);
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
        TimerConfigurationProfile configuration = TimerConfigurationProfileManager.getProfileByName("Timer Configuration");
        if (configuration == null) return;
    }

    public void loadLocation() {
        TimerConfigurationProfile configuration = TimerConfigurationProfileManager.getProfileByName("Timer Configuration");
        if (configuration == null) return;
        setLocation(configuration.getWindowX(), configuration.getWindowY());
        setAlwaysOnTop(true);
        setAlwaysOnTop(false);
    }

    public void loadSize() {
        TimerConfigurationProfile configuration = TimerConfigurationProfileManager.getProfileByName("Timer Configuration");
        if (configuration == null) return;
        setPreferredSize(new Dimension(configuration.getWidth(), configuration.getHeight()));
    }
}