package io.github.ImpactDevelopment.installer.setting;

import io.github.ImpactDevelopment.installer.Args;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InstallationConfig {
    private final Map<Setting, Object> settingValues = new HashMap<>();

    public InstallationConfig(Args args) {
        args.apply(this);
    }

    public InstallationConfig() {}

    public <T> T getSettingValue(Setting<T> setting) {
        return (T) settingValues.computeIfAbsent(setting, s -> s.getDefaultValue(this));
    }

    public <T> void setSettingValue(Setting<T> setting, T value) {
        settingValues.put(setting, value);
        // iteratively remove now invalid setting values
        // e.g. if you change the minecraft version from 1.12 to 1.13 that makes your selection of Impact version now invalid, which makes your selection of Baritone version now invalid
        outer:
        while (true) {
            for (Setting type : new ArrayList<>(settingValues.keySet())) {
                if (!type.validSetting(this, settingValues.get(type))) {
                    System.out.println(type.getClass().getSimpleName() + " was invalidated by changing " + setting.getClass().getSimpleName() + " to " + value);
                    // uh oh!
                    settingValues.remove(type);
                    continue outer; // recheck from the beginning sadly
                }
            }
            // if we got through all settings with no issues, we're done
            break;
        }
        System.out.println(settingValues);
    }
}
