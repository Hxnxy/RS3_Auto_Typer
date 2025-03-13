package com.hxnry.autotyper.ui.autotyper;

import com.hxnry.autotyper.Boot;
import com.hxnry.autotyper.io.profiling.managers.SettingsProfileManager;
import com.hxnry.autotyper.io.profiling.profiles.SettingsProfile;
import com.hxnry.autotyper.util.Icons;
import com.hxnry.autotyper.util.ImageDecoder;
import lombok.Data;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Data
public class CustomMenuBar extends JMenuBar {

    private BufferedImage SAVE_ICON;
    private BufferedImage NEW_ICON;
    private BufferedImage SWITCH_CONFIG_ICON;
    private BufferedImage PROFILE_ICON;
    private BufferedImage LAUNCH_ICON;
    private BufferedImage REPAIR_ICON;
    ImageIcon imageIcon;
    ImageIcon switchIcon;
    private JMenu menu;
    private JMenu repairMenu;
    private JMenu spawnClientMenuItem;
    private JMenuItem spawnConfig;
    private List<JMenuItem> profileMenuList = new ArrayList<>();

    public CustomMenuBar() {
        init();
    }

    public void init() {

        SAVE_ICON = ImageDecoder.decodeToImage(Icons.SAVE_ENCODE);
        PROFILE_ICON = ImageDecoder.decodeToImage(Icons.PROFILE_ICON);
        LAUNCH_ICON = ImageDecoder.decodeToImage(Icons.LAUNCH_ICON);
        REPAIR_ICON = ImageDecoder.decodeToImage(Icons.REPAIR_ICON);
        NEW_ICON = ImageDecoder.decodeToImage(Icons.NEW_ICON);
        SWITCH_CONFIG_ICON = ImageDecoder.decodeToImage(Icons.SWITCH_CONFIG_ICON);
        imageIcon = new ImageIcon(SAVE_ICON);
        switchIcon = new ImageIcon(SWITCH_CONFIG_ICON);

        menu = new JMenu("Configuration - 1");
        menu.setIcon(new ImageIcon(PROFILE_ICON));
        menu.setFont(Boot.customFont);
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                loadConfigs();
            }
        });

        repairMenu = new JMenu("Repair Window");
        repairMenu.setIcon(new ImageIcon(REPAIR_ICON));
        repairMenu.setFont(Boot.customFont);
        repairMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Boot.frame.repairFrame();
            }
        });

        spawnClientMenuItem = new JMenu("Launch Client");
        spawnClientMenuItem.setIcon(new ImageIcon(LAUNCH_ICON));
        spawnClientMenuItem.setFont(Boot.customFont);
        spawnClientMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("Launching from @ -> " + System.getProperty("launcher_path"));
                try {
                    Runtime runTime = Runtime.getRuntime();
                    Process process = runTime.exec(System.getProperty("launcher_path"));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

        spawnConfig = getNewConfig();

        add(menu);
        add(spawnClientMenuItem);
        add(repairMenu);

    }

    private JMenuItem getNewConfig() {
        JMenuItem newConfig = new JMenuItem("create");
        newConfig.setFont(Boot.customFont);
        newConfig.setIcon(new ImageIcon(NEW_ICON));
        newConfig.addActionListener(event -> {
            int newNumberOfSettings = SettingsProfileManager.count();
            SettingsProfileManager.setCurrentSetting(newNumberOfSettings + 1);
            SettingsProfileManager.saveJson(new SettingsProfile());
            SettingsProfileManager.setCurrentSetting(newNumberOfSettings);
            System.out.println("Spawned new config #" + SettingsProfileManager.count());
        });
        return newConfig;
    }


    private void loadConfigs() {
        menu.removeAll();
        profileMenuList.clear();
        int numberOfSettings = SettingsProfileManager.count();
        IntStream.rangeClosed(1, numberOfSettings).forEach(index -> {
            JMenuItem entry = new JMenuItem("-> " + index);
            entry.setFont(Boot.customFont);
            if(index == SettingsProfileManager.getCurrentSetting()) {
                entry.setIcon(imageIcon);
            } else {
                entry.setIcon(switchIcon);
            }
            entry.addActionListener(event -> {
                if(index == SettingsProfileManager.getCurrentSetting()) {
                    SettingsProfile settings = new SettingsProfile();
                    Boot.frame.autoTypePanel.getTypePanelList().forEach(typerPanel -> {
                        typerPanel.getTyperConstants().setMessage(typerPanel.getOutputMessage());
                        settings.getTypers().add(typerPanel.getTyperConstants());
                    });
                    SettingsProfileManager.saveJson(settings);
                    Boot.frame.autoTypePanel.writeToDebugLabel("Saved data...");
                } else {
                    menu.setText("Configuration - " + String.valueOf((index)));
                    Boot.frame.autoTypePanel.reload(index);
                    Boot.frame.autoTypePanel.writeToDebugLabel("Switched config to # " + index);
                }
            });
            profileMenuList.add(entry);
        });
        profileMenuList.forEach(menu::add);
        menu.add(spawnConfig);
    }
}
