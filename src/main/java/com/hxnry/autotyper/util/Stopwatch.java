package com.hxnry.autotyper.util;

import java.time.Duration;
import java.time.Instant;

public class Stopwatch {

    private Instant start = null,  pause = null;

    public Stopwatch() {
        this.start = Instant.now();
    }

    public int getSeconds() {
        return (int) duration().toMillis() / 1000;
    }

    public boolean isPaused() {
        return pause != null;
    }

    public void pause() {
        pause = Instant.now();
    }

    public void resume() {
        if (pause != null) {
            start = start.plus(Duration.between(pause, Instant.now()));
            pause = null;
        }
    }

    public void reset() {
        this.start = Instant.now();
    }

    public Duration duration() {
        if (pause != null) {
            return Duration.between(start, pause);
        }
        return Duration.between(start, Instant.now());
    }

    public String formatTime(final long time) {
        final int sec = (int) (time / 1000), d = sec / 86400, h = sec / 3600 % 24,  m = sec / 60 % 60, s = sec % 60;
        return (d < 10 ? "" + d : d) + "d " + (h < 10 ? "" + h : h) + "h " + (m < 10 ? "" + m : m) + "m " + (s < 10 ? "" + s : s) + "s";
    }

    public double formatPerHour(long num) {
        return num * 3600000D / duration().toMillis();
    }

    public String getFormattedTime() {
        return formatTime(duration().toMillis());
    }

    @Override
    public String toString() {
        Duration duration = duration();
        return duration.toDays() + ":" + duration.toHours() + ":" + duration.toMinutes() + ":" + duration.getSeconds();
    }
}

