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

import io.github.ImpactDevelopment.installer.github.GithubRelease;
import io.github.ImpactDevelopment.installer.impact.ImpactJsonLibrary;

public class LibraryImpact extends LibraryMaven {
    private final GithubRelease release;

    public LibraryImpact(GithubRelease release, ImpactJsonLibrary lib) {
        super(lib);
        String mavenNameExpectedFromRelease = "com.github.ImpactDevelopment:Impact:" + release.tagName;
        if (!mavenNameExpectedFromRelease.equals(getName())) {
            throw new IllegalStateException("Malformed Impact release / json " + mavenNameExpectedFromRelease + " " + getName());
        }
        this.release = release;
    }

    @Override
    public String getURL() {
        // e.g. Impact-4.6-1.13.2.jar
        return release.byName("Impact-" + release.tagName + ".jar").get().browserDownloadUrl;
    }
}
