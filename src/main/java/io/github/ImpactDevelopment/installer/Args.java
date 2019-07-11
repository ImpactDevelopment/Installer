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
import io.github.ImpactDevelopment.installer.impact.ImpactVersion;
import io.github.ImpactDevelopment.installer.impact.ImpactVersionDisk;
import io.github.ImpactDevelopment.installer.impact.ImpactVersions;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.InstallationModeSetting;
import io.github.ImpactDevelopment.installer.setting.settings.MinecraftVersionSetting;
import io.github.ImpactDevelopment.installer.target.InstallationModeOptions;

import java.nio.file.Paths;

public class Args {

    @Parameter(names = {"--no-gpg", "--disable-gpg"}, description = "Disable checking the release signature for testing purposes")
    public boolean noGPG = false;

    @Parameter(names = {"-i", "--impact-version"}, description = "The fully qualified Impact version (e.g. 4.6-1.12.2)")
    public String impactVersion;

    @Parameter(names = {"-f", "--json-file", "--file"}, description = "A json file to install from. Overrides impactVersion.")
    public String file;

    @Parameter(names = {"-m", "--mode"}, description = "The mode of installation to execute")
    public String mode;

    @Parameter(names = {"--no-gui", "--disable-gui"}, description = "Disable the GUI and execute the specifcied mode")
    public boolean noGUI = false;

    @Parameter(names = {"--pre", "--include-pre", "--prerelease", "--prereleases", "--include-prereleases"}, description = "Include releases marked as prerelease on GitHub")
    public boolean prereleases = false;

    public void apply(InstallationConfig config) {
        if (mode != null) {
            config.setSettingValue(InstallationModeSetting.INSTANCE, InstallationModeOptions.valueOf(mode.toUpperCase()));
        }
        if (impactVersion != null) {
            setImpactVersion(config, true,
                    ImpactVersions.getAllVersions().stream()
                            .filter(version -> version.getCombinedVersion().equals(impactVersion))
                            .findAny()
                            .orElseThrow(() -> new IllegalArgumentException("No impact version matches " + impactVersion))
            );
        }
        if (file != null) {
            setImpactVersion(config, false, new ImpactVersionDisk(Paths.get(file)));
        }
    }

    private void setImpactVersion(InstallationConfig config, boolean checkMcVersionValidityAgainstReleases, ImpactVersion version) {
        config.setSettingValue(MinecraftVersionSetting.INSTANCE, version.mcVersion);
        if (checkMcVersionValidityAgainstReleases && !ImpactVersionSetting.INSTANCE.validSetting(config, version)) {
            throw new IllegalStateException(impactVersion + " is not a valid selection in the current configuration. Perhaps try a different mode or version");
        }
        config.setSettingValue(ImpactVersionSetting.INSTANCE, version);
    }
}
