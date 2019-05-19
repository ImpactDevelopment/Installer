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

public class LibraryMaven implements ILibrary {

    private final String name;
    private final String sha1;
    private final int size;

    public LibraryMaven(String name, String sha1, int size) {
        this.name = name;
        this.sha1 = sha1;
        this.size = size;
    }

    public LibraryMaven(ImpactJsonLibrary lib) {
        this(lib.name, lib.sha1, lib.size);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSHA1() {
        return sha1;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getURL() {
        return MavenResolver.getFullURL(name);
    }
}
