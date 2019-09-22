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
