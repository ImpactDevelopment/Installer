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

package io.github.ImpactDevelopment.installer.impact;

import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.libraries.LibraryBaritoneSpecific;
import io.github.ImpactDevelopment.installer.libraries.LibraryMaven;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;

import java.util.Arrays;
import java.util.List;
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
            if (name.equals(LibraryBaritoneSpecific.VARIANT)) {
                if (parts[2].contains("*")) {
                    throw new IllegalStateException(parts[2]);
                }
                return new LibraryBaritoneSpecific(lib);
            }
            if (name.equals(this.name)) {
                return config.getSettingValue(ImpactVersionSetting.INSTANCE).resolveSelf(lib);
            }
            return new LibraryMaven(lib);
        }).collect(Collectors.toList());
    }
}
