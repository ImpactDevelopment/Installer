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

package io.github.ImpactDevelopment.installer;

import static java.util.Locale.ROOT;

/**
 * @author Brady
 * @since 3/7/2019
 */
public enum OperatingSystem {

    WINDOWS,
    OSX,
    LINUX,
    UNKNOWN;

    public static OperatingSystem getOS() {
        String name = System.getProperty("os.name").toLowerCase(ROOT);
        if (name.contains("windows")) {
            return WINDOWS;
        }
        if (name.contains("mac")) {
            return OSX;
        }
        if (name.contains("linux") || name.contains("solaris") || name.contains("sunos") || name.contains("unix")) {
            return LINUX;
        }
        return UNKNOWN;
    }
}
