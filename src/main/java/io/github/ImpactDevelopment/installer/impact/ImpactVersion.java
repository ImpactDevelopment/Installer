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

package io.github.ImpactDevelopment.installer.impact;

import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;

public abstract class ImpactVersion {

    public final String impactVersion;
    public final String mcVersion;

    protected ImpactJsonVersion fetchedContents;

    public ImpactVersion(String combinedName) {
        this.impactVersion = splitReleaseName(combinedName)[0];
        this.mcVersion = splitReleaseName(combinedName)[1];
    }

    public String getCombinedVersion() {
        return impactVersion + "-" + mcVersion;
    }

    private static String[] splitReleaseName(String combined) {
        int pos = combined.lastIndexOf('-');
        return new String[]{combined.substring(0, pos), combined.substring(pos + 1)};
    }

    public abstract ImpactJsonVersion fetchContents();

    public abstract ILibrary resolveSelf(ImpactJsonLibrary entry);

    protected void sanityCheck() {
        // make sure that the json is what it should be
        if (!fetchedContents.mcVersion.equals(mcVersion)) {
            throw new IllegalStateException(fetchedContents.mcVersion + " " + mcVersion);
        }
        if (!fetchedContents.version.equals(impactVersion)) {
            throw new IllegalStateException(fetchedContents.version + " " + impactVersion);
        }
        if (!fetchedContents.name.equals(Installer.project)) {
            throw new IllegalStateException(fetchedContents.name);
        }
    }

    protected void sanityCheck(ImpactJsonLibrary self) {
        if (!self.name.equals("com.github.ImpactDevelopment:Impact:" + getCombinedVersion())) {
            throw new IllegalStateException(self.name + " " + getCombinedVersion());
        }
    }

    protected String jsonFileName() {
        return Installer.project + "-" + getCombinedVersion() + ".json";
    }
}
