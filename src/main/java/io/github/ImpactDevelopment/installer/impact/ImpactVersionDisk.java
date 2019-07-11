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
import io.github.ImpactDevelopment.installer.libraries.LibraryCustomURL;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ImpactVersionDisk extends ImpactVersion {

    private final Path pathToFile;

    public ImpactVersionDisk(Path pathToFile) {
        super(validateAndStrip(pathToFile));
        this.pathToFile = pathToFile;
        if (!pathToFile.getFileName().toString().equals(jsonFileName())) {
            throw new IllegalArgumentException(pathToFile.getFileName().toString() + " " + jsonFileName());
        }
    }

    private static String validateAndStrip(Path pathToFile) {
        String filename = pathToFile.getFileName().toString();
        if (!filename.startsWith(Installer.project + "-") || !filename.endsWith(".json") || filename.split("-").length < 3) {
            throw new IllegalArgumentException("Malformed json name " + filename);
        }
        return filename.split(Installer.project + "-")[1].split("\\.json")[0];
    }

    @Override
    public ImpactJsonVersion fetchContents() {
        if (fetchedContents == null) {
            if (!Installer.args.noGPG) {
                throw new IllegalArgumentException("Unable to verify GPG on local file!");
            }

            try {
                fetchedContents = Installer.gson.fromJson(IOUtils.toString(pathToFile.toFile().toURI(), StandardCharsets.UTF_8), ImpactJsonVersion.class);
            } catch (IOException e) {
                throw new RuntimeException("Unable to load file", e);
            }
        }
        sanityCheck();
        return fetchedContents;
    }

    @Override
    public ILibrary resolveSelf(ImpactJsonLibrary entry) {
        sanityCheck(entry);
        return new LibraryCustomURL(entry, pathToFile.toUri().toString().replace(".json", ".jar"));
    }
}
