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

package io.github.ImpactDevelopment.installer;

import com.beust.jcommander.Parameter;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.Setting;
import io.github.ImpactDevelopment.installer.setting.settings.MinecraftVersionSetting;

public class Args {

    @Parameter(names = { "--no-gpg", "--disable-gpg" }, description = "Disable checking the release signature for testing purposes")
    public boolean gpg = true;

    @Parameter(names = { "-i", "--impact-version" }, description = "The Impact version to default to")
    public String impactVersion;

    @Parameter(names = { "-m", "--minecraft-version" }, description = "The Minecraft version to default to")
    public String minecraftVersion;

    public Args() {
        // TODO populate somme default values from gh releases
        // This happens _before_ JCommander parses argv and overrides our defaults.
    }

    public void apply(InstallationConfig config) {
        // TODO Apply args defined here to the config
        // Alternatively we could try and replace the InstallationConfig system with this class
        if (!minecraftVersion.isEmpty() && !setSetting(config, MinecraftVersionSetting.INSTANCE, minecraftVersion))
            error("Invalid Minecraft Version " + minecraftVersion);

//        if (!impactVersion.isEmpty() && !setSetting(config, ImpactVersionSetting.INSTANCE, impactVersion))
//            error("Invalid Minecraft Version " + impactVersion);
    }

    private static <T> boolean setSetting(InstallationConfig config, Setting<T> setting, T value) {
        if (setting.validSetting(config, value)) {
            config.setSettingValue(setting, value);
            return true;
        }
        return false;
    }

    private static void error(String message) {
        System.err.println(message);
        System.exit(1);
    }
}
