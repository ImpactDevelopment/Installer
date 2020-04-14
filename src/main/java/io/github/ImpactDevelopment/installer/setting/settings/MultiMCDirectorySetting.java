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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public enum MultiMCDirectorySetting implements Setting<Path> {
    INSTANCE;

    @Override
    public Path getDefaultValue(InstallationConfig config) {
        Path home = Paths.get(System.getProperty("user.home"));

        // MultiMC is a portable app, so we can only guess its location.
        // On linux it can be installed via repos too, where it normally uses XDG_DATA_HOME, which is nice.
        switch (OperatingSystem.getOS()) {
            case WINDOWS:
                return scanForMultiMC(Paths.get("C:"), Paths.get(System.getenv("APPDATA")), home, home.resolve("Downloads"), home.resolve("Games")).orElse(home);
            case OSX:
                return scanForMultiMC(Paths.get("/"), home, home.resolve("Library").resolve("Application Support"), home.resolve("Downloads")).orElse(home);
            default:
                return scanForMultiMC(linuxDefault(), home, home.resolve("Downloads")).orElse(linuxDefault());
        }
    }

    private static Optional<Path> scanForMultiMC(Path... locations) {
        for (Path folder : locations) {
            if (Files.isDirectory(folder)) {
                // Just in case, check if the search location itself is multimc
                if (isMultiMC(folder)) return Optional.of(folder);

                // If not, check each of its children
                try {
                    Optional<Path> match = Files.walk(folder, 1)
                            .filter(MultiMCDirectorySetting::isMultiMC)
                            .findFirst();
                    if (match.isPresent()) {
                        return match;
                    }
                } catch (IOException ignored) { }
            }
        }
        return Optional.empty();
    }

    private static boolean isMultiMC(Path path) {
        // If multimc has been run in this location, even without completing setup, this file will exist
        Path config = path.resolve("multimc.cfg");
        return Files.isDirectory(path) && Files.isRegularFile(config);
    }

    private static Path linuxDefault() {
        String xdg = System.getenv("XDG_DATA_HOME");
        Path data = xdg.isEmpty()
                ? Paths.get(System.getProperty("user.home")).resolve(".local").resolve("share")
                : Paths.get(xdg);
        return data.resolve("multimc");
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
