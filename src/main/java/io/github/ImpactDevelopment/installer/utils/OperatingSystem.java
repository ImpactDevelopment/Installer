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

package io.github.ImpactDevelopment.installer.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    public static Path getDownloads() {
        if (getOS() == OperatingSystem.LINUX) {
            String xdg = System.getenv("XDG_DOWNLOAD_DIR");
            if (!xdg.isEmpty()) return Paths.get(xdg);
        }
        return getHome().resolve("Downloads");
    }

    public static Path getHome() {
        return Paths.get(System.getProperty("user.home"));
    }
}
