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

package io.github.ImpactDevelopment.installer.target;

import io.github.ImpactDevelopment.installer.impact.ImpactVersion;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.target.targets.Forge;
import io.github.ImpactDevelopment.installer.target.targets.ShowJSON;
import io.github.ImpactDevelopment.installer.target.targets.Validate;
import io.github.ImpactDevelopment.installer.target.targets.Vanilla;

import java.util.function.Function;

public enum InstallationModeOptions {
    VANILLA(Vanilla::new, true), FORGE(Forge::new, true), VALIDATE(Validate::new, false), SHOWJSON(ShowJSON::new, true) {
        @Override
        public String toString() {
            return "Show JSON";
        }
    };

    InstallationModeOptions(Function<InstallationConfig, InstallationMode> mode, boolean showInGUI) {
        this.mode = mode;
        this.showInGUI = showInGUI;
    }

    public final Function<InstallationConfig, InstallationMode> mode;
    public final boolean showInGUI;

    public boolean supports(ImpactVersion impact) {
        switch (this) {
            case FORGE:
                return impact.mcVersion.equals("1.12.2") && impact.impactVersion.compareTo("4.6") >= 0;
            default:
                return true;
        }
    }

    @Override
    public String toString() {
        // incredibly based code
        String name = super.toString();
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
