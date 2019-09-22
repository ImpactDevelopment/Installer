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

import com.google.gson.reflect.TypeToken;
import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.utils.Fetcher;

import java.util.Map;

public class MavenResolver {
    private static Map<String, String> MAVEN_MAP = null;

    private static Map<String, String> getMavenMap() {
        return Installer.gson.fromJson(Fetcher.fetch("https://impactdevelopment.github.io/Resources/data/maven.refmap.json"), new TypeToken<Map<String, String>>() {}.getType());
    }

    public static String getURLBase(String mavenGroup) {
        if (MAVEN_MAP == null) {
            // don't do this in the class initializer, so that if it fails we don't have a broken class that can't be referenced in the future
            MAVEN_MAP = getMavenMap();
        }
        String ret = MAVEN_MAP.get(mavenGroup);
        if (ret == null) {
            throw new IllegalArgumentException("Can't get URL for maven group " + mavenGroup);
        }
        return ret;
    }

    public static String getFullURL(String mavenName) {
        String[] parts = mavenName.split(":");
        return getURLBase(parts[0]) + partsToPath(parts);
    }

    public static String partsToPath(String[] parts) {
        return parts[0].replace(".", "/") + "/" + parts[1] + "/" + parts[2] + "/" + parts[1] + "-" + parts[2] + ".jar";
    }
}
