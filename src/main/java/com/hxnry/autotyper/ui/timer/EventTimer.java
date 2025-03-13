package com.hxnry.autotyper.ui.timer;

import com.hxnry.autotyper.util.Icons;
import com.hxnry.autotyper.util.ImageDecoder;
import com.hxnry.autotyper.util.Stopwatch;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class EventTimer {

    @Getter
    @Setter
    private Stopwatch stopwatch;

    @Getter
    private String name = "generic event timer";

    @Getter
    @Setter
    private String timerText = "n/a";

    public void setTimerText() {
        timerText = stopwatch.getFormattedTime();
    }

    @Getter
    private BufferedImage ICON_IMAGE = ImageDecoder.decodeToImage(Icons.LAUNCH_ICON);

    @Getter
    private ImageIcon testIcon = new ImageIcon(ICON_IMAGE);

    public ImageIcon resizeIcon(int newWidth, int newHeight, BufferedImage iconImage) {
        Image scaledImage = iconImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }



    @Getter
    @Setter
    private Color color = Color.WHITE;

    abstract void execute();

    public EventTimer(String name) {
        this.name = name;
        this.stopwatch = new Stopwatch();
        stopwatch.pause();
    }
}
