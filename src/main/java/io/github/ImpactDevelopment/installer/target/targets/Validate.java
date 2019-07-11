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

package io.github.ImpactDevelopment.installer.target.targets;

import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;
import io.github.ImpactDevelopment.installer.target.InstallationMode;
import io.github.ImpactDevelopment.installer.utils.Fetcher;

public class Validate implements InstallationMode {
    private final ImpactJsonVersion version;
    private final InstallationConfig config;

    public Validate(InstallationConfig config) {
        this.version = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        this.config = config;
    }

    @Override
    public String apply() {
        for (ILibrary library : version.resolveLibraries(config)) {
            byte[] b = Fetcher.fetchBytes(library.getURL());
            if (b.length != library.getSize() || !Forge.sha1hex(b).equals(library.getSHA1())) {
                throw new RuntimeException(b.length + " " + library.getSize() + " " + Forge.sha1hex(b) + " " + library.getSHA1());
            }
        }
        return "All libraries verified";
    }
}
