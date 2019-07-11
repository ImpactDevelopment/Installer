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

package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.target.InstallationModeOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum InstallationModeSetting implements ChoiceSetting<InstallationModeOptions> {
    INSTANCE;

    @Override
    public List<InstallationModeOptions> getPossibleValues(InstallationConfig config) {
        List<InstallationModeOptions> options = new ArrayList<>(Arrays.asList(InstallationModeOptions.values()));
        if (!config.hasSettingValue(this) || config.getSettingValue(this).showInGUI) {
            options.removeIf(opt -> !opt.showInGUI);
        }
        return options;
    }
}
