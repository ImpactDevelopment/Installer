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

package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.target.TargetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum TargetSetting implements ChoiceSetting<TargetOptions> {
    INSTANCE;

    @Override
    public List<TargetOptions> getPossibleValues(InstallationConfig config) {
        List<TargetOptions> options = new ArrayList<>(Arrays.asList(TargetOptions.values()));
        if (!config.hasSettingValue(this) || config.getSettingValue(this).showInGUI) {
            options.removeIf(opt -> !opt.showInGUI);
        }
        return options;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
