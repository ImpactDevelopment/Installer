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

import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.github.GithubRelease;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.libraries.LibraryCustomURL;
import io.github.ImpactDevelopment.installer.utils.GPG;

/**
 * A version of Impact that we know about but might not have fetched the actual JSON for yet
 */
public class ImpactVersionReleased extends ImpactVersion {
    private final GithubRelease release;

    public ImpactVersionReleased(GithubRelease release) {
        super(release.tagName);
        this.release = release;
    }

    public ImpactJsonVersion fetchContents() {
        if (fetchedContents == null) {
            if (Installer.args.noGPG) {
                System.out.println("SKIPPING SIGNATURE VERIFICATION DUE TO COMMAND LINE OPTION I HOPE YOU KNOW WHAT YOURE DOING");
            } else {
                System.out.println("Verifying GPG signatures on Impact release " + release.tagName);
                if (!GPG.verifyRelease(release, jsonFileName(), jsonFileName() + ".asc", sigs -> sigs.size() >= 2)) {
                    throw new RuntimeException("Invalid signature on Impact release " + release.tagName);
                }
            }
            fetchedContents = Installer.gson.fromJson(release.byName(jsonFileName()).get().fetch(), ImpactJsonVersion.class);
        }
        sanityCheck();
        return fetchedContents;
    }

    @Override
    public ILibrary resolveSelf(ImpactJsonLibrary entry) {
        sanityCheck(entry);
        return new LibraryCustomURL(entry, release.byName(Installer.project + "-" + release.tagName + ".jar").get().browserDownloadUrl);
    }

    public boolean possiblySigned() {
        return release.byName(jsonFileName() + ".asc").isPresent();
    }
}
