package com.hxnry.autotyper.ui.autotyper;

import com.hxnry.autotyper.Boot;
import com.hxnry.autotyper.away.AwayMouseHookReceiver;
import com.hxnry.autotyper.io.profiling.managers.SettingsProfileManager;
import com.hxnry.autotyper.io.profiling.profiles.SettingsProfile;
import com.hxnry.autotyper.keybinds.KeybindHookReceiver;
import com.hxnry.autotyper.ui.lnf.CloseIcon;
import com.hxnry.autotyper.util.ArrayUtil;
import com.hxnry.autotyper.util.Clock;
import com.hxnry.autotyper.util.Random;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.Getter;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.sun.jna.platform.win32.WinUser.WM_CHAR;
import static com.sun.jna.platform.win32.WinUser.WM_KEYDOWN;

public class TypePanel extends JPanel {

    @Getter
    private final TyperConstants typerConstants = new TyperConstants();

    private JTextField input;
    private JButton send;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel infoPanel;
    private JPanel keybindPanel;
    private JRadioButton enterButton;
    private JRadioButton keybindButton;
    private JRadioButton setKeybindsButton;
    private JRadioButton preventLogoutButton;
    private ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
    private AwayMouseHookReceiver mouseHookReceiver;

    private boolean canSend;

    private Future<?> future;

    private long timestamp = 0L;
    private long sendTimestamp = 0L;

    public int getUid() {
        return typerConstants.getUid();
    }

    public void setUid(int id) {
        typerConstants.setUid(id);
    }

    public void initLabels() {
        minIntervalLabel.setText(Long.toString(typerConstants.getMinInterval()));
        maxIntervalLabel.setText(Long.toString(typerConstants.getMaxInterval()));
        messageIntervalLabel.setText("" + typerConstants.getMessageDelay());
        pidLabel.setText("not set");
    }

    public Timer timer = genTimer();
    public Timer logoutTimer = generateLogoutTimer();

    public boolean isPaused() {
        return send.getText().equalsIgnoreCase("Start");
    }

    boolean alerted = false;
    boolean updatedStuff = false;

    public void generateBeep() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = loader.getResourceAsStream("beep.wav")) {
            InputStream bufferedIn = new BufferedInputStream(stream);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }
    }

    public boolean shouldClickWindow() {
        return mouseHookReceiver != null && typerConstants != null
                && mouseHookReceiver.getTimePassed() > mouseHookReceiver.nextPress;
    }

    private Timer generateLogoutTimer() {
        return new Timer(300, (actionEvent -> {
            if (mouseHookReceiver != null && preventLogoutButton.isSelected() && mouseHookReceiver.lastPressed != -1) {
                long nextInput = mouseHookReceiver.nextPress - mouseHookReceiver.getTimePassed();
                if (mouseHookReceiver.lastPressed > 0) {
                    lastInputLabel.setText(Clock.formatTime(mouseHookReceiver.getTimePassed()) +
                            " | Next input: " + Clock.formatTime((nextInput)));
                }
            }
        }));
    }

    public void highlight() {
        topPanel.setBackground(Color.WHITE);
        bottomPanel.setBackground(Color.WHITE);
        keybindPanel.setBackground(Color.WHITE);
        contentPanel.setBackground(Color.WHITE);
        infoPanel.setBackground(Color.WHITE);
    }

    public void reset() {
        topPanel.setBackground(getBackground());
        bottomPanel.setBackground(getBackground());
        infoPanel.setBackground(getBackground());
        keybindPanel.setBackground(getBackground());
        contentPanel.setBackground(getBackground());
    }

    private Timer genTimer() {
        return new Timer((int) typerConstants.getMessageDelay(), (actionEvent -> {
            if (!isVisible()) {
                ((Timer) actionEvent.getSource()).stop();
                return;
            }
            if (!canSend) return;
            if (future != null && !future.isDone()) return;
            future = threadPoolExecutor.submit(() -> {
                System.out.println(((double) (System.currentTimeMillis() - timestamp) / 1000) + " seconds has passed since last message.");
                User32.INSTANCE.EnumWindows((hWnd, data) -> {
                    int processId = User32.INSTANCE.GetWindowThreadProcessId(hWnd, null);
                    if (processId == typerConstants.getPid()) {
                        User32.INSTANCE.EnumChildWindows(hWnd, (hwnd, pointer) -> {
                            sendTimestamp = System.currentTimeMillis();
                            for (char c : Native.toCharArray(input.getText())) {
                                if (isPaused()) break;
                                if (typerConstants.getHwnd() != null) {
                                    //System.out.println("hwnd isnt null! yay!");
                                }
                                try {
                                    Thread.sleep(Random.mid((int) typerConstants.getMinInterval(), (int) typerConstants.getMaxInterval()));
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                User32.INSTANCE.SendMessage(hwnd, WM_CHAR, new WinDef.WPARAM(c), new WinDef.LPARAM(0x002C0001));
                            }
                            if (isPaused()) {
                                System.out.println("Paused detected! canceling...");
                                return false;
                            }
                            if (enterButton.isSelected()) {
                                User32.INSTANCE.SendMessage(hwnd, WM_KEYDOWN, new WinDef.WPARAM(0x0D), new WinDef.LPARAM(0x002C0001));
                                System.out.println("enter!");
                            }
                            if (send.getText().equalsIgnoreCase("Pause"))
                                canSend = true;
                            System.out.println("done sending -> '" + input.getText() + "' took " + (System.currentTimeMillis() - sendTimestamp + " ms to type out"));
                            resetTimer();
                            return false;
                        }, data);
                        return false;
                    }
                    return true;
                }, null);
            });
        }));
    }

    KeybindHookReceiver keyHook;
    boolean debug;
    List<Integer> heldKeys = new ArrayList<>();
    long lastKeyinput = 0;

    private void setKeyHook() {
        this.keyHook = new KeybindHookReceiver(Boot.keyHookManager) {
            @Override
            public boolean onKeyUpdate(SystemState sysState, PressState pressState, int time, int vkCode) {

                if (!pressState.equals(PressState.DOWN)) return false;

                if (System.currentTimeMillis() - lastKeyinput >= 150) heldKeys.clear();

                lastKeyinput = System.currentTimeMillis();

                if (setKeybindsButton.isSelected()) {
                    if (typerConstants.getKeycodes().size() < 2) {
                        if (!typerConstants.getKeycodes().contains(vkCode)) typerConstants.getKeycodes().add(vkCode);
                        int counter = 0;
                        for (int keyCode : typerConstants.getKeycodes()) {
                            Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                            if (counter == 0) {
                                keybindLabel.setText(keybind.getConversion());
                            } else {
                                keybindLabel.setText(keybindLabel.getText() + "  +  " + keybind.getConversion());
                            }
                            counter++;
                        }
                        if (debug) {
                            int[] array = typerConstants.getKeycodes().stream().mapToInt(x -> x).toArray();
                            System.out.println("KEYBINDS: " + Keybindings.convert(array));
                        }
                    }
                } else {
                    if (typerConstants.getKeycodes().isEmpty()) return false;
                    if (!heldKeys.contains(vkCode)) {
                        heldKeys.add(vkCode);
                        if (debug) {
                            int[] array = heldKeys.stream().mapToInt(x -> x).toArray();
                            System.out.println("HELD DOWN KEYS: " + Keybindings.convert(array));
                        }
                    }
                    int counter = 0;
                    for (int key : heldKeys) {
                        int[] array = typerConstants.getKeycodes().stream().mapToInt(x -> x).toArray();
                        if (ArrayUtil.arrayContains(array, key)) {
                            counter++;
                        }
                    }
                    if (counter >= typerConstants.getKeycodes().size()) {
                        if (!debug) {
                            int[] array = typerConstants.getKeycodes().stream().mapToInt(x -> x).toArray();
                            System.out.println("AUTO TYPER " + this.getId() + " KEYBIND PRESSED: " + Keybindings.convert(array));
                        }
                        sendStuff();
                    }
                }
                return false;
            }
        };
        keyHook.setId(getUid());
        Boot.keyHookManager.hook(this.keyHook);
    }

    public void removeKeyHook() {
        if (Boot.keyHookManager.hasHook(keyHook)) {
            Boot.keyHookManager.unhook(keyHook);
        }
    }

    private void setMouseHook() {
        User32.INSTANCE.EnumWindows((hWnd, data) -> {
            int processId = User32.INSTANCE.GetWindowThreadProcessId(hWnd, null);
            if (processId == typerConstants.getPid()) {
                User32.INSTANCE.EnumChildWindows(hWnd, (hwnd, pointer) -> {
                    mouseHookReceiver = new AwayMouseHookReceiver(Boot.mouseHookManager);
                    mouseHookReceiver.setPid(typerConstants.getPid());
                    mouseHookReceiver.setName("Mouse Hook process ID:" + typerConstants.getPid());
                    Boot.mouseHookManager.hook(mouseHookReceiver);
                    System.out.println("Set hook for process ID:" + typerConstants.getPid() + "!");
                    logoutTimer.restart();
                    logoutTimer.start();
                    return false;
                }, data);
                return false;
            }
            return true;
        }, null);
    }

    private void resetTimer() {
        timer.restart();
        timestamp = System.currentTimeMillis();
    }

    JPanel contentPanel;
    private AutoTypePanel source;

    public TypePanel(AutoTypePanel source) {

        this.source = source;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new LineBorder(new Color(140, 140, 140), 1));

        topPanel = new JPanel();
        bottomPanel = new JPanel();
        infoPanel = new JPanel();
        timer.start();

        initLabels();

        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        spawnTopPanel();
        contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        spawnKeystrokePanel();
        contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        spawnKeybindPanel();
        contentPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        spawnInfoPanel();

        add(Box.createRigidArea(new Dimension(4, 0)));
        add(contentPanel);
        add(Box.createRigidArea(new Dimension(4, 0)));
    }

    public void initSettings() {

        SettingsProfile settingsProfile = SettingsProfileManager.getProfileByName();

        if (settingsProfile == null) {
            System.out.println("Couldn't load settings!");
            return;
        }

        TyperConstants constants = settingsProfile.getTypers().stream().filter(p -> p.getUid() == typerConstants.getUid()).findAny().orElse(null);

        if (constants == null) {
            return;
        }

        enterButton.setSelected(constants.isPressEnter());

        if (constants.getPid() != -1) {

            System.out.println("Searching for process ID -> " + constants.getPid());

            User32.INSTANCE.EnumWindows((hWnd, data) -> {
                int processId = User32.INSTANCE.GetWindowThreadProcessId(hWnd, null);
                if (processId == typerConstants.getPid()) {
                    typerConstants.setPid(constants.getPid());
                    pidLabel.setText(Integer.toString(typerConstants.getPid()));
                    pidLabel.setForeground(Color.GREEN);
                    preventLogoutButton.setEnabled(true);
                    keybindButton.setEnabled(true);
                    return false;
                }
                return true;
            }, null);
        }

        typerConstants.setMessage(constants.getMessage());
        input.setText(typerConstants.getMessage());

        typerConstants.setMinInterval(constants.getMinInterval());
        minIntervalLabel.setText(Long.toString(typerConstants.getMinInterval()));

        typerConstants.setMaxInterval(constants.getMaxInterval());
        maxIntervalLabel.setText(Long.toString(typerConstants.getMaxInterval()));

        typerConstants.setMessageDelay(constants.getMessageDelay());

        if (!constants.getKeycodes().isEmpty()) {
            typerConstants.setKeycodes(constants.getKeycodes());
            int counter = 0;
            for (int keyCode : typerConstants.getKeycodes()) {
                Keybindings.Keybind keybind = Keybindings.getByKeycode(keyCode);
                if (counter == 0) {
                    keybindLabel.setText(keybind.getConversion());
                } else {
                    keybindLabel.setText(keybindLabel.getText() + "  +  " + keybind.getConversion());
                }
                counter++;
            }
        }

        typerConstants.setMessageDelay(constants.getMessageDelay());
        messageIntervalLabel.setText(Long.toString(typerConstants.getMessageDelay()));

        System.out.println("Loaded settings for auto typer ID: " + constants.getUid() + "!");
    }

    public JLabel keybindLabel = new JLabel("not set");

    private void spawnKeybindPanel() {

        keybindPanel = new JPanel();
        keybindPanel.setLayout(new BoxLayout(keybindPanel, BoxLayout.X_AXIS));

        JLabel keybindInfoLabel = new JLabel("Keybind ->");
        keybindInfoLabel.setFont(Boot.customFont);
        keybindLabel.setFont(Boot.customFont);
        keybindLabel.setForeground(new Color(232, 205, 64));

        keybindButton = new JRadioButton("Keybinds");
        keybindButton.addActionListener(e -> {
            if (typerConstants.getPid() == -1) return;
            if (keybindButton.isSelected()) {
                setKeybindsButton.setEnabled(true);
                setKeyHook();
            } else if (!keybindButton.isSelected()) {
                setKeybindsButton.setEnabled(false);
                removeKeyHook();
            }
        });
        keybindButton.setEnabled(false);
        keybindButton.setFont(Boot.customFont);
        keybindButton.setFocusPainted(false);

        setKeybindsButton = new JRadioButton("Set Mode");
        setKeybindsButton.setEnabled(false);
        setKeybindsButton.setFont(Boot.customFont);
        setKeybindsButton.setFocusPainted(false);
        setKeybindsButton.addActionListener(e -> {
            if (setKeybindsButton.isSelected()) {
                typerConstants.getKeycodes().clear();
                keybindLabel.setText("not set");
            }
        });

        keybindPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        keybindPanel.add(keybindInfoLabel);
        keybindPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        keybindPanel.add(keybindLabel);
        keybindPanel.add(Box.createGlue());
        keybindPanel.add(setKeybindsButton);
        keybindPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        keybindPanel.add(keybindButton);
        keybindPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        contentPanel.add(keybindPanel);
    }

    private JLabel lastInputLabel = new JLabel("unknown");

    private void spawnInfoPanel() {

        preventLogoutButton = new JRadioButton("Prevent Logout");
        preventLogoutButton.addActionListener(e -> {
            if (typerConstants.getPid() == -1) return;
            if (preventLogoutButton.isSelected()) {
                setMouseHook();
            } else if (!preventLogoutButton.isSelected()) {
                if (Boot.mouseHookManager.hasHook(mouseHookReceiver)) {
                    removeMouseHook();
                }
                reset();
            }
        });
        preventLogoutButton.setEnabled(false);
        preventLogoutButton.setFont(Boot.customFont);
        preventLogoutButton.setFocusPainted(false);

        JLabel lastInputInfoLabel = new JLabel("Last Input -> ");
        lastInputInfoLabel.setFont(Boot.customFont);
        lastInputLabel.setFont(Boot.customFont);

        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        infoPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        infoPanel.add(lastInputInfoLabel);
        infoPanel.add(lastInputLabel);
        infoPanel.add(Box.createGlue());
        infoPanel.add(Box.createRigidArea(new Dimension(2, 0)));
        infoPanel.add(Box.createRigidArea(new Dimension(2, 0)));
        infoPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        infoPanel.add(preventLogoutButton);
        infoPanel.add(Box.createRigidArea(new Dimension(8, 0)));

        contentPanel.add(infoPanel);
    }

    private JButton createCloseButton() {
        CloseIcon closeIcon = new CloseIcon();
        JButton button = new JButton(closeIcon);
        button.setBackground(Color.red);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(e -> {
            if (source == null) return;
            SwingUtilities.invokeLater(() -> {
                removeKeyHook();
                removeMouseHook();
                source.despawn(this);
            });
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

    JLabel minIntervalLabel = new JLabel();
    JLabel maxIntervalLabel = new JLabel();
    JLabel messageIntervalLabel = new JLabel();
    JLabel pidLabel = new JLabel();

    private void spawnKeystrokePanel() {

        JLabel keyStrokesLabel = new JLabel("Key strokes ->");
        keyStrokesLabel.setFont(Boot.customFont);
        minIntervalLabel.setFont(Boot.customFont);
        minIntervalLabel.setForeground(Color.GREEN);
        maxIntervalLabel.setFont(Boot.customFont);
        maxIntervalLabel.setForeground(Color.GREEN);
        JLabel minInfoLabel = new JLabel("min (ms) ->");
        minInfoLabel.setFont(Boot.customFont);
        minIntervalLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    typerConstants.setMinInterval(Random.low(20, 40));
                    minIntervalLabel.setText("" + typerConstants.getMinInterval());
                } else {
                    Object input = JOptionPane.showInputDialog("Set min keystroke interval (ms)");
                    if (input == null || String.valueOf(input).isEmpty()) return;
                    int amount = Integer.parseInt(String.valueOf(input).replaceAll("[^0-9]", ""));
                    minIntervalLabel.setText("" + amount);
                    typerConstants.setMinInterval(amount);
                }
            }
        });
        JLabel maxInfoLabel = new JLabel("max (ms) ->");
        maxIntervalLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    typerConstants.setMaxInterval(Random.mid(75, 200));
                    maxIntervalLabel.setText("" + typerConstants.getMaxInterval());
                } else {
                    Object input = JOptionPane.showInputDialog("Set max keystroke interval (ms)");
                    if (input == null || String.valueOf(input).isEmpty()) return;
                    int amount = Integer.parseInt(String.valueOf(input).replaceAll("[^0-9]", ""));
                    maxIntervalLabel.setText("" + amount);
                    typerConstants.setMaxInterval(amount);
                }
            }
        });
        maxInfoLabel.setFont(Boot.customFont);
        messageIntervalLabel.setText("" + typerConstants.getMessageDelay());
        messageIntervalLabel.setForeground(Color.GREEN);
        messageIntervalLabel.setFont(Boot.customFont);
        JLabel delayInfoLabel = new JLabel("message delay (ms) ->");
        messageIntervalLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    typerConstants.setMessageDelay(Random.mid(4800, 6800));
                    messageIntervalLabel.setText("" + typerConstants.getMessageDelay());
                } else {
                    Object input = JOptionPane.showInputDialog("Set Message Delay Time (ms)");
                    if (input == null || String.valueOf(input).isEmpty()) return;
                    int amount = Integer.parseInt(String.valueOf(input).replaceAll("[^0-9]", ""));
                    messageIntervalLabel.setText("" + amount);
                    typerConstants.setMessageDelay(amount);
                    timer.stop();
                    timer = genTimer();
                    timer.start();
                    System.out.println("Set delay to " + timer.getDelay() + "ms");
                }
            }
        });
        delayInfoLabel.setFont(Boot.customFont);
        pidLabel.setFont(Boot.customFont);
        pidLabel.setForeground(new Color(202, 67, 71));
        JLabel pidInfoLabel = new JLabel("process id:");
        pidInfoLabel.setBorder(new EmptyBorder(0, 15, 0, 2));
        pidLabel.setBorder(new EmptyBorder(0, 0, 0, 5));
        pidLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JDialog dialog = new JDialog();
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dialog.setTitle("Select a process ID");
                dialog.setLocationRelativeTo(null);
                SelectClientPanel selectClientPanel = new SelectClientPanel(dialog);
                dialog.add(selectClientPanel);
                dialog.setPreferredSize(new Dimension(382, 187));
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        int chosenPid = selectClientPanel.getPid();
                        if (chosenPid == -1) return;
                        pidLabel.setForeground(Color.GREEN);
                        pidLabel.setText("" + chosenPid);
                        typerConstants.setPid(chosenPid);
                        User32.INSTANCE.EnumWindows((hWnd, data) -> {
                            int processId = User32.INSTANCE.GetWindowThreadProcessId(hWnd, null);
                            if (processId == typerConstants.getPid()) {
                                typerConstants.setHwnd(hWnd);
                                return false;
                            }
                            return true;
                        }, null);
                        preventLogoutButton.setEnabled(true);
                        keybindButton.setEnabled(true);
                        super.windowClosed(e);
                    }
                });
                dialog.setPreferredSize(new Dimension(455, 385));
                dialog.pack();
                dialog.setVisible(true);
            }
        });

        pidInfoLabel.setFont(Boot.customFont);

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        bottomPanel.add(keyStrokesLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        bottomPanel.add(minInfoLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        bottomPanel.add(minIntervalLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        bottomPanel.add(maxInfoLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        bottomPanel.add(maxIntervalLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        bottomPanel.add(delayInfoLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        bottomPanel.add(messageIntervalLabel);
        bottomPanel.add(Box.createGlue());
        bottomPanel.add(pidInfoLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        bottomPanel.add(pidLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        contentPanel.add(bottomPanel);
    }

    private void spawnTopPanel() {

        JButton close = createCloseButton();

        input = new JTextField();
        send = new JButton("Start");
        send.setForeground(Color.GREEN);
        input.setFont(Boot.customFont);

        send.setFont(Boot.customFont);
        send.setFocusPainted(false);

        enterButton = new JRadioButton("Press Enter");
        enterButton.setFont(Boot.customFont);
        enterButton.setSelected(true);
        enterButton.setFocusPainted(false);
        enterButton.addActionListener(event -> {
            typerConstants.setPressEnter(enterButton.isSelected());
        });

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        topPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        JLabel messageLabel = new JLabel("Message");
        messageLabel.setFont(Boot.customFont);
        topPanel.add(messageLabel);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(input);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(enterButton);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(send);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(close);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        send.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sendStuff();
            }
        });
        contentPanel.add(topPanel);
    }

    public void start() {
        if (typerConstants.getPid() == -1) {
            System.err.println(getPidNotSetError());
            return;
        } else if (input.getText().isEmpty()) {
            System.err.println(getEmptyInputError());
            return;
        }
        send.setText("Pause");
        send.setForeground(new Color(202, 67, 71));
        canSend = true;
        System.out.println("Started auto typer #" + getUid());
        timestamp = System.currentTimeMillis();
    }

    private String getEmptyInputError() {
        return "Can't send message because input for auto typer #" + getUid() + " is empty.";
    }

    public void pause() {
        if (!send.getText().equalsIgnoreCase("Pause")) return;
        canSend = false;
        send.setText("Start");
        send.setForeground(Color.GREEN);
        System.out.println("Paused auto typer #" + getUid());
    }

    public void sendStuff() {
        switch (send.getText()) {
            case "Start":
                if (typerConstants.getPid() == -1) {
                    System.err.println(getPidNotSetError());
                    return;
                } else if (input.getText().isEmpty()) {
                    System.err.println(getEmptyInputError());
                    return;
                }
                send.setText("Pause");
                logoutTimer.restart();
                logoutTimer.start();
                send.setForeground(new Color(202, 67, 71));
                canSend = true;
                System.out.println("Started auto typer #" + getUid());
                timestamp = System.currentTimeMillis();
                break;
            case "Pause":
                canSend = false;
                send.setText("Start");
                send.setForeground(Color.GREEN);
                System.out.println("Paused auto typer #" + getUid());
                break;
        }
    }

    private void sendClick(WinDef.HWND hwnd) {
        int numInputs = 1;
        final int MOUSEEVENTF_LEFTUP = 0x0004;
        final int MOUSEEVENTF_LEFTDOWN = 0x0002;
        final int MOUSEEVENTF_RIGHTUP = 0x0010;
        final int MOUSEEVENTF_RIGHTDOWN = 0x0008;
        WinUser.INPUT[] inputs = new WinUser.INPUT[numInputs];
        inputs[0] = new WinUser.INPUT();
        inputs[0].type = new WinDef.DWORD(WinUser.INPUT.INPUT_MOUSE);
        inputs[0].input.setType("mi");
        inputs[0].input.mi.dx = new WinDef.LONG(0);
        inputs[0].input.mi.dy = new WinDef.LONG(0);
        inputs[0].input.mi.mouseData = new WinDef.DWORD(0);
        inputs[0].input.mi.dwFlags = new WinDef.DWORD(MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_RIGHTUP);
        inputs[0].input.mi.time = new WinDef.DWORD(0);
        int cbSize = inputs[0].size();
        User32.INSTANCE.SendInput(new WinDef.DWORD(numInputs), inputs, cbSize);
    }

    private String getPidNotSetError() {
        return "Can't send message because the process id for auto typer " + getUid() + " is not set.";
    }

    public void removeMouseHook() {
        if (Boot.mouseHookManager.hasHook(mouseHookReceiver)) {
            Boot.mouseHookManager.unhook(mouseHookReceiver);
        }
        topPanel.setBackground(getBackground());
        bottomPanel.setBackground(getBackground());
        contentPanel.setBackground(getBackground());
        keybindPanel.setBackground(getBackground());
        infoPanel.setBackground(getBackground());
        lastInputLabel.setText("unknown");
        updatedStuff = false;
        alerted = false;
        logoutTimer.stop();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public TyperConstants getTyperConstants() {
        return typerConstants;
    }

    public String getOutputMessage() {
        return input.getText().isEmpty() ? "" : input.getText();
    }
}