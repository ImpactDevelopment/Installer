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

import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.Setting;
import io.github.ImpactDevelopment.installer.utils.OperatingSystem;

import java.nio.file.Path;

public enum MinecraftDirectorySetting implements Setting<Path> {
    INSTANCE;

    @Override
    public Path getDefaultValue(InstallationConfig config) {
        switch (OperatingSystem.getOS()) {
            case WINDOWS:
                return OperatingSystem.getDataDirectory().resolve(".minecraft");
            case OSX:
                return OperatingSystem.getDataDirectory().resolve("minecraft");
            default:
                return OperatingSystem.getHome().resolve(".minecraft");
        }
    }

    @Override
    public boolean validSetting(InstallationConfig config, Path value) {
        // we are ALL minecraft paths on this blessed day
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
