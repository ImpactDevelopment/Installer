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

package io.github.ImpactDevelopment.installer.target;

import io.github.ImpactDevelopment.installer.impact.ImpactVersion;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.target.targets.Forge;
import io.github.ImpactDevelopment.installer.target.targets.ShowJSON;
import io.github.ImpactDevelopment.installer.target.targets.MultiMC;
import io.github.ImpactDevelopment.installer.target.targets.Validate;
import io.github.ImpactDevelopment.installer.target.targets.Vanilla;

import java.util.function.Function;

public enum InstallationModeOptions {
    VANILLA(Vanilla::new, true),
    FORGE(Forge::new, true),
    VALIDATE(Validate::new, false),
    MULTIMC(MultiMC::new,true),
    SHOWJSON(ShowJSON::new, true);

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
        // this is oof NGL :eyes:
        switch (this) {
            case VANILLA:
                return "Vanilla";
            case SHOWJSON:
                return "Show Vanilla JSON";
            case MULTIMC:
                return "Show MultiMC JSON";
            case FORGE:
                return "Forge";
            case VALIDATE:
                return "Validate Vanilla version";
            default:
                return "Unknown";
        }
    }
}
