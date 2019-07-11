/*
 * This file is part of Impact Installer.
 *
 * Impact Installer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Impact Installer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impact Installer.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.ImpactDevelopment.installer.setting;

import io.github.ImpactDevelopment.installer.Args;
import io.github.ImpactDevelopment.installer.setting.settings.InstallationModeSetting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class InstallationConfig {
    private final Map<Setting, Object> settingValues = new LinkedHashMap<>();

    public InstallationConfig(Args args) {
        args.apply(this);
    }

    public InstallationConfig() {}

    public <T> T getSettingValue(Setting<T> setting) {
        return (T) settingValues.computeIfAbsent(setting, s -> s.getDefaultValue(this));
    }

    public <T> boolean hasSettingValue(Setting<T> setting) {
        return settingValues.containsKey(setting);
    }

    public <T> void setSettingValue(Setting<T> setting, T value) {
        settingValues.put(setting, value);
        // iteratively remove now invalid setting values
        // e.g. if you change the minecraft version from 1.12 to 1.13 that makes your selection of Impact version now invalid, which makes your selection of Baritone version now invalid
        outer:
        while (true) {
            for (Setting type : new ArrayList<>(settingValues.keySet())) {
                if (!type.validSetting(this, settingValues.get(type))) {
                    System.out.println(type + " was invalidated by changing " + setting + " to " + value);
                    // uh oh!
                    settingValues.remove(type);
                    settingValues.put(type, type.getDefaultValue(this)); // reset to default
                    continue outer; // recheck from the beginning sadly
                }
            }
            // if we got through all settings with no issues, we're done
            break;
        }
        System.out.println(settingValues);
    }

    public String execute() throws IOException {
        return getSettingValue(InstallationModeSetting.INSTANCE).mode.apply(this).apply();
    }
}
