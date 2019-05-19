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

/**
 * A library that can be added to the final launcher json
 */
public interface ILibrary {
    String getName(); // e.g. "com.github.ImpactDevelopment:Impact:4.6-1.13.2"

    String getSHA1(); // e.g. 6dc748bbc1cabac3dbbabd8abce0b0859162ca85

    int getSize(); // e.g. 5335535

    String getURL();
}
