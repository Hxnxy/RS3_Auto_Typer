package com.hxnry.autotyper.io.profiling.profiles;

import com.hxnry.autotyper.ui.autotyper.TyperConstants;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SettingsProfile {

    public String name = "Settings_1";

    public List<TyperConstants> typers = new ArrayList<>();

    @Override
    public String toString() {
        return this.name;
    }
}
