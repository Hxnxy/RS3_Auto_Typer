package com.hxnry.autotyper.io.profiling.profiles;

import com.hxnry.autotyper.ui.timer.TimerPanelConstants;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TimerConfigurationProfile {

    public String name = "Timer Configuration";

    int windowX;
    int windowY;
    int width;
    int height;
    int selected_profile = 0;
    float fontSize;
    List<TimerPanelConstants> gBindsList = new ArrayList<>();

    @Override
    public String toString() {
        return this.name;
    }
}
