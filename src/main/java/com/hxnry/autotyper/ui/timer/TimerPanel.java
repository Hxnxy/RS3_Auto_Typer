package com.hxnry.autotyper.ui.timer;

import com.hxnry.autotyper.Boot;
import com.hxnry.autotyper.away.WindowInteractions;
import com.hxnry.autotyper.io.profiling.managers.TimerConfigurationProfileManager;
import com.hxnry.autotyper.io.profiling.profiles.TimerConfigurationProfile;
import com.hxnry.autotyper.keybinds.KeybindHookReceiver;
import com.hxnry.autotyper.ui.autotyper.Keybindings;
import com.hxnry.autotyper.util.ArrayUtil;
import com.hxnry.autotyper.util.Stopwatch;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TimerPanel extends JPanel {

    protected JLabel debugLabel;
    public TimerPanelConstants timerConstants = new TimerPanelConstants();

    public Timer debugTimer = new Timer(2500, event -> {
        if(!debugLabel.getText().isEmpty()) {
            clearDebugLabel();
        }
    });

    @Getter
    @Setter
    public EventTimer eventTimer = new EventTimer("Generic Timer") {
        @Override
        void execute() {
            eventTimer.setTimerText();
        }
    };

    public WindowInteractions windowInteractions = new WindowInteractions() {

        @Override
        public void setLoop(int loop) {
            super.setLoop(50);
        }
        @Override
        public void execute() {
            if(eventTimer != null) {
                eventTimer.execute();
                timer.setForeground(eventTimer.getColor());
                timer.setText(eventTimer.getTimerText());
            }
        }
    };

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

    JLabel timer = new JLabel("n/a");

    public TimerPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        debugTimer.start();

        timer.setFont(Boot.customFont.deriveFont(105f));
        timer.setIcon(eventTimer.resizeIcon(150, 150, eventTimer.getICON_IMAGE()));

        timer.setHorizontalAlignment(SwingConstants.CENTER);

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
                timerConstants.startKeyCodes.clear();
                startKeybindLabel.setText("Start not set");
            } else {
                setPauseKeybindsButton.setEnabled(true);
            }
        });

        setPauseKeybindsButton.addActionListener(e -> {
            if(setPauseKeybindsButton.isSelected()) {
                setStartKeybindsButton.setEnabled(false);
                timerConstants.pauseKeyCodes.clear();
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

        debugLabel = new JLabel("Welcome :3");
        debugLabel.setFont(Boot.customFont.deriveFont(18f));
        debugLabel.setForeground(Color.GREEN);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 6, 8, 6));
        buttonPanel.add(debugLabel);
        buttonPanel.add(Box.createGlue());

        buttonPanel.add(timer);

        add(ghkPanel);
        add(Box.createVerticalGlue());
        add(buttonPanel);

        initSettings();

        windowInteractions.start();
    }

    public void writeToDebugLabel(String text) {
        debugTimer.start();
        debugLabel.setText(text);
    }


    private void initSettings() {
        TimerConfigurationProfile configuration = TimerConfigurationProfileManager.getProfileByName("Timer Configuration");
        if(configuration == null) return;
        timer.setFont(Boot.customFont.deriveFont(configuration.getFontSize()));
        List<TimerPanelConstants> constantsList = configuration.getGBindsList();
        if(constantsList.size() > 0) {
            timerConstants.pauseKeyCodes = constantsList.get(0).pauseKeyCodes;
            timerConstants.startKeyCodes = constantsList.get(0).startKeyCodes;
            int counter = 0;
            for(int keyCode : timerConstants.pauseKeyCodes) {
                Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                if(counter == 0) {
                    pauseKeybindLabel.setText("Pause -> " + keybind.getConversion());
                } else {
                    pauseKeybindLabel.setText(pauseKeybindLabel.getText() + "  +  " + keybind.getConversion());
                }
                counter++;
            }
            counter = 0;
            for(int keyCode : timerConstants.startKeyCodes) {
                Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                if(counter == 0) {
                    startKeybindLabel.setText("Reset -> " + keybind.getConversion());
                } else {
                    startKeybindLabel.setText(startKeybindLabel.getText() + "  +  " + keybind.getConversion());
                }
                counter++;
            }

        }
    }

    public Component genBox(int width, int height) {
        return Box.createRigidArea(new Dimension(width, height));
    }

    private void setKeyHook() {
        this.keyHook = new KeybindHookReceiver(Boot.keyHookManager) {
            @Override
            public boolean onKeyUpdate(SystemState sysState, PressState pressState, int time, int vkCode) {
                if(!pressState.equals(PressState.DOWN)) return false;
                if(System.currentTimeMillis() - lastKeyinput >= 150) heldKeys.clear();
                lastKeyinput = System.currentTimeMillis();
                if(setPauseKeybindsButton.isSelected()) {
                    if(timerConstants.pauseKeyCodes.size() < 2) {
                        if(!timerConstants.pauseKeyCodes.contains(vkCode)) timerConstants.pauseKeyCodes.add(vkCode);
                        int counter = 0;
                        for(int keyCode : timerConstants.pauseKeyCodes) {
                            Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                            if(counter == 0) {
                                pauseKeybindLabel.setText("Pause -> " + keybind.getConversion());
                            } else {
                                pauseKeybindLabel.setText(pauseKeybindLabel.getText() + "  +  " + keybind.getConversion());
                            }
                            counter++;
                        }
                        if(debug) {
                            int[] array = timerConstants.pauseKeyCodes.stream().mapToInt(x->x).toArray();
                            System.out.println("PAUSE KEYBINDS: " + Keybindings.convert(array));
                        }
                    }
                } else if(setStartKeybindsButton.isSelected()) {
                    if(timerConstants.startKeyCodes.size() < 2) {
                        if(!timerConstants.startKeyCodes.contains(vkCode)) timerConstants.startKeyCodes.add(vkCode);
                        int counter = 0;
                        for(int keyCode : timerConstants.startKeyCodes) {
                            Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                            if(counter == 0) {
                                startKeybindLabel.setText("Start -> " + keybind.getConversion());
                            } else {
                                startKeybindLabel.setText(startKeybindLabel.getText() + "  +  " + keybind.getConversion());
                            }
                            counter++;
                        }
                        if(debug) {
                            int[] array = timerConstants.startKeyCodes.stream().mapToInt(x->x).toArray();
                            System.out.println("KEYBINDS: " + Keybindings.convert(array));
                        }
                    }
                } else {
                    if(timerConstants.startKeyCodes.isEmpty() && timerConstants.pauseKeyCodes.isEmpty()) return false;
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
                        int[] startArray = timerConstants.startKeyCodes.stream().mapToInt(x->x).toArray();
                        int[] pauseArray = timerConstants.pauseKeyCodes.stream().mapToInt(x->x).toArray();
                        if(ArrayUtil.arrayContains(startArray, key)) {
                            startCounter++;
                        }
                        if(ArrayUtil.arrayContains(pauseArray, key)) {
                            pauseCounter++;
                        }
                    }

                    Keybindings.Keybind keybind = Keybindings.getByKeycode(vkCode);

                    System.out.println("Key code pressed -> " + keybind.getConversion());

                    //PRESSED RESET
                    if(pauseCounter >= timerConstants.pauseKeyCodes.size()) {
                        if(!debug) {
                            int[] array = timerConstants.pauseKeyCodes.stream().mapToInt(x->x).toArray();
                            System.out.println("GLOBAL RESET KEYBIND PRESSED: " + Keybindings.convert(array));
                            eventTimer.getStopwatch().pause();
                            writeToDebugLabel("Paused the timer");
                        }
                    }
                    //PRESSED START
                    if(startCounter >= timerConstants.startKeyCodes.size()) {
                        if(!debug) {
                            int[] array = timerConstants.startKeyCodes.stream().mapToInt(x->x).toArray();
                            System.out.println("GLOBAL START KEYBIND PRESSED: " + Keybindings.convert(array));
                            eventTimer.setStopwatch(new Stopwatch());
                            writeToDebugLabel("Reset the timer");
                        }
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
}