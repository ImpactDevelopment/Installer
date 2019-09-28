/*
 * This file is part of Impact Installer.
 *
 * Copyright (C) 2019  ImpactDevelopment and contributors
 *
 * See the CONTRIBUTORS.md file for a list of copyright holders
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package io.github.ImpactDevelopment.installer.setting;

import io.github.ImpactDevelopment.installer.Args;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.InstallationModeSetting;
import io.github.ImpactDevelopment.installer.utils.Tracky;

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

    public <T> boolean setSettingValue(Setting<T> setting, T value) {
        settingValues.put(setting, value);
        // iteratively remove now invalid setting values
        // e.g. if you change the minecraft version from 1.12 to 1.13 that makes your selection of Impact version now invalid, which makes your selection of Baritone version now invalid
        boolean thisSettingReverted = false;
        outer:
        while (true) {
            for (Setting type : new ArrayList<>(settingValues.keySet())) {
                if (!type.validSetting(this, settingValues.get(type))) {
                    System.out.println(type + " was invalidated by changing " + setting + " to " + value);
                    if (type == setting) {
                        thisSettingReverted = true;
                    }
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
        return !thisSettingReverted;
    }

    public String execute() throws IOException {
        String label = getSettingValue(ImpactVersionSetting.INSTANCE).getCombinedVersion();
        Tracky.event("installer", "install", label);
        String result;
        try {
            result = getSettingValue(InstallationModeSetting.INSTANCE).mode.apply(this).apply();
        } catch (RuntimeException | IOException ex) {
            Tracky.event("installer", "error", label);
            throw ex;
        }
        Tracky.event("installer", "success", label);
        return result;
    }
}
