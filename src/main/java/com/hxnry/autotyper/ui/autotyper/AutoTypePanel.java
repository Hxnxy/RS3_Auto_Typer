package com.hxnry.autotyper.ui.autotyper;

import com.hxnry.autotyper.Boot;
import com.hxnry.autotyper.away.WindowInteractions;
import com.hxnry.autotyper.away.WindowUtils;
import com.hxnry.autotyper.io.profiling.managers.ConfigurationProfileManager;
import com.hxnry.autotyper.io.profiling.managers.SettingsProfileManager;
import com.hxnry.autotyper.io.profiling.profiles.ConfigurationProfile;
import com.hxnry.autotyper.io.profiling.profiles.SettingsProfile;
import com.hxnry.autotyper.keybinds.KeybindHookReceiver;
import com.hxnry.autotyper.util.ArrayUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AutoTypePanel extends JPanel {

    public WindowInteractions windowInteractions = new WindowInteractions() {
        @Override
        public void setLoop(int loop) {
            super.setLoop(300);
        }
        @Override
        public void execute() {
            getTypePanelList().forEach(panel -> {
                if(panel != null) {
                    if(!panel.isPaused() && panel.shouldClickWindow()) {
                        panel.highlight();
                        WindowUtils.clickWindow(panel.getTyperConstants(), panel.getTyperConstants().getHwnd());
                    } else {
                        panel.reset();
                    }
                }
            });
        }
    };

    private JButton button;
    protected JLabel debugLabel;
    public AutoTypePanelConstants autoTypePanelConstants = new AutoTypePanelConstants();
    public Timer debugTimer = new Timer(2500, event -> {
            if(!debugLabel.getText().isEmpty()) {
                clearDebugLabel();
            }
    });

    private void clearDebugLabel() {
        debugLabel.setText("");
        debugTimer.stop();
    }

    int counter = 0;
    JPanel buttonPanel = new JPanel();

    JPanel ghkPanel;
    JPanel keybindPanel;
    JRadioButton setStartKeybindsButton;
    JRadioButton setPauseKeybindsButton;
    JRadioButton keybindButton;
    JLabel startKeybindLabel = new JLabel("Start not set");
    JLabel pauseKeybindLabel = new JLabel("Pause not set");

    boolean debug;
    KeybindHookReceiver keyHook;
    List<Integer> heldKeys = new ArrayList<>();
    long lastKeyinput = 0;

    public AutoTypePanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        debugTimer.start();

        keybindPanel = new JPanel();
        keybindPanel.setLayout(new BoxLayout(keybindPanel, BoxLayout.Y_AXIS));
        keybindPanel.setBorder(new EmptyBorder(4, 8, 0, 0));

        ghkPanel = new JPanel();

        ghkPanel.setLayout(new BoxLayout(ghkPanel, BoxLayout.X_AXIS));

        startKeybindLabel.setFont(Boot.customFont.deriveFont(15f));
        startKeybindLabel.setForeground(new Color(232, 205, 64));

        pauseKeybindLabel.setFont(Boot.customFont.deriveFont(15f));
        pauseKeybindLabel.setForeground(new Color(232, 205, 64));

        setPauseKeybindsButton = new JRadioButton("Set Pause");
        setPauseKeybindsButton.setEnabled(false);
        setPauseKeybindsButton.setFocusPainted(false);
        setPauseKeybindsButton.setFont(Boot.customFont);

        setStartKeybindsButton = new JRadioButton("Set Start");
        setStartKeybindsButton.setEnabled(false);
        setStartKeybindsButton.setFocusPainted(false);
        setStartKeybindsButton.setFont(Boot.customFont);


        keybindButton = new JRadioButton("Global Keybind");
        keybindButton.setFont(Boot.customFont);
        keybindButton.setFocusPainted(false);

        keybindButton.addActionListener(e -> {
            if(keybindButton.isSelected()) {
                setStartKeybindsButton.setEnabled(true);
                setPauseKeybindsButton.setEnabled(true);
                setKeyHook();
            } else if(!keybindButton.isSelected()) {
                setStartKeybindsButton.setEnabled(false);
                setPauseKeybindsButton.setEnabled(false);
                removeKeyHook();
            }
        });

        setStartKeybindsButton.addActionListener(e -> {
            if(setStartKeybindsButton.isSelected()) {
                setPauseKeybindsButton.setEnabled(false);
                autoTypePanelConstants.startKeyCodes.clear();
                startKeybindLabel.setText("Start not set");
            } else {
                setPauseKeybindsButton.setEnabled(true);
            }
        });

        setPauseKeybindsButton.addActionListener(e -> {
            if(setPauseKeybindsButton.isSelected()) {
                setStartKeybindsButton.setEnabled(false);
                autoTypePanelConstants.pauseKeyCodes.clear();
                pauseKeybindLabel.setText("Start not set");
            } else {
                setStartKeybindsButton.setEnabled(true);
            }
        });

        keybindPanel.add(startKeybindLabel);
        keybindPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        keybindPanel.add(pauseKeybindLabel);
        ghkPanel.add(keybindPanel);
        ghkPanel.add(Box.createGlue());
        ghkPanel.add(setStartKeybindsButton);
        ghkPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        ghkPanel.add(setPauseKeybindsButton);
        ghkPanel.add(keybindButton);
        ghkPanel.add(Box.createRigidArea(new Dimension(2, 0)));

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        button = new JButton("spawn");
        button.setSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        button.setFocusPainted(false);
        button.setFont(Boot.customFont);
        button.addActionListener(actionEvent -> SwingUtilities.invokeLater(() -> {
            remove(buttonPanel);
            spawn();
            add(buttonPanel);
            Boot.frame.setPreferredSize(null);
            Boot.frame.pack();
        }));

        debugLabel = new JLabel("Welcome :3");
        debugLabel.setFont(Boot.customFont.deriveFont(18f));
        debugLabel.setForeground(Color.GREEN);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 6, 8, 6));
        buttonPanel.add(debugLabel);
        buttonPanel.add(Box.createGlue());
        buttonPanel.add(button);

        add(ghkPanel);
        add(Box.createVerticalGlue());
        spawnAll();
        add(buttonPanel);

        initSettings();

        windowInteractions.start();
    }

    public void writeToDebugLabel(String text) {
        debugTimer.start();
        debugLabel.setText(text);
    }


    private void initSettings() {
        ConfigurationProfile configuration = ConfigurationProfileManager.getProfileByName("Configuration");
        if(configuration == null) return;
        List<AutoTypePanelConstants> constantsList = configuration.getGlobalKeybindsList();
        if(constantsList.size() > 0) {
            autoTypePanelConstants.pauseKeyCodes = constantsList.get(0).pauseKeyCodes;
            autoTypePanelConstants.startKeyCodes = constantsList.get(0).startKeyCodes;
            int counter = 0;
            for(int keyCode : autoTypePanelConstants.pauseKeyCodes) {
                Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                if(counter == 0) {
                    pauseKeybindLabel.setText("Pause -> " + keybind.getConversion());
                } else {
                    pauseKeybindLabel.setText(pauseKeybindLabel.getText() + "  +  " + keybind.getConversion());
                }
                counter++;
            }
            counter = 0;
            for(int keyCode : autoTypePanelConstants.startKeyCodes) {
                Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                if(counter == 0) {
                    startKeybindLabel.setText("Start -> " + keybind.getConversion());
                } else {
                    startKeybindLabel.setText(startKeybindLabel.getText() + "  +  " + keybind.getConversion());
                }
                counter++;
            }

        }
    }

    private void spawnAll() {
        counter = 0;
        SettingsProfile settingsProfile = SettingsProfileManager.getProfileByName();
        if(settingsProfile != null) {
            if(settingsProfile.getTypers().size() > 0) {
                settingsProfile.getTypers().forEach(typer -> spawn());
            }
        } else {
            spawn();
        }
    }

    public Component genBox(int width, int height) {
        return Box.createRigidArea(new Dimension(width, height));
    }

    public final List<TypePanel> typePanelList = new ArrayList<>();

    public List<TypePanel> getTypePanelList() {
        return this.typePanelList;
    }

    public void despawn(TypePanel typePanel) {
        counter--;
        remove(typePanel);
        typePanelList.remove(typePanel);
        Boot.frame.setPreferredSize(null);
        Boot.frame.pack();
    }

    public void spawn() {
        counter++;
        TypePanel panel = new TypePanel(this);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        typePanelList.add(panel);
        panel.setUid(counter);
        panel.initSettings();
        add(panel);
    }

    private void setKeyHook() {
        this.keyHook = new KeybindHookReceiver(Boot.keyHookManager) {
            @Override
            public boolean onKeyUpdate(SystemState sysState, PressState pressState, int time, int vkCode) {
                if(!pressState.equals(PressState.DOWN)) return false;
                if(System.currentTimeMillis() - lastKeyinput >= 150) heldKeys.clear();
                lastKeyinput = System.currentTimeMillis();
                if(setPauseKeybindsButton.isSelected()) {
                    if(autoTypePanelConstants.pauseKeyCodes.size() < 2) {
                        if(!autoTypePanelConstants.pauseKeyCodes.contains(vkCode)) autoTypePanelConstants.pauseKeyCodes.add(vkCode);
                        int counter = 0;
                        for(int keyCode : autoTypePanelConstants.pauseKeyCodes) {
                            Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                            if(counter == 0) {
                                pauseKeybindLabel.setText("Pause -> " + keybind.getConversion());
                            } else {
                                pauseKeybindLabel.setText(pauseKeybindLabel.getText() + "  +  " + keybind.getConversion());
                            }
                            counter++;
                        }
                        if(debug) {
                            int[] array = autoTypePanelConstants.pauseKeyCodes.stream().mapToInt(x->x).toArray();
                            System.out.println("PAUSE KEYBINDS: " + Keybindings.convert(array));
                        }
                    }
                } else if(setStartKeybindsButton.isSelected()) {
                    if(autoTypePanelConstants.startKeyCodes.size() < 2) {
                        if(!autoTypePanelConstants.startKeyCodes.contains(vkCode)) autoTypePanelConstants.startKeyCodes.add(vkCode);
                        int counter = 0;
                        for(int keyCode : autoTypePanelConstants.startKeyCodes) {
                            Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                            if(counter == 0) {
                                startKeybindLabel.setText("Start -> " + keybind.getConversion());
                            } else {
                                startKeybindLabel.setText(startKeybindLabel.getText() + "  +  " + keybind.getConversion());
                            }
                            counter++;
                        }
                        if(debug) {
                            int[] array = autoTypePanelConstants.startKeyCodes.stream().mapToInt(x->x).toArray();
                            System.out.println("KEYBINDS: " + Keybindings.convert(array));
                        }
                    }
                } else {
                    if(autoTypePanelConstants.startKeyCodes.isEmpty() && autoTypePanelConstants.pauseKeyCodes.isEmpty()) return false;
                    if(!heldKeys.contains(vkCode)) {
                        heldKeys.add(vkCode);
                        if(debug) {
                            int[] array = heldKeys.stream().mapToInt(x->x).toArray();
                            System.out.println("HELD DOWN KEYS: " + Keybindings.convert(array));
                        }
                    }
                    int startCounter = 0;
                    int pauseCounter = 0;
                    for(int key : heldKeys) {
                        int[] startArray = autoTypePanelConstants.startKeyCodes.stream().mapToInt(x->x).toArray();
                        int[] pauseArray = autoTypePanelConstants.pauseKeyCodes.stream().mapToInt(x->x).toArray();
                        if(ArrayUtil.arrayContains(startArray, key)) {
                            startCounter++;
                        }
                        if(ArrayUtil.arrayContains(pauseArray, key)) {
                            pauseCounter++;
                        }
                    }
                    if(pauseCounter >= autoTypePanelConstants.pauseKeyCodes.size()) {
                        if(!debug) {
                            int[] array = autoTypePanelConstants.pauseKeyCodes.stream().mapToInt(x->x).toArray();
                            System.out.println("GLOBAL PAUSE KEYBIND PRESSED: " + Keybindings.convert(array));
                        }
                        typePanelList.forEach(TypePanel::pause);
                    }
                    if(startCounter >= autoTypePanelConstants.startKeyCodes.size()) {
                        if(!debug) {
                            int[] array = autoTypePanelConstants.startKeyCodes.stream().mapToInt(x->x).toArray();
                            System.out.println("GLOBAL START KEYBIND PRESSED: " + Keybindings.convert(array));
                        }
                        typePanelList.forEach(TypePanel::start);
                    }
                }
                return false;
            }
        };
        this.keyHook.setName("Global Keybind Hook");
        Boot.keyHookManager.hook(this.keyHook);
    }

    public void removeKeyHook() {
        if(Boot.keyHookManager.hasHook(keyHook)) {
            Boot.keyHookManager.unhook(keyHook);
        }
    }

    public void reload(int index) {
        remove(buttonPanel);
        typePanelList.forEach(panel -> {
            panel.pause();
            panel.removeMouseHook();
            panel.removeKeyHook();
            System.out.println("Completed Unhooking " + "Auto Typer" + " ID: " + panel.getUid());
            remove(panel);
        });
        System.out.println("Completed processing config " + (index - 1) + "...");
        SettingsProfileManager.setCurrentSetting(index);
        typePanelList.clear();
        counter = 0;
        spawnAll();
        add(buttonPanel);
        Boot.frame.repairFrame();
        System.out.println("Reloaded config " + index + "!");
    }
}
