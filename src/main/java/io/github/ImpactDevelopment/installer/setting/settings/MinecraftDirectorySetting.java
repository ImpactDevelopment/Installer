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

import io.github.ImpactDevelopment.installer.OperatingSystem;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.Setting;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum MinecraftDirectorySetting implements Setting<Path> {
    INSTANCE;

    @Override
    public Path getDefaultValue(InstallationConfig config) {
        switch (OperatingSystem.getOS()) {
            case WINDOWS:
                return Paths.get(System.getenv("APPDATA")).resolve(".minecraft");
            case OSX:
                return Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support").resolve("minecraft");
            default:
                return Paths.get(System.getProperty("user.home")).resolve(".minecraft");
        }
    }

    @Override
    public boolean validSetting(InstallationConfig config, Path value) {
        // we are ALL minecraft paths on this blessed day
        return true;
    }
}
