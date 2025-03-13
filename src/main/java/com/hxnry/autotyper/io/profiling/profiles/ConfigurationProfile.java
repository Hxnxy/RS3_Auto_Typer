package com.hxnry.autotyper.io.profiling.profiles;

import com.hxnry.autotyper.ui.autotyper.AutoTypePanelConstants;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigurationProfile {

    public String name = "Configuration";

    int windowX;
    int windowY;
    int width;
    int height;
    int selected_profile = 0;
    List<AutoTypePanelConstants> globalKeybindsList = new ArrayList<>();

    @Override
    public String toString() {
        return this.name;
    }
}