package com.hxnry.autotyper;

import com.hxnry.autotyper.io.profiling.managers.ConfigurationProfileManager;
import com.hxnry.autotyper.io.profiling.managers.SettingsProfileManager;
import com.hxnry.autotyper.io.profiling.managers.TimerConfigurationProfileManager;
import com.hxnry.autotyper.io.profiling.profiles.ConfigurationProfile;
import com.hxnry.autotyper.io.profiling.profiles.SettingsProfile;
import com.hxnry.autotyper.io.profiling.profiles.TimerConfigurationProfile;
import com.hxnry.autotyper.ui.autotyper.Frame;
import com.hxnry.autotyper.ui.timer.TimerFrame;
import me.coley.simplejna.hook.key.KeyHookManager;
import me.coley.simplejna.hook.mouse.MouseHookManager;
import org.pushingpixels.substance.api.skin.*;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.lang.instrument.Instrumentation;

public class Boot {

    public static Frame frame;
    public static TimerFrame timerFrame;
    public static Font customFont;
    public static MouseHookManager mouseHookManager = new MouseHookManager();
    public static KeyHookManager keyHookManager = new KeyHookManager();

    public static void premain(String args, Instrumentation inst) {

    }

    public static void main(String[] args) {
        /**
        try {


            for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
                if(vmd.toString().contains("16236")) {
                    VirtualMachine vm = VirtualMachine.attach(vmd);
                    System.out.println(" (" + vmd.id() + ")" + " Detected entry point -> " + vmd.displayName());
                    String path = "D:\\Cached windows 10\\IdeaProjects\\JNA RS3 Auto Typer\\target\\" +
                            "JNA-RS3-Auto-Typer-1.4.1-SNAPSHOT-shaded.jar";
                    vm.loadAgent(path, "name=Server" + vm.id());
                    vm.detach();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
         **/

        String settingsPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File launcher = new File(settingsPath + "\\Hxnry\\Auto Typer\\launcher.bat");
        System.setProperty("launcher_path", launcher.getAbsolutePath());
        System.out.println("Set launcher batch file path @ -> " + System.getProperty("launcher_path"));
        if(!launcher.exists()) {
            try {
                launcher.createNewFile();
                System.out.println("Created launcher batch file @ -> " + launcher.getAbsolutePath());
                BufferedWriter writer = new BufferedWriter(new FileWriter(launcher));
                writer.write ("start \"C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\Jagex\\RuneScape Launcher.url\" \"rs-launch://www.runescape.com/k=5/l=$(Language:0)/jav_config.ws\"");
                writer.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        System.out.println("Data stored @ -> " + settingsPath);

        SettingsProfileManager.setBase(settingsPath);
        SettingsProfileManager.createBaseIfNotExists();
        SettingsProfileManager.createProfile(new SettingsProfile());

        ConfigurationProfileManager.setBase(settingsPath);
        ConfigurationProfileManager.createBaseIfNotExists();
        ConfigurationProfileManager.createProfile(new ConfigurationProfile());

        TimerConfigurationProfileManager.setBase(settingsPath);
        TimerConfigurationProfileManager.createBaseIfNotExists();
        TimerConfigurationProfileManager.createProfile(new TimerConfigurationProfile());

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = loader.getResourceAsStream("runescape_uf.ttf")) {
            customFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(22f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (FontFormatException | IOException ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(Boot::createGui);
    }

    private static void createGui() {
        try {
            UIManager.setLookAndFeel(new SubstanceRavenLookAndFeel());
            //UIManager.setLookAndFeel(new SubstanceDustLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }


        frame = new Frame();
        frame.setVisible(true);
        frame.loadFrameData();
        frame.pack();


        timerFrame = new TimerFrame();
        timerFrame.setVisible(true);
        timerFrame.loadFrameData();
        timerFrame.pack();
    }
}
