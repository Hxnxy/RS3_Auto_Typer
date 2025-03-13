package com.hxnry.autotyper.ui.timer;

import com.hxnry.autotyper.Boot;
import com.hxnry.autotyper.io.profiling.managers.SettingsProfileManager;
import com.hxnry.autotyper.io.profiling.managers.TimerConfigurationProfileManager;
import com.hxnry.autotyper.io.profiling.profiles.SettingsProfile;
import com.hxnry.autotyper.io.profiling.profiles.TimerConfigurationProfile;
import com.hxnry.autotyper.util.Icons;
import com.hxnry.autotyper.util.ImageDecoder;
import com.hxnry.autotyper.util.Stopwatch;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.stream.IntStream;

@Data
public class TimerMenuBar extends JMenuBar {

    private BufferedImage SAVE_ICON;
    private BufferedImage NEW_ICON;
    private BufferedImage SWITCH_CONFIG_ICON;
    private BufferedImage PROFILE_ICON;
    private BufferedImage LAUNCH_ICON;
    private BufferedImage REPAIR_ICON;
    ImageIcon imageIcon;
    ImageIcon switchIcon;
    private TimerFrame parentFrame;
    private JMenu menu;
    private JMenu repairMenu;
    private JMenu fontSizeMenu;
    private JCheckBoxMenuItem alwaysOnTopMenu;
    private int buttonHeight = 65;
    private List<JMenuItem> profileMenuList = new ArrayList<>();

    public TimerMenuBar(TimerFrame parentFrame) {
        this.parentFrame = parentFrame;
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

        menu = new JMenu("Select Activity");
        menu.setIcon(new ImageIcon(PROFILE_ICON));
        menu.setFont(Boot.customFont);
        JMenuItem cerberus = new JMenuItem("Cerberus");
        cerberus.addActionListener(actionEvent -> {
            menu.setText("Cerberus");
            parentFrame.getRootPanel().writeToDebugLabel("Fetching Cerberus data...");
            parentFrame.getRootPanel().setEventTimer(new EventTimer("Death Charge") {
                @Override
                void execute() {

                }
            });
            parentFrame.getRootPanel().getEventTimer().getStopwatch().reset();
            parentFrame.getRootPanel().getEventTimer().getStopwatch().pause();
        });
        JMenuItem deathCharge = new JMenuItem("Death Charge");
        deathCharge.addActionListener(actionEvent -> {
            menu.setText("Death Charge");
            parentFrame.getRootPanel().writeToDebugLabel("Fetching death charge data...");
            parentFrame.getRootPanel().setEventTimer(new EventTimer("Death Charge") {
                @Override
                void execute() {
                    Stopwatch stopwatch = getStopwatch();
                    int remainingSeconds = 60 - stopwatch.getSeconds();
                    String timerText = String.valueOf(remainingSeconds);
                    if(remainingSeconds <= 7) {
                        setColor(Color.GREEN);
                    }
                    if(remainingSeconds <= 0) {
                        setTimerText("");
                    } else {
                        setTimerText(timerText);
                    }
                }
            });
        });
        JMenuItem flinch = new JMenuItem("Flinch");
        flinch.addActionListener(actionEvent -> {
            menu.setText("Flinch");
            parentFrame.getRootPanel().writeToDebugLabel("Fetching flinch data...");
            parentFrame.getRootPanel().setEventTimer(new EventTimer("Flinch") {
                @Override
                void execute() {
                    Stopwatch stopwatch = getStopwatch();
                    int seconds = stopwatch.getSeconds();
                    if(seconds > 6) {
                        stopwatch.reset();
                        return;
                    }
                    int prettySeconds = 6 - seconds;
                    if(seconds > 0 && seconds % 6 == 0) {
                        setColor(Color.GREEN);
                        setTimerText("FLINCH");
                    } else {
                        setColor(Color.WHITE);
                        setTimerText(String.valueOf(prettySeconds));
                    }
                }
            });
        });
        menu.add(cerberus);
        menu.add(deathCharge);
        menu.add(flinch);

        fontSizeMenu = new JMenu("Font Size");
        fontSizeMenu.setIcon(new ImageIcon(REPAIR_ICON));
        fontSizeMenu.setFont(Boot.customFont);
        fontSizeMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                TimerConfigurationProfile profile = TimerConfigurationProfileManager.getProfileByName("Timer Configuration");
                if(profile != null) {
                    String input = JOptionPane.showInputDialog(null, "Please enter a number between 1 and 200:");
                    float size = Integer.parseInt(input);
                    profile.setFontSize((int) size);
                    TimerConfigurationProfileManager.saveJson(profile);
                    parentFrame.getRootPanel().timer.setFont(Boot.customFont.deriveFont(size));
                }
            }
        });

        alwaysOnTopMenu = new JCheckBoxMenuItem("On Top");
        alwaysOnTopMenu.setFont(Boot.customFont);
        alwaysOnTopMenu.setMaximumSize(new Dimension(110, buttonHeight));
        alwaysOnTopMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                SwingUtilities.invokeLater(() -> {
                    boolean state = !Boot.timerFrame.isAlwaysOnTop();
                    System.out.println("Set always on top to -> " + state);
                    Boot.timerFrame.setAlwaysOnTop(state);
                    if(state) {
                        alwaysOnTopMenu.setBackground(new Color(0, 0, 0));
                    } else {
                        alwaysOnTopMenu.setBackground(new Color(255, 255, 255));
                    }
                });
            }
        });

        add(menu);
        add(fontSizeMenu);
        add(alwaysOnTopMenu);

    }
}
