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

package io.github.ImpactDevelopment.installer.libraries;

import io.github.ImpactDevelopment.installer.impact.ImpactJsonLibrary;

public class LibraryBaritoneSpecific extends LibraryCustomURL {

    public static final String VARIANT = "baritone-api";

    public LibraryBaritoneSpecific(ImpactJsonLibrary lib) {
        super(lib, urlFromVersion(lib.name.split(":")[2]));
        if (lib.name.contains("*") || !lib.name.startsWith("cabaletta:" + VARIANT + ":")) {
            throw new IllegalStateException(lib.name);
        }
    }

    private static String urlFromVersion(String version) {
        return "https://github.com/cabaletta/baritone/releases/download/v" + version + "/" + VARIANT + "-" + version + ".jar";
    }
}
