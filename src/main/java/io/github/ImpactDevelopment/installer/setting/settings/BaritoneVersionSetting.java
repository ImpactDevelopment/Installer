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

import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.libraries.LibraryBaritoneReleased;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public enum BaritoneVersionSetting implements ChoiceSetting<LibraryBaritoneReleased> {
    INSTANCE;

    @Override
    public List<LibraryBaritoneReleased> getPossibleValues(InstallationConfig config) {
        ImpactJsonVersion impact = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        Optional<String> versionFilter = impact.baritoneVersionFilter();
        if (versionFilter.isPresent()) {
            return LibraryBaritoneReleased.getVersionsMatching(versionFilter.get());
        }
        return Collections.emptyList();
    }

    @Override
    public String displayName(InstallationConfig config, LibraryBaritoneReleased option) {
        String ret = option.getVersion();
        if (getPossibleValues(config).indexOf(option) == 0) { // hitting nae nae on the O(n^2)
            ret += " (latest)";
        }
        return ret;
    }

    @Override
    public String toString(){
        return getClass().getSimpleName();
    }
}
