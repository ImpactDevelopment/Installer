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

import io.github.ImpactDevelopment.installer.impact.ImpactVersion;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.Setting;
import io.github.ImpactDevelopment.installer.target.InstallationModeOptions;

import java.nio.file.Path;

public enum DestinationSetting implements Setting<Path> {
    INSTANCE;

    @Override
    public Path getDefaultValue(InstallationConfig config) {
        InstallationModeOptions mode = config.getSettingValue(InstallationModeSetting.INSTANCE);
        switch (mode) {
            case FORGE:
            case FORGE_PLUS_LITELOADER:
                Path minecraft = config.getSettingValue(MinecraftDirectorySetting.INSTANCE);
                ImpactVersion version = config.getSettingValue(ImpactVersionSetting.INSTANCE);
                return minecraft.resolve("mods").resolve(version.mcVersion).resolve("Impact" + "-" + version.getCombinedVersion() + ".jar");
            default:
                return null;
        }
    }

    @Override
    public boolean validSetting(InstallationConfig config, Path value) {
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
