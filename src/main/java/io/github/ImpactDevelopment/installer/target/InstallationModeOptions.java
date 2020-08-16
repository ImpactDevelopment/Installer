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
import io.github.ImpactDevelopment.installer.target.targets.*;

import java.util.function.Function;

public enum InstallationModeOptions {
    VANILLA("Official Launcher", "Install", true, Vanilla::new),
    FORGE("Forge Mod", "Save As", true, opt -> new Forge(opt, false)),
    FORGE_PLUS_LITELOADER("Forge + Liteloader", "Save As", true, opt -> new Forge(opt, true)),
    VALIDATE("Validate version", "Validate", false, Validate::new),
    MULTIMC("MultiMC Instance", "Install", true, MultiMC::new),
    SHOWJSON("Show Version JSON", "Show JSON", true, ShowJSON::new);

    InstallationModeOptions(String name, String buttonText, boolean showInGUI, Function<InstallationConfig, InstallationMode> mode) {
        this.mode = mode;
        this.name = name;
        this.buttonText = buttonText;
        this.showInGUI = showInGUI;
    }

    public final Function<InstallationConfig, InstallationMode> mode;
    private final String name;
    private final String buttonText;
    public final boolean showInGUI;

    public boolean supports(ImpactVersion impact) {
        switch (this) {
            case FORGE:
            case FORGE_PLUS_LITELOADER:
                return impact.mcVersion.equals("1.12.2") && impact.impactVersion.compareTo("4.6") >= 0;
            default:
                return true;
        }
    }

    public String getButtonText() {
        return buttonText;
    }

    @Override
    public String toString() {
        return name;
    }
}
