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

package io.github.ImpactDevelopment.installer.impact;

import io.github.ImpactDevelopment.installer.github.GithubRelease;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.libraries.LibraryBaritone;
import io.github.ImpactDevelopment.installer.libraries.LibraryImpact;
import io.github.ImpactDevelopment.installer.libraries.LibraryMaven;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.BaritoneVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An Impact version that we have the JSON for
 */
public class ImpactJsonVersion {
    public String name; // will always be Impact :wink:
    public String version;
    public String mcVersion;
    public String date;
    public String[] tweakers;
    public ImpactJsonLibrary[] libraries;

    public void printInfo() {
        System.out.println(name);
        System.out.println(version);
        System.out.println(mcVersion);
        System.out.println(date);
        System.out.println(Arrays.toString(tweakers));
        for (ImpactJsonLibrary lib : libraries) {
            System.out.println(lib.name + " " + lib.sha1 + " " + lib.size);
        }
    }

    public List<ILibrary> resolveLibraries(InstallationConfig config) {
        return Stream.of(libraries).map(lib -> {
            String[] parts = lib.name.split(":");
            if (parts.length != 3) {
                throw new IllegalStateException("malformed " + lib.name);
            }
            String name = parts[1];
            if (name.equals(LibraryBaritone.VARIANT)) {
                return config.getSettingValue(BaritoneVersionSetting.INSTANCE);
            }
            if (name.equals(this.name)) {
                GithubRelease release = config.getSettingValue(ImpactVersionSetting.INSTANCE).release;
                if (!release.tagName.equals(version + "-" + mcVersion)) {
                    throw new RuntimeException(release.tagName + " " + version + " " + mcVersion);
                }
                return new LibraryImpact(release, lib);
            }
            return new LibraryMaven(lib);
        }).collect(Collectors.toList());
    }

    public Optional<String> baritoneVersionFilter() {
        return Stream
                .of(libraries)
                .map(lib -> lib.name)
                .map(name -> name.split(":"))
                .filter(parts -> parts[1].equals(LibraryBaritone.VARIANT))
                .map(parts -> parts[2])
                .findFirst();
    }
}
