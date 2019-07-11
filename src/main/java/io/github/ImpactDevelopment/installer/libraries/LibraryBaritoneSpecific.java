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

package io.github.ImpactDevelopment.installer.libraries;

import io.github.ImpactDevelopment.installer.impact.ImpactJsonLibrary;

public class LibraryBaritoneSpecific extends LibraryCustomURL {

    public LibraryBaritoneSpecific(ImpactJsonLibrary lib) {
        super(lib, urlFromVersion(lib.name.split(":")[2]));
        if (lib.name.contains("*") || !lib.name.startsWith("cabaletta:" + LibraryBaritoneReleased.VARIANT + ":")) {
            throw new IllegalStateException(lib.name);
        }
    }

    private static String urlFromVersion(String version) {
        return "https://github.com/cabaletta/baritone/releases/download/v" + version + "/" + LibraryBaritoneReleased.VARIANT + "-" + version + ".jar";
    }
}
